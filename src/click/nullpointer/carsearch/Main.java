package click.nullpointer.carsearch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import click.nullpointer.carsearch.autotrader.AutotraderSearcher;
import click.nullpointer.carsearch.autotrader.AutotraderSearcherConfig;
import click.nullpointer.carsearch.autotrader.NotificationType;
import click.nullpointer.carsearch.cargurus.CarGurusConfig;
import click.nullpointer.carsearch.cargurus.CarGurusSearcher;
import click.nullpointer.carsearch.ebay.EbaySearcher;
import click.nullpointer.carsearch.ebay.EbaySearcherConfig;
import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;
import click.nullpointer.carsearch.model.ISearcherConfiguration;
import click.nullpointer.carsearch.motorscouk.MotorsCoUkConfig;
import click.nullpointer.carsearch.motorscouk.MotorsCoUkSearcher;
import click.nullpointer.carsearch.rac.RACConfig;
import click.nullpointer.carsearch.rac.RACSearcher;
import click.nullpointer.carsearch.theaa.TheAAConfig;
import click.nullpointer.carsearch.theaa.TheAASearcher;

public class Main {
	private static final Gson CONFIG_GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
//	public static final String NOTIFICATION_URL = "https://maker.ifttt.com/trigger/notify/with/key/nZ7AUfIHSFPuvwatJfFdHLCNckGKKlzCPvYpOnAnoOU?value1=%s&value2=%s&value3=%s";
//	public static final int SLEEP_BETWEEN_REQUESTS_MS = 1000;
//	private static final int RUN_EVERY_MINS = 67 * 3;// Offset so it's not exactly 6h between searches.
	private static MainSearcherConfig config;
	private static ActionRateLimiter rateLimitedNotifications;

	// Map searcher class to corresponding config class
	private static final Map<Class<?>, ISearcherConfiguration> DEFAULT_CONFIG_MAPPING = new HashMap<>();
	private static final Map<String, AbstractCarListing> previousResults = new HashMap<>();

	public static void main(String[] args) throws IOException, ReflectiveOperationException {
		// Load config
		File cnf = new File("config.json");
		if (!cnf.exists()) {
			config = new MainSearcherConfig();
			Files.write(cnf.toPath(), Arrays.asList(CONFIG_GSON.toJson(config)), StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.CREATE_NEW);
		} else {
			try (FileReader fr = new FileReader(cnf)) {
				config = CONFIG_GSON.fromJson(fr, MainSearcherConfig.class);
			}
		}

		// Initialise rate limiter
		rateLimitedNotifications = new ActionRateLimiter(config.getNotificationsPerMinute(), TimeUnit.MINUTES);

		// Initialise default searches:
		DEFAULT_CONFIG_MAPPING.put(EbaySearcher.class,
				new EbaySearcherConfig(29748, "Discovery 4 -(sport,damaged,writeoff,salvage,write-off,crashed,broken)",
						"PhiPante-Searcher-PRD-292d3cfae-f1359560", new String[] { "2014", "2015", "2016" }, 0.01,
						10000, "sa16hw"));
		DEFAULT_CONFIG_MAPPING.put(AutotraderSearcher.class, new AutotraderSearcherConfig("Land Rover", "Discovery 4",
				2014, 10000, "sa16hw", config.getSleepBetweenRequests()));

		DEFAULT_CONFIG_MAPPING.put(MotorsCoUkSearcher.class,
				new MotorsCoUkConfig("Land Rover", "Discovery 4", 2014, 2017, 15000, "SA16RA"));

		DEFAULT_CONFIG_MAPPING.put(CarGurusSearcher.class, new CarGurusConfig(2014, 15000, "SA1 6RA"));

		DEFAULT_CONFIG_MAPPING.put(TheAASearcher.class, new TheAAConfig("SA16RA", 0, 16000, 9, 102, 121));

		DEFAULT_CONFIG_MAPPING.put(RACSearcher.class, new RACConfig(10, 16000, "SA16RA"));

		// Load configs for searchers
		Map<Class<?>, ISearcherConfiguration> configs = loadOrCreateConfigs();
		List<ICarSearcher> searchers = new ArrayList<>();
		for (Class<?> searcher : configs.keySet()) {
			Constructor<? extends ICarSearcher> c = (Constructor<? extends ICarSearcher>) searcher
					.getDeclaredConstructor(configs.get(searcher).getClass());
			searchers.add(c.newInstance(configs.get(searcher)));
		}
		System.out.println(searchers.size() + " searchers enabled.");

		ScheduledExecutorService sex = Executors.newScheduledThreadPool(1);
		sex.scheduleAtFixedRate(() -> {
			ZonedDateTime z = ZonedDateTime.now(config.getTimezone());
			if (z.getHour() < config.getStartSearchingAtHour() && z.getHour() > config.getStopSearchingAtHour()) {
				System.out.println("Sleep time. No searches at this point.");
				return;
			}
			doFunThings(searchers);
		}, 0, config.getRunEverySeconds(), TimeUnit.MINUTES);
	}

//	private static Map<String, AbstractCarListing> loadPreviousResults(File file)
//			throws IOException, ClassNotFoundException {
//		if (!file.exists())
//			return Collections.emptyMap();
//		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//			return (Map<String, AbstractCarListing>) ois.readObject();
//		}
//	}
//
//	private static void writePreviousResults(File file, Map<String, AbstractCarListing> val)
//			throws IOException, ClassNotFoundException {
//		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
//			oos.writeObject(val);
//		}
//	}

