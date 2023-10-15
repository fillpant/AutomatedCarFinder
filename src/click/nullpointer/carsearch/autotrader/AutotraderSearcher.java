package click.nullpointer.carsearch.autotrader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;

public class AutotraderSearcher implements ICarSearcher {

//	private static final String MAKE = "Land Rover";
//	private static final String MODEL = "Discovery 4";
//	private static final int MIN_YEAR = 2014;
//	private static final int MAX_PRICE = 12000;
//	private static final String POSTCODE_REFERENCE = "sa16hw";
	private final int sleepFor;
	private final String make;
	private final String model;
	private final int minYear;
	private final int maxPrice;
	private final String referencePostcode;

	///
//	private static final Map<Long, AutotraderSearchListings> previousResults = new HashMap<>();

	public AutotraderSearcher(AutotraderSearcherConfig config) {
		this.make = config.getMake();
		this.model = config.getModel();
		this.minYear = config.getMinYear();
		this.maxPrice = config.getMaxPrice();
		this.referencePostcode = config.getReferencePostcode();
		this.sleepFor = config.getSleepForMsBetweenRequests();
	}

	@Override
	public Collection<AbstractCarListing> searchForListings() throws IOException {
		List<AbstractCarListing> ls = new ArrayList<>();
		ls.addAll(getAllResultsForRecursive(1, make, model, minYear, maxPrice, referencePostcode, sleepFor));
		ls.removeIf(a -> !((AutotraderSearchListings) a).type.equals("NATURAL_LISTING"));
		ls.forEach(a -> a.setSearcher(this));
		return ls;
	}

	@Override
	public String getPrettySearcherName() {
		return "🚙 AutoTrader Crawler";
	}

//	public static void doTheFunThing() {
//		try {
//			List<AutotraderSearchListings> results = getAllResultsForRecursive(1, MAKE, MODEL, MIN_YEAR, MAX_PRICE,
//					POSTCODE_REFERENCE, Main.SLEEP_BETWEEN_REQUESTS_MS);
//			results.removeIf(a -> !a.type.equals("NATURAL_LISTING"));
//			System.out.println(results.size() + " results.\n");
//
//			// Check what's new
//			Map<AutotraderSearchListings, AutotraderSearchListings> theNew = results.stream()
//					.filter(a -> !previousResults.containsKey(a.advertId))
//					.collect(HashMap::new, (m, v) -> m.put(v, null), HashMap::putAll);
//			System.out.println("New listings: " + theNew.size());
//			printListings(theNew.keySet());
//
//			// Check if any changed price
//			Map<AutotraderSearchListings, AutotraderSearchListings> priceChange = results.stream()//
//					.filter(a -> previousResults.containsKey(a.advertId))//
//					.filter(a -> a.tryParsePrice().isPresent() && !previousResults.get(a.advertId).tryParsePrice()
//							.orElse(-1).equals(a.tryParsePrice().orElse(-1)))
//					.collect(Collectors.toMap(a -> a, a -> previousResults.get(a.advertId)));
//			System.out.println("Price change: " + priceChange.size());
//			printListings(priceChange.keySet());
//
//			// Check what is not in the list anymore
//			Map<AutotraderSearchListings, AutotraderSearchListings> soldOrGone = previousResults.values().stream()
//					.filter(o -> !results.stream().filter(a -> a.advertId == o.advertId).findAny().isPresent())
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
//			Map<Long, AutotraderSearchListings> newResults = results.stream()
//					.collect(Collectors.toMap(a -> a.advertId, a -> a));
//			previousResults.clear();
//			previousResults.putAll(newResults);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static void printListings(Collection<AutotraderSearchListings> results) {
//		results.forEach(a -> {
//			System.out.println(a.title);
//			System.out.println(a.subTitle);
//			System.out.println(a.advertId);
//			System.out.println("\tPrice: " + a.tryParsePrice().orElse(-1));
//			System.out.println("\tMileage: " + a.tryParseMileage().orElse(-1));
//			System.out.println("\tTransmission: " + a.tryGetTransmission().orElse("???"));
//			System.out.println("\tPrevious Owners: " + a.tryExtractOwnerCnt().orElse(-1));
//			System.out.println("\tURL: https://www.autotrader.co.uk" + a.fpaLink);
//		});
//	}
//
//	// new->old lst
//	private static void sendNotifications(Map<AutotraderSearchListings, AutotraderSearchListings> lst,
//			NotificationType type) {
//		try {
//			for (AutotraderSearchListings l : lst.keySet())
//				new URL(String.format(Main.NOTIFICATION_URL,
//						URLEncoder.encode("🚗 AutoTrader -- " + type.toTitle(), "UTF-8"),
//						URLEncoder.encode(listingToNotificationText(l, lst.get(l)), "UTF-8"),
//						URLEncoder.encode("https://www.autotrader.co.uk" + l.fpaLink, "UTF-8"))).openStream().close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static String listingToNotificationText(AutotraderSearchListings l, AutotraderSearchListings old) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(l.title).append('\n');
//		if (l.subTitle != null && !l.subTitle.trim().isEmpty())
//			sb.append(l.subTitle).append('\n');
//		sb.append("💰 " + l.price);
//		if (old != null && l.tryParsePrice().isPresent() && old.tryParsePrice().isPresent()
//				&& l.tryParsePrice().orElse(-1) != old.tryParsePrice().orElse(-1))
//			sb.append(" " + styleChange(l.tryParsePrice().orElse(-1), old.tryParsePrice().orElse(-1)));
//		sb.append('\n');
//		int miles = l.tryParseMileage().orElse(-1);
//		int km = (int) (miles * 1.609344);
//		sb.append("📏 " + (miles == -1 ? l.mileageText : (String.format("%,d miles (%,d km", miles, km))));
//		sb.append('\n');
//		sb.append("📖 Year " + (l.tryParseYear().isPresent() ? l.tryParseYear().get() : l.yearAndPlateText))
//				.append('\n');
//		if (l.tryGetTransmission().isPresent())
//			sb.append("📍 " + l.tryGetTransmission().get()).append('\n');
//		if (l.tryExtractOwnerCnt().isPresent())
//			sb.append("🧍 " + l.tryExtractOwnerCnt().get()).append(" owners").append('\n');
//		sb.append("📸 " + l.numberOfImages);
//		return sb.toString();
//	}
//
//	private static String styleChange(int current, int old) {
//		int diff = current - old;
//		if (diff == 0) {
//			return "";
//		} else if (diff < 0) {
//			return "👇 " + (int) Math.abs(diff);
//		} else {
//			return "☝️ " + diff;
//		}
//	}

