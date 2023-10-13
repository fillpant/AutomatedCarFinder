package click.nullpointer.carsearch.ebay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;

public class EbaySearcher implements ICarSearcher {

	private final int categoryId;
	private final String searchQuerry;
	private final String appKey;
	private final String[] modelYears;
	private final double minPrice, maxPrice;
	private final String referencePostcode;

	public EbaySearcher(EbaySearcherConfig c) {
		this.categoryId = c.getCategoryId();
		this.searchQuerry = c.getSearchQuerry();
		this.appKey = c.getAppKey();
		this.modelYears = c.getModelYears();
		this.minPrice = c.getMinPrice();
		this.maxPrice = c.getMaxPrice();
		this.referencePostcode = c.getReferencePostcode();
	}

	@Override
	public Collection<AbstractCarListing> searchForListings() throws IOException {
		Gson g = new GsonBuilder().registerTypeHierarchyAdapter(EbaySearchListing.class, new EbaySearchListingParser())
				.create();
		List<AbstractCarListing> l = new ArrayList<>();
		l.addAll(recursivelyGetAllResults(g, 1));
		l.forEach(a -> a.setSearcher(this));
		return l;
	}

	@Override
	public String getPrettySearcherName() {
		return "🚗 eBay Crawler";

	}

//	private static final Map<Long, EbaySearchListing> previousResults = new HashMap<>();
//	private static final Pattern CAT_SCRAP = Pattern.compile("cat(egory)? [absn]{1} ", Pattern.CASE_INSENSITIVE);
//
//	public static void doTheFunThing() {
//		try {
//			Gson g = new GsonBuilder()
//					.registerTypeHierarchyAdapter(EbaySearchListing.class, new EbaySearchListingParser()).create();
//			List<EbaySearchListing> results = recursivelyGetAllResults(g, 1);
//			results.removeIf(a -> a.getTitle().toLowerCase().contains("discovery sport"));
//
//			System.out.println(results.size() + " results.\n");
//
//			// Check what's new
//			Map<EbaySearchListing, EbaySearchListing> theNew = results.stream()
//					.filter(a -> !previousResults.containsKey(a.getItemId()))
//					.collect(HashMap::new, (m, v) -> m.put(v, null), HashMap::putAll);
//			System.out.println("New listings: " + theNew.size());
//			printListings(theNew.keySet());
//
//			// Check if any changed price
//			Map<EbaySearchListing, EbaySearchListing> priceChange = results.stream()//
//					.filter(a -> previousResults.containsKey(a.getItemId()))//
//					.filter(a -> previousResults.get(a.getItemId()).getCurrentPrice() != a.getCurrentPrice())
//					.collect(Collectors.toMap(a -> a, a -> previousResults.get(a.getItemId())));
//			System.out.println("Price change: " + priceChange.size());
//			printListings(priceChange.keySet());
//
//			// Check what is not in the list anymore
//			Map<EbaySearchListing, EbaySearchListing> soldOrGone = previousResults.values().stream()
//					.filter(o -> !results.stream().filter(a -> a.getItemId() == o.getItemId()).findAny().isPresent())
//					.collect(HashMap::new, (m, v) -> m.put(v, null), HashMap::putAll);
//			System.out.println("Gone listings: " + soldOrGone.size());
//			printListings(soldOrGone.keySet());
//
//			// Notifications!
//			if (!theNew.isEmpty())
//				sendNotifications(theNew, NotificationType.NEW_LISTING);
//			if (!priceChange.isEmpty())
//				sendNotifications(priceChange, NotificationType.PRICE_CHANGED);
//			if (!soldOrGone.isEmpty())
//				sendNotifications(soldOrGone, NotificationType.LISTING_GONE);
//
//			// Update previous results
//			Map<Long, EbaySearchListing> newResults = results.stream()
//					.collect(Collectors.toMap(a -> a.getItemId(), a -> a));
//			previousResults.clear();
//			previousResults.putAll(newResults);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// new->old lst
//	private static void sendNotifications(Map<EbaySearchListing, EbaySearchListing> lst, NotificationType type) {
//		try {
//			for (EbaySearchListing l : lst.keySet())
//				new URL(String.format(Main.NOTIFICATION_URL, URLEncoder.encode("🚙 eBay -- " + type.toTitle(), "UTF-8"),
//						URLEncoder.encode(listingToNotificationText(l, lst.get(l)), "UTF-8"),
//						URLEncoder.encode(l.getViewItemURL(), "UTF-8"))).openStream().close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static String listingToNotificationText(EbaySearchListing l, EbaySearchListing old) {
//		StringBuilder sb = new StringBuilder();
//		String title = l.getTitle();
//		if (title.length() > 42)
//			title = title.substring(0, 39) + "...";
//		if (containsBadWords(l.getTitle() + " " + l.getSubtitle()))
//			title = "⚠️⚠️" + title;
//		String subtitle = l.getSubtitle();
//		if (subtitle.length() > 42)
//			subtitle = subtitle.substring(0, 39) + "...";
//
//		sb.append(title).append('\n');
//		if (!l.getSubtitle().trim().isEmpty())
//			sb.append(subtitle).append('\n');
//		sb.append("💰 " + String.format("£%,.2f", l.getCurrentPrice()));
//		if (old != null && old.getCurrentPrice() != l.getCurrentPrice())
//			sb.append(" " + styleChange(l.getCurrentPrice(), old.getCurrentPrice()));
//		sb.append('\n');
//		sb.append("🤓 " + l.getWatchCount() + " watches").append('\n');
//		Duration durLeft = l.getTimeLeft();
//		long dh = durLeft.getSeconds() / 3600;
//		long dm = (durLeft.getSeconds() % 3600) / 60;
//		long ds = durLeft.getSeconds() % 60;
//		String left = String.format("%d:%02d:%02d", dh, dm, ds);
//		sb.append("⌚ " + left).append('\n');
//		sb.append("🏁 " + l.getEndTime().toString().replace("T", " "));
//		sb.append("🧮 Type: " + l.getListingType());
//		return sb.toString();
//	}
//
//	private static boolean containsBadWords(String title) {
//		title = title.toLowerCase();
//		title = title.replaceAll(" +", " ");
//		boolean damage = title.contains("damage") && !title.contains("no damage") && !title.contains("damage free");
//		boolean accident = title.contains("crash") && !title.contains("no crash") && !title.contains("crash free");
//		boolean salvage = title.contains("salvage");
//		boolean catABSN = CAT_SCRAP.matcher(title).find();
//		boolean scrap = title.contains("scrap") || title.contains("write-off") || title.contains("writeoff")
//				|| title.contains("written off");
//		boolean rubbish = title.contains("upgrade only");
//
//		return damage | accident | salvage | catABSN | scrap | rubbish;
//	}
//
//	private static String styleChange(double current, double old) {
//		double diff = current - old;
//		if (diff == 0) {
//			return "";
//		} else if (diff < 0) {
//			return "👇 " + (int) Math.abs(diff);
//		} else {
//			return "☝️ " + (int) diff;
//		}
//	}
//
//	private static void printListings(Collection<EbaySearchListing> results) {
//		results.forEach(a -> {
//			System.out.println("\nIs Good? " + !containsBadWords(a.getTitle() + " " + a.getSubtitle()));
//			System.out.println(a.getTitle());
//			System.out.println(a.getSubtitle());
//			System.out.println(a.getItemId());
//			System.out.println("\tPrice: " + a.getCurrentPrice() + " " + a.getCurrency());
//			System.out.println("\tEnd time: " + a.getEndTime());
//		});
//	}
//
	private List<EbaySearchListing> recursivelyGetAllResults(final Gson g, int page) throws IOException {
		System.err.println("GET Page " + page);
		JsonObject obj = g.fromJson(searchForLandRover(page), JsonObject.class);
		obj = obj.getAsJsonArray("findItemsAdvancedResponse").get(0).getAsJsonObject();
		EbaySearchListing[] listings = g.fromJson(
				obj.getAsJsonArray("searchResult").get(0).getAsJsonObject().getAsJsonArray("item"),
				EbaySearchListing[].class);

		int totalPages = obj.getAsJsonArray("paginationOutput").get(0).getAsJsonObject().getAsJsonArray("totalPages")
				.get(0).getAsInt();
		if (page >= totalPages) {
			return new ArrayList<>(Arrays.asList(listings));
		} else {
			List<EbaySearchListing> e = recursivelyGetAllResults(g, page + 1);
			e.addAll(Arrays.asList(listings));
			return e;
		}

	}