	//
	private static Map<Class<?>, ISearcherConfiguration> loadOrCreateConfigs()
			throws IOException, ReflectiveOperationException {
		// Searcher class to searcher config
		Map<Class<?>, ISearcherConfiguration> configs = new HashMap<>();
		for (Class<?> c : DEFAULT_CONFIG_MAPPING.keySet()) {
			File f = new File(c.getSimpleName() + ".json");
			if (!f.exists()) {
				writeConfig(DEFAULT_CONFIG_MAPPING.get(c), c);
				configs.put(c, DEFAULT_CONFIG_MAPPING.get(c));
			} else {
				configs.put(c, loadConfig(f));
			}
		}
		return configs;
	}

	private static ISearcherConfiguration loadConfig(File f) throws IOException, ReflectiveOperationException {
		String json = Files.readAllLines(f.toPath()).stream().collect(Collectors.joining("\n"));
		JsonObject ob = CONFIG_GSON.fromJson(json, JsonObject.class);
		Class<?> conf = Class.forName(ob.get("__configClass").getAsString());
		return (ISearcherConfiguration) CONFIG_GSON.fromJson(ob, conf);
	}

	private static void writeConfig(ISearcherConfiguration cnf, Class<?> searcher) throws IOException {
		JsonObject e = CONFIG_GSON.toJsonTree(cnf).getAsJsonObject();
		e.add("__configClass", CONFIG_GSON.toJsonTree(cnf.getClass().getCanonicalName()));
		File out = new File(searcher.getSimpleName() + ".json");
		try (PrintWriter pw = new PrintWriter(out)) {
			pw.write(CONFIG_GSON.toJson(e));
		}
	}