	public static List<AutotraderSearchListings> getAllResultsForRecursive(int page, final String make,
			final String model, final int minYear, final int maxPrice, final String postcode, final long sleepFor)
			throws IOException {
		System.err.println("GET page " + page);
		String request = getQuerryText(make, model, minYear, maxPrice, postcode, page);
		String result = getPOSTResponse(
				"https://www.autotrader.co.uk/at-gateway?opname=SearchResultsFacetsQuery&opname=SearchResultsListingsQuery",
				request, "https://www.autotrader.co.uk", "application/json");

		Gson g = new Gson();
		JsonArray whatAMess = g.fromJson(result, JsonArray.class);
		JsonObject searchResults = null;
		JsonArray listings = null;
		for (JsonElement e : whatAMess) {
			if (e.isJsonObject()) {
				JsonObject o = e.getAsJsonObject();
				if (o.has("data") && o.get("data").isJsonObject()) {
					o = o.getAsJsonObject("data");
					if (o.has("searchResults") && o.get("searchResults").isJsonObject()) {
						o = o.getAsJsonObject("searchResults");
						if (o.has("listings") && o.get("listings").isJsonArray()) {
							listings = o.getAsJsonArray("listings");
							searchResults = o;
						}
					}
				}
			}
		}

		if (listings == null) {
			throw new IllegalStateException("FAILED to find listings!");
		}

		AutotraderSearchListings[] lst = g.fromJson(listings, AutotraderSearchListings[].class);
		JsonObject pageInfo = searchResults.getAsJsonObject("page");
		if (page >= pageInfo.get("count").getAsInt()) {
			return new ArrayList<>(Arrays.asList(lst));
		} else {
			try {
				Thread.sleep(sleepFor);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<AutotraderSearchListings> l = getAllResultsForRecursive(page + 1, make, model, minYear, maxPrice,
					postcode, sleepFor);
			l.addAll(Arrays.asList(lst));
			return l;
		}
	}

	public static String getPOSTResponse(String targetUrl, String postData, String origin, String contentType)
			throws IOException {
//		System.out.println(postData);
//		System.exit(0);
		URL url = new URL(targetUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Set request method to POST
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Accept-Language", "en");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setRequestProperty("Content-Type", contentType);
		connection.setRequestProperty("Dnt", "1");
		connection.setRequestProperty("Origin", origin);
		connection.setRequestProperty("Sec-Ch-Ua",
				"\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"");
		connection.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
		connection.setRequestProperty("Sec-Ch-Ua-Platform", "Windows");
		connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
		connection.setRequestProperty("Sec-Fetch-Dest", "empty");
		connection.setRequestProperty("Sec-Fetch-Mode", "cors");
		connection.setRequestProperty("Sec-Fetch-Site", "same-origin");
		connection.setRequestProperty("Sec-Gpc", "1");
		connection.setRequestProperty("X-Sauron-App-Name", "sauron-search-results-app");
		connection.setRequestProperty("X-Sauron-App-Version", "19fdd606d7");
		connection.setDoOutput(true);
		connection.setDoInput(true);

		try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
			outputStream.writeBytes(postData);
			outputStream.flush();
		}

		int responseCode = connection.getResponseCode();
		StringBuilder response = new StringBuilder();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream inputStream = connection.getInputStream();
			String contentEncoding = connection.getHeaderField("Content-Encoding");
			if ("gzip".equals(contentEncoding)) {
				inputStream = new GZIPInputStream(inputStream);
			}

			try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}
		} else {
			throw new IOException("POST request failed with response code: " + responseCode);
		}

		connection.disconnect();

		return response.toString();
	}