	private String searchForLandRover(int page) throws IOException {
//		String apiKey = "PhiPante-Searcher-PRD-292d3cfae-f1359560"; // Replace with your sandbox API key
//		String keywords = "Discovery 4 -(sport,damaged,writeoff,salvage,write-off,crashed,broken)";
//		String[] modelYears = { "2014", "2015", "2016" };
//		String categoryId = "29748";// Search for an example item, choose the category, look at the URL and you will
//									// find the /sch/<ID>/i.html part where ID is this.
		String apiUrl = "https://svcs.ebay.com/services/search/FindingService/v1?" //
				+ "OPERATION-NAME=findItemsAdvanced&" //
				+ "SERVICE-VERSION=1.0.0&" //
				+ "SECURITY-APPNAME=" + appKey + "&"//
				+ "RESPONSE-DATA-FORMAT=JSON&" //
				+ "keywords=" + URLEncoder.encode(searchQuerry, "utf-8") + "&" //
				+ "categoryId=" + categoryId + "&" //
				+ "GLOBAL-ID=EBAY-GB&"//
				+ "buyerPostalCode=" + referencePostcode + "&"//
				+ "paginationInput.pageNumber=" + page + "&"//
				+ "itemFilter(0).name=MinPrice&itemFilter(0).value=" + minPrice + "&"
				+ "itemFilter(0).paramName=Currency&itemFilter(0).paramValue=GBP&"
				+ "itemFilter(1).name=MaxPrice&itemFilter(1).value=" + maxPrice + "&"
				+ "itemFilter(1).paramName=Currency&itemFilter(1).paramValue=GBP&";
		int aspectCnt = 0;
		for (String s : modelYears)
			apiUrl += "aspectFilter(" + aspectCnt + ").aspectName=Model+Year&aspectFilter(" + aspectCnt++
					+ ").aspectValueName=" + s + "&";

		String response = makeGET(apiUrl);
		return response;
	}

	private static String makeGET(String toUrl) throws IOException {
		URL url = new URL(toUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		int responseCode = connection.getResponseCode();
		StringBuilder response = new StringBuilder();
		InputStream readFrom = responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream()
				: connection.getErrorStream();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(readFrom))) {
			br.lines().forEach(response::append);
		}
		if (responseCode != HttpURLConnection.HTTP_OK) {
			System.err.println("Got code: " + responseCode);
		}
		return response.toString();
	}
}