	public static void doFunThings(List<ICarSearcher> searchers) {
		Map<String, AbstractCarListing> listings = new HashMap<>();
		for (ICarSearcher s : searchers) {
			System.out.println(">>>>SEARCH " + s.getPrettySearcherName() + ": ");
			try {
				Collection<AbstractCarListing> l = s.searchForListings();
				System.out.println("\t>>>>" + l.size() + " LISTINGS RECEIVED.");
				l.forEach(a -> listings.put(s.getClass().toString() + a.getListingUniqueID(), a));
				l.forEach(a -> a.getPictureURLs());
			} catch (Exception e) {
				notifyException("Search", s.getPrettySearcherName(), e);
				e.printStackTrace();
			}
		}

//		registerImagesFromListings(listings);

		// Check what's new
		Map<AbstractCarListing, AbstractCarListing> theNew = listings.entrySet().stream()
				.filter(a -> !previousResults.containsKey(a.getKey()))
				.collect(HashMap::new, (m, v) -> m.put(v.getValue(), null), HashMap::putAll);
		System.out.println("New listings: " + theNew.size());
//			printListings(theNew.keySet());

		// Check if any changed price
		Map<AbstractCarListing, AbstractCarListing> priceChange = listings.entrySet().stream()//
				.filter(a -> previousResults.containsKey(a.getKey()))//
				.filter(a -> a.getValue().getPrice() != previousResults.get(a.getKey()).getPrice())
				.collect(Collectors.toMap(a -> a.getValue(), a -> previousResults.get(a.getKey())));
		System.out.println("Price change: " + priceChange.size());
//			printListings(priceChange.keySet());

		// Check what is not in the list anymore
		Map<AbstractCarListing, AbstractCarListing> soldOrGone = previousResults.entrySet().stream()
				.filter(o -> !listings.containsKey(o.getKey()))
				.collect(HashMap::new, (m, v) -> m.put(v.getValue(), null), HashMap::putAll);
		System.out.println("Gone listings: " + soldOrGone.size());
//			printListings(soldOrGone.keySet());

		// Export the simple list!
		try {
			List<AbstractCarListing> ls = listings.values().stream().collect(Collectors.toList());
			if (!config.getSimpleWebpageViewFile().exists())
				FileUtils.writeStringToFile(config.getSimpleWebpageViewFile(),
						SimpleHTMLCarDetailsExporter.getFrontEndView(), Charset.defaultCharset());
			FileUtils.writeStringToFile(new File("cars.json"), SimpleHTMLCarDetailsExporter.getJsonPayloadForView(ls),
					Charset.defaultCharset());
		} catch (Exception e) {
			notifyException("Simple Webpage Creation", null, e);
			e.printStackTrace();
		}
//		 Notifications!
		if (!theNew.isEmpty())
			sendNotifications(theNew, NotificationType.NEW_LISTING);
		if (!priceChange.isEmpty())
			sendNotifications(priceChange, NotificationType.PRICE_CHANGED);
		if (!soldOrGone.isEmpty())
			sendNotifications(soldOrGone, NotificationType.LISTING_GONE);

		previousResults.clear();
		previousResults.putAll(listings);

	}

//	private static void registerImagesFromListings(Map<String, AbstractCarListing> listings) {
//		MessageDigest md = MessageDigest.getInstance("MD5");
//		listings.values().stream().map(a -> {
//			List<String> imgs = a.getPictureURLs();
//			List<String> hashes = new ArrayList<>(imgs.size());
//			imgs.forEach(x -> hashes.put);
//			return imgs;
//		});
//	}

	private static void sendNotifications(Map<AbstractCarListing, AbstractCarListing> lst, NotificationType type) {
		try {
			for (AbstractCarListing l : lst.keySet()) {
				String searcher = l.getSearcher().isPresent() ? l.getSearcher().get().getPrettySearcherName()
						: "Generic searcher";
				String notifText = l.toNotificationText();
				if (lst.get(l) != null) {
					if (lst.get(l).getPrice() > l.getPrice())
						notifText += ("\n📉 " + (lst.get(l).getPrice() - l.getPrice()));
					else if (lst.get(l).getPrice() < l.getPrice())
						notifText += ("\n📈 " + (l.getPrice() - lst.get(l).getPrice()));
				}
				final String notifTextF = notifText;
				rateLimitedNotifications.submitAction(() -> {
					try {
						new URL(String.format(config.getNotificationURL(),
								URLEncoder.encode(searcher + " | " + type.toTitle(), "UTF-8"),
								URLEncoder.encode(notifTextF, "UTF-8"), URLEncoder.encode(l.getListingURL(), "UTF-8")))
										.openStream().close();
						System.out.println(notifTextF + "\n\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void notifyException(String action, String searcher, Exception e) {

		String trace = "";
		for (StackTraceElement l : e.getStackTrace())
			trace += l.toString() + "\n";
		String traceF = trace;
		rateLimitedNotifications.submitAction(() -> {
			try {
				new URL(String.format(Main.config.getNotificationURL(),
						URLEncoder.encode("☢️ ‼️Exception‼️ 🧯 " + (searcher == null ? "" : searcher + " ") + "☣️",
								"UTF-8"),
						URLEncoder.encode("▶️ Action: " + action + "\n💔 Exception: " + e.getClass().toString() + ": "
								+ e.getMessage() + "\n🔻 " + traceF, "UTF-8"),
						"")).openStream().close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

}