	private static String getQuerryText(String make, String model, int minYear, int maxPrice, String postcode,
			int page) {
		//@formatter:off
		return "[\n"
				+ "  {\n"
				+ "    \"operationName\": \"SearchResultsListingsQuery\",\n"
				+ "    \"variables\": {\n"
				+ "      \"filters\": [\n"
				+ "        {\n"
				+ "          \"filter\": \"home_delivery_adverts\",\n"
				+ "          \"selected\": [\n"
				+ "            \"include\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"make\",\n"
				+ "          \"selected\": [\n"
				+ "            \"Land Rover\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"max_price\",\n"
				+ "          \"selected\": [\n"
				+ "            \"15000\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"min_year_manufactured\",\n"
				+ "          \"selected\": [\n"
				+ "            \"2014\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"model\",\n"
				+ "          \"selected\": [\n"
				+ "            \"Discovery 4\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"postcode\",\n"
				+ "          \"selected\": [\n"
				+ "            \"sa28pp\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"price_search_type\",\n"
				+ "          \"selected\": [\n"
				+ "            \"total\"\n"
				+ "          ]\n"
				+ "        }\n"
				+ "      ],\n"
				+ "      \"channel\": \"cars\",\n"
				+ "      \"page\": 1,\n"
				+ "      \"sortBy\": \"relevance\",\n"
				+ "      \"listingType\": null\n"
				+ "    },\n"
				+ "    \"query\": \"fragment fragmentListings on GQLListing {\\n  ... on SearchListing {\\n    type\\n    advertId\\n    title\\n    subTitle\\n    attentionGrabber\\n    price\\n    bodyType\\n    viewRetailerProfileLinkLabel\\n    approvedUsedLogo\\n    nearestCollectionLocation: collectionLocations(limit: 1) {\\n      distance\\n      town\\n      __typename\\n    }\\n    distance\\n    discount\\n    description\\n    images\\n    location\\n    numberOfImages\\n    priceIndicatorRating\\n    rrp\\n    manufacturerLogo\\n    sellerType\\n    sellerName\\n    dealerLogo\\n    dealerLink\\n    dealerReview {\\n      overallReviewRating\\n      numberOfReviews\\n      dealerProfilePageLink\\n      __typename\\n    }\\n    fpaLink\\n    hasVideo\\n    has360Spin\\n    hasDigitalRetailing\\n    mileageText\\n    yearAndPlateText\\n    specs\\n    finance {\\n      monthlyPrice {\\n        priceFormattedAndRounded\\n        __typename\\n      }\\n      quoteSubType\\n      representativeExample\\n      __typename\\n    }\\n    badges {\\n      type\\n      displayText\\n      __typename\\n    }\\n    sellerId\\n    trackingContext {\\n      retailerContext {\\n        id\\n        __typename\\n      }\\n      advertContext {\\n        id\\n        advertiserId\\n        advertiserType\\n        make\\n        model\\n        vehicleCategory\\n        year\\n        condition\\n        price\\n        __typename\\n      }\\n      card {\\n        category\\n        subCategory\\n        __typename\\n      }\\n      advertCardFeatures {\\n        condition\\n        numImages\\n        hasFinance\\n        priceIndicator\\n        isManufacturedApproved\\n        isFranchiseApproved\\n        __typename\\n      }\\n      distance {\\n        distance\\n        distance_unit\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  ... on GPTListing {\\n    type\\n    targetingSegments {\\n      name\\n      values\\n      __typename\\n    }\\n    posId\\n    __typename\\n  }\\n  ... on PreLaunchMarketingListing {\\n    type\\n    trackingLabel\\n    targetUrl\\n    title\\n    callToActionText\\n    textColor\\n    backgroundColor\\n    bodyCopy\\n    smallPrint\\n    vehicleImage {\\n      src\\n      altText\\n      __typename\\n    }\\n    searchFormTitle\\n    __typename\\n  }\\n  ... on LeasingListing {\\n    type\\n    advertId\\n    title\\n    subTitle\\n    price\\n    viewRetailerProfileLinkLabel\\n    leasingGuideLink\\n    images\\n    numberOfImages\\n    dealerLogo\\n    dealerLink\\n    fpaLink\\n    hasVideo\\n    has360Spin\\n    finance {\\n      monthlyPrice {\\n        priceFormattedAndRounded\\n        __typename\\n      }\\n      representativeExample\\n      initialPayment\\n      __typename\\n    }\\n    badges {\\n      type\\n      displayText\\n      __typename\\n    }\\n    policies {\\n      roadTax\\n      returns\\n      delivery\\n      __typename\\n    }\\n    sellerId\\n    trackingContext {\\n      retailerContext {\\n        id\\n        __typename\\n      }\\n      advertContext {\\n        id\\n        advertiserId\\n        advertiserType\\n        make\\n        model\\n        vehicleCategory\\n        year\\n        condition\\n        price\\n        __typename\\n      }\\n      card {\\n        category\\n        subCategory\\n        __typename\\n      }\\n      advertCardFeatures {\\n        condition\\n        numImages\\n        hasFinance\\n        priceIndicator\\n        isManufacturedApproved\\n        isFranchiseApproved\\n        __typename\\n      }\\n      distance {\\n        distance\\n        distance_unit\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  ... on NewCarDealListing {\\n    type\\n    adverts {\\n      advertId\\n      title\\n      subTitle\\n      rrp\\n      price\\n      discount\\n      mainImage\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment fragmentPage on SearchResultsPage {\\n  number\\n  count\\n  results {\\n    count\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment fragmentSearchInfo on SearchInfo {\\n  isForFinanceSearch\\n  __typename\\n}\\n\\nquery SearchResultsListingsQuery($filters: [FilterInput!]!, $channel: Channel!, $page: Int, $sortBy: SearchResultsSort, $listingType: [ListingType!]) {\\n  searchResults(\\n    input: {facets: [], filters: $filters, channel: $channel, page: $page, sortBy: $sortBy, listingType: $listingType}\\n  ) {\\n    listings {\\n      ...fragmentListings\\n      __typename\\n    }\\n    page {\\n      ...fragmentPage\\n      __typename\\n    }\\n    searchInfo {\\n      ...fragmentSearchInfo\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"\n"
				+ "  },\n"
				+ "  {\n"
				+ "    \"operationName\": \"SearchResultsFacetsQuery\",\n"
				+ "    \"variables\": {\n"
				+ "      \"filters\": [\n"
				+ "        {\n"
				+ "          \"filter\": \"home_delivery_adverts\",\n"
				+ "          \"selected\": [\n"
				+ "            \"include\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"make\",\n"
				+ "          \"selected\": [\n"
				+ "            \"Land Rover\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"max_price\",\n"
				+ "          \"selected\": [\n"
				+ "            \"15000\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"min_year_manufactured\",\n"
				+ "          \"selected\": [\n"
				+ "            \"2014\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"model\",\n"
				+ "          \"selected\": [\n"
				+ "            \"Discovery 4\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"postcode\",\n"
				+ "          \"selected\": [\n"
				+ "            \"sa28pp\"\n"
				+ "          ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "          \"filter\": \"price_search_type\",\n"
				+ "          \"selected\": [\n"
				+ "            \"total\"\n"
				+ "          ]\n"
				+ "        }\n"
				+ "      ],\n"
				+ "      \"channel\": \"cars\",\n"
				+ "      \"sortBy\": \"relevance\",\n"
				+ "      \"facets\": [\n"
				+ "        \"acceleration_values\",\n"
				+ "        \"aggregated_trim\",\n"
				+ "        \"annual_tax_values\",\n"
				+ "        \"battery_charge_time_values\",\n"
				+ "        \"battery_quick_charge_time_values\",\n"
				+ "        \"battery_range_values\",\n"
				+ "        \"body_type\",\n"
				+ "        \"boot_size_values\",\n"
				+ "        \"co2_emission_values\",\n"
				+ "        \"colour\",\n"
				+ "        \"digital_retailing\",\n"
				+ "        \"distance\",\n"
				+ "        \"doors_values\",\n"
				+ "        \"drivetrain\",\n"
				+ "        \"engine_power\",\n"
				+ "        \"engine_size\",\n"
				+ "        \"finance\",\n"
				+ "        \"fuel_consumption_values\",\n"
				+ "        \"fuel_type\",\n"
				+ "        \"insurance_group\",\n"
				+ "        \"is_manufacturer_approved\",\n"
				+ "        \"is_writeoff\",\n"
				+ "        \"keywords\",\n"
				+ "        \"lat_long\",\n"
				+ "        \"make\",\n"
				+ "        \"mileage\",\n"
				+ "        \"model\",\n"
				+ "        \"monthly_price\",\n"
				+ "        \"ni_only\",\n"
				+ "        \"part_exchange_available\",\n"
				+ "        \"postcode\",\n"
				+ "        \"price\",\n"
				+ "        \"price_search_type\",\n"
				+ "        \"seats\",\n"
				+ "        \"seller_type\",\n"
				+ "        \"style\",\n"
				+ "        \"sub_style\",\n"
				+ "        \"transmission\",\n"
				+ "        \"ulez_compliant\",\n"
				+ "        \"engine_power\",\n"
				+ "        \"with_manufacturer_rrp_saving\",\n"
				+ "        \"year_manufactured\"\n"
				+ "      ]\n"
				+ "    },\n"
				+ "    \"query\": \"fragment fragmentSortBy on SortBy {\\n  selected\\n  options {\\n    name\\n    value\\n    descriptionTooltipText\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment fragmentFinance on Finance {\\n  hpGuideLink\\n  pcpGuideLink\\n  __typename\\n}\\n\\nquery SearchResultsFacetsQuery($facets: [FacetName!]!, $filters: [FilterInput!]!, $channel: Channel!, $sortBy: SearchResultsSort) {\\n  searchResults(\\n    input: {facets: $facets, filters: $filters, channel: $channel, sortBy: $sortBy}\\n  ) {\\n    sortBy {\\n      ...fragmentSortBy\\n      __typename\\n    }\\n    facets {\\n      facet\\n      filters {\\n        filter\\n        options {\\n          label\\n          value\\n          count\\n          __typename\\n        }\\n        selected\\n        isOnlySelected\\n        sections {\\n          label\\n          values\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    page {\\n      number\\n      count\\n      results {\\n        count\\n        __typename\\n      }\\n      __typename\\n    }\\n    finance {\\n      ...fragmentFinance\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"\n"
				+ "  }\n"
				+ "]".replace("\n", "\\n");
		
//		return ("[{\"operationName\":\"SearchResultsFacetsQuery\",\"variables\":{\"filters\":[{\"filter\":\"home_delivery_adverts\",\"selected\":[\"include\"]},{\"filter\":\"is_writeoff\",\"selected\":[\"exclude\"]},{\"filter\":\"make\",\"selected\":[\""+make+"\"]},{\"filter\":\"max_price\",\"selected\":[\""+maxPrice+"\"]},{\"filter\":\"min_year_manufactured\",\"selected\":[\""+minYear+"\"]},{\"filter\":\"model\",\"selected\":[\""+model+"\"]},{\"filter\":\"postcode\",\"selected\":[\""+postcode+"\"]},{\"filter\":\"price_search_type\",\"selected\":[\"total\"]}],\"channel\":\"cars\",\"facets\":[\"acceleration_values\",\"aggregated_trim\",\"annual_tax_values\",\"battery_charge_time_values\",\"battery_quick_charge_time_values\",\"battery_range_values\",\"body_type\",\"boot_size_values\",\"co2_emission_values\",\"colour\",\"distance\",\"doors_values\",\"drivetrain\",\"engine_power\",\"engine_size\",\"finance\",\"fuel_consumption_values\",\"fuel_type\",\"insurance_group\",\"is_manufacturer_approved\",\"is_writeoff\",\"keywords\",\"lat_long\",\"make\",\"mileage\",\"model\",\"monthly_price\",\"ni_only\",\"part_exchange_available\",\"postcode\",\"price\",\"price_search_type\",\"seats\",\"seller_type\",\"style\",\"sub_style\",\"transmission\",\"ulez_compliant\",\"engine_power\",\"boot_size_values\",\"with_manufacturer_rrp_saving\",\"year_manufactured\",\"battery_range_values\",\"battery_charge_time_values\",\"battery_quick_charge_time_values\"]},\"query\":\"fragment fragmentSortBy on SortBy {\n"
//				+ "  selected\n"
//				+ "  options {\n"
//				+ "    name\n"
//				+ "    value\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  defaultSortDescription\n"
//				+ "  __typename\n"
//				+ "}\n"
//				+ "\n"
//				+ "fragment fragmentFinance on Finance {\n"
//				+ "  hpGuideLink\n"
//				+ "  pcpGuideLink\n"
//				+ "  __typename\n"
//				+ "}\n"
//				+ "\n"
//				+ "query SearchResultsFacetsQuery($facets: [FacetName\\u0021]\\u0021, $filters: [FilterInput\\u0021]\\u0021, $channel: Channel\\u0021, $sortBy: SearchResultsSort) {\n"
//				+ "  searchResults(\n"
//				+ "    input: {facets: $facets, filters: $filters, channel: $channel, sortBy: $sortBy}\n"
//				+ "  ) {\n"
//				+ "    sortBy {\n"
//				+ "      ...fragmentSortBy\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    facets {\n"
//				+ "      facet\n"
//				+ "      filters {\n"
//				+ "        filter\n"
//				+ "        options {\n"
//				+ "          label\n"
//				+ "          value\n"
//				+ "          count\n"
//				+ "          __typename\n"
//				+ "        }\n"
//				+ "        selected\n"
//				+ "        isOnlySelected\n"
//				+ "        sections {\n"
//				+ "          label\n"
//				+ "          values\n"
//				+ "          __typename\n"
//				+ "        }\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    page {\n"
//				+ "      number\n"
//				+ "      count\n"
//				+ "      results {\n"
//				+ "        count\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    finance {\n"
//				+ "      ...fragmentFinance\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "}\n"
//				+ "\"},{\"operationName\":\"SearchResultsListingsQuery\",\"variables\":{\"facets\":[],\"filters\":[{\"filter\":\"home_delivery_adverts\",\"selected\":[\"include\"]},{\"filter\":\"is_writeoff\",\"selected\":[\"exclude\"]},{\"filter\":\"make\",\"selected\":[\""+make+"\"]},{\"filter\":\"max_price\",\"selected\":[\""+maxPrice+"\"]},{\"filter\":\"min_year_manufactured\",\"selected\":[\""+minYear+"\"]},{\"filter\":\"model\",\"selected\":[\""+model+"\"]},{\"filter\":\"postcode\",\"selected\":[\""+postcode+"\"]},{\"filter\":\"price_search_type\",\"selected\":[\"total\"]}],\"channel\":\"cars\",\"page\":"+page+",\"listingType\":null},\"query\":\"fragment fragmentListings on GQLListing {\n"
//				+ "  ... on SearchListing {\n"
//				+ "    type\n"
//				+ "    advertId\n"
//				+ "    title\n"
//				+ "    subTitle\n"
//				+ "    attentionGrabber\n"
//				+ "    price\n"
//				+ "    bodyType\n"
//				+ "    viewRetailerProfileLinkLabel\n"
//				+ "    approvedUsedLogo\n"
//				+ "    nearestCollectionLocation: collectionLocations(limit: 1) {\n"
//				+ "      distance\n"
//				+ "      town\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    distance\n"
//				+ "    discount\n"
//				+ "    description\n"
//				+ "    images\n"
//				+ "    location\n"
//				+ "    numberOfImages\n"
//				+ "    priceIndicatorRating\n"
//				+ "    rrp\n"
//				+ "    manufacturerLogo\n"
//				+ "    sellerType\n"
//				+ "    sellerName\n"
//				+ "    dealerLogo\n"
//				+ "    dealerLink\n"
//				+ "    dealerReview {\n"
//				+ "      overallReviewRating\n"
//				+ "      numberOfReviews\n"
//				+ "      dealerProfilePageLink\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    fpaLink\n"
//				+ "    hasVideo\n"
//				+ "    has360Spin\n"
//				+ "    mileageText\n"
//				+ "    yearAndPlateText\n"
//				+ "    specs\n"
//				+ "    finance {\n"
//				+ "      monthlyPrice {\n"
//				+ "        priceFormattedAndRounded\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      quoteSubType\n"
//				+ "      representativeExample\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    badges {\n"
//				+ "      type\n"
//				+ "      displayText\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    sellerId\n"
//				+ "    trackingContext {\n"
//				+ "      retailerContext {\n"
//				+ "        id\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      advertContext {\n"
//				+ "        id\n"
//				+ "        advertiserId\n"
//				+ "        advertiserType\n"
//				+ "        make\n"
//				+ "        model\n"
//				+ "        vehicleCategory\n"
//				+ "        year\n"
//				+ "        condition\n"
//				+ "        price\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      card {\n"
//				+ "        category\n"
//				+ "        subCategory\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      advertCardFeatures {\n"
//				+ "        condition\n"
//				+ "        numImages\n"
//				+ "        hasFinance\n"
//				+ "        priceIndicator\n"
//				+ "        isManufacturedApproved\n"
//				+ "        isFranchiseApproved\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      distance {\n"
//				+ "        distance\n"
//				+ "        distance_unit\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  ... on GPTListing {\n"
//				+ "    type\n"
//				+ "    targetingSegments {\n"
//				+ "      name\n"
//				+ "      values\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    posId\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  ... on PreLaunchMarketingListing {\n"
//				+ "    type\n"
//				+ "    trackingLabel\n"
//				+ "    targetUrl\n"
//				+ "    title\n"
//				+ "    callToActionText\n"
//				+ "    textColor\n"
//				+ "    backgroundColor\n"
//				+ "    bodyCopy\n"
//				+ "    smallPrint\n"
//				+ "    vehicleImage {\n"
//				+ "      src\n"
//				+ "      altText\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    searchFormTitle\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  ... on LeasingListing {\n"
//				+ "    type\n"
//				+ "    advertId\n"
//				+ "    title\n"
//				+ "    subTitle\n"
//				+ "    price\n"
//				+ "    viewRetailerProfileLinkLabel\n"
//				+ "    leasingGuideLink\n"
//				+ "    images\n"
//				+ "    numberOfImages\n"
//				+ "    dealerLogo\n"
//				+ "    dealerLink\n"
//				+ "    fpaLink\n"
//				+ "    hasVideo\n"
//				+ "    has360Spin\n"
//				+ "    finance {\n"
//				+ "      monthlyPrice {\n"
//				+ "        priceFormattedAndRounded\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      representativeExample\n"
//				+ "      initialPayment\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    badges {\n"
//				+ "      type\n"
//				+ "      displayText\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    policies {\n"
//				+ "      roadTax\n"
//				+ "      returns\n"
//				+ "      delivery\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    sellerId\n"
//				+ "    trackingContext {\n"
//				+ "      retailerContext {\n"
//				+ "        id\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      advertContext {\n"
//				+ "        id\n"
//				+ "        advertiserId\n"
//				+ "        advertiserType\n"
//				+ "        make\n"
//				+ "        model\n"
//				+ "        vehicleCategory\n"
//				+ "        year\n"
//				+ "        condition\n"
//				+ "        price\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      card {\n"
//				+ "        category\n"
//				+ "        subCategory\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      advertCardFeatures {\n"
//				+ "        condition\n"
//				+ "        numImages\n"
//				+ "        hasFinance\n"
//				+ "        priceIndicator\n"
//				+ "        isManufacturedApproved\n"
//				+ "        isFranchiseApproved\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      distance {\n"
//				+ "        distance\n"
//				+ "        distance_unit\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  ... on TopSpotListing {\n"
//				+ "    type\n"
//				+ "    dealer {\n"
//				+ "      dealerReview {\n"
//				+ "        overallReviewRating\n"
//				+ "        numberOfReviews\n"
//				+ "        dealerProfilePageLink\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      distance\n"
//				+ "      sellerName\n"
//				+ "      dealerLink\n"
//				+ "      retailerLogo\n"
//				+ "      marketingTargetUrl\n"
//				+ "      marketingImageUrl\n"
//				+ "      retailerId\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    adverts {\n"
//				+ "      advertId\n"
//				+ "      title\n"
//				+ "      subTitle\n"
//				+ "      price\n"
//				+ "      attentionGrabber\n"
//				+ "      badges {\n"
//				+ "        type\n"
//				+ "        displayText\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      discount\n"
//				+ "      rrp\n"
//				+ "      specs\n"
//				+ "      fpaLink\n"
//				+ "      price\n"
//				+ "      discount\n"
//				+ "      images\n"
//				+ "      trackingContext {\n"
//				+ "        retailerContext {\n"
//				+ "          id\n"
//				+ "          __typename\n"
//				+ "        }\n"
//				+ "        advertContext {\n"
//				+ "          id\n"
//				+ "          advertiserId\n"
//				+ "          advertiserType\n"
//				+ "          make\n"
//				+ "          model\n"
//				+ "          vehicleCategory\n"
//				+ "          year\n"
//				+ "          condition\n"
//				+ "          price\n"
//				+ "          __typename\n"
//				+ "        }\n"
//				+ "        card {\n"
//				+ "          category\n"
//				+ "          subCategory\n"
//				+ "          __typename\n"
//				+ "        }\n"
//				+ "        advertCardFeatures {\n"
//				+ "          condition\n"
//				+ "          numImages\n"
//				+ "          hasFinance\n"
//				+ "          priceIndicator\n"
//				+ "          isManufacturedApproved\n"
//				+ "          isFranchiseApproved\n"
//				+ "          __typename\n"
//				+ "        }\n"
//				+ "        distance {\n"
//				+ "          distance\n"
//				+ "          distance_unit\n"
//				+ "          __typename\n"
//				+ "        }\n"
//				+ "        __typename\n"
//				+ "      }\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  ... on NewCarDealListing {\n"
//				+ "    type\n"
//				+ "    adverts {\n"
//				+ "      advertId\n"
//				+ "      title\n"
//				+ "      subTitle\n"
//				+ "      rrp\n"
//				+ "      price\n"
//				+ "      discount\n"
//				+ "      mainImage\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  __typename\n"
//				+ "}\n"
//				+ "\n"
//				+ "fragment fragmentPage on SearchResultsPage {\n"
//				+ "  number\n"
//				+ "  count\n"
//				+ "  results {\n"
//				+ "    count\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "  __typename\n"
//				+ "}\n"
//				+ "\n"
//				+ "fragment fragmentSearchInfo on SearchInfo {\n"
//				+ "  isForFinanceSearch\n"
//				+ "  __typename\n"
//				+ "}\n"
//				+ "\n"
//				+ "query SearchResultsListingsQuery($filters: [FilterInput\\u0021]\\u0021, $channel: Channel\\u0021, $page: Int, $sortBy: SearchResultsSort) {\n"
//				+ "  searchResults(\n"
//				+ "    input: {facets: [], filters: $filters, channel: $channel, page: $page, sortBy: $sortBy}\n"
//				+ "  ) {\n"
//				+ "    listings {\n"
//				+ "      ...fragmentListings\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    page {\n"
//				+ "      ...fragmentPage\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    searchInfo {\n"
//				+ "      ...fragmentSearchInfo\n"
//				+ "      __typename\n"
//				+ "    }\n"
//				+ "    __typename\n"
//				+ "  }\n"
//				+ "}\n"
//				+ "\"}]").replace("\n", "\\n");
		//@formatter:on
	}

}
