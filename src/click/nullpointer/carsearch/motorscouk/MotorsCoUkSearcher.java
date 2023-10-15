package click.nullpointer.carsearch.motorscouk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;

public class MotorsCoUkSearcher implements ICarSearcher {
	private static final Map<String, String> STATIC_REQUEST_HEADERS = new HashMap<>();
	private static final Gson COMMON_GSON = new GsonBuilder()
			.registerTypeAdapter(MotorsCoUkCarListing.class, new MotorsCoUkListingParser()).create();

	static {
		STATIC_REQUEST_HEADERS.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
		STATIC_REQUEST_HEADERS.put("Accept", "*/*");
		STATIC_REQUEST_HEADERS.put("Accept-Language", "en");
		STATIC_REQUEST_HEADERS.put("Accept-Encoding", "gzip, deflate, br");
		STATIC_REQUEST_HEADERS.put("Connection", "keep-alive");
		STATIC_REQUEST_HEADERS.put("Cache-Control", "no-cache");
		STATIC_REQUEST_HEADERS.put("Dnt", "1");
		STATIC_REQUEST_HEADERS.put("Origin", "https://motors.co.uk");
		STATIC_REQUEST_HEADERS.put("Sec-Ch-Ua",
				"\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"");
		STATIC_REQUEST_HEADERS.put("Sec-Ch-Ua-Mobile", "?0");
		STATIC_REQUEST_HEADERS.put("Sec-Ch-Ua-Platform", "Windows");
		STATIC_REQUEST_HEADERS.put("Upgrade-Insecure-Requests", "1");
		STATIC_REQUEST_HEADERS.put("Sec-Fetch-Dest", "empty");
		STATIC_REQUEST_HEADERS.put("Sec-Fetch-Mode", "cors");
		STATIC_REQUEST_HEADERS.put("Sec-Fetch-Site", "same-origin");
		STATIC_REQUEST_HEADERS.put("Sec-Gpc", "1");
	}

	private final String make;
	private final String model;
	private final int minYear;
	private final int maxYear;
	private final int maxPrice;
	private final String referencePostcode;
	private final long sleepBetweenReqs;

	public MotorsCoUkSearcher(MotorsCoUkConfig conf) {
		this.make = conf.getMake();
		this.model = conf.getModel();
		this.minYear = conf.getMinYear();
		this.maxYear = conf.getMaxYear();
		this.maxPrice = conf.getMaxPrice();
		this.referencePostcode = conf.getReferencePostcode();
		this.sleepBetweenReqs = conf.getSleepBetweenReqs();
	}

	@Override
	public Collection<AbstractCarListing> searchForListings() throws IOException {
		return recursiveSearch(1);
	}

	private List<AbstractCarListing> recursiveSearch(int page) throws IOException {
		String dat = sendPostRequest("https://www.motors.co.uk/search/car/results", getRequestPayload(page),
				"application/json", STATIC_REQUEST_HEADERS);
		JsonObject obj = COMMON_GSON.fromJson(dat, JsonObject.class);
		System.err.print("GET Page " + page);
//		int currPage = obj.getAsJsonObject("Pagination").get("CurrentPage").getAsInt();
		int lastPage = obj.getAsJsonObject("Pagination").get("LastPage").getAsInt();
		System.err.println(" of " + lastPage);

		JsonArray arr = obj.getAsJsonArray("Results");
		Iterator<JsonElement> i = arr.iterator();
		while (i.hasNext()) {
			JsonElement e = i.next();
			if (e.isJsonObject()
					&& !e.getAsJsonObject().get("ObjectType").getAsString().equalsIgnoreCase("UsedVehicleResult")) {
				i.remove();
			}
		}

		List<AbstractCarListing> lst = new LinkedList<>();
		MotorsCoUkCarListing[] listings = COMMON_GSON.fromJson(arr, MotorsCoUkCarListing[].class);
		Arrays.stream(listings).forEach(a -> {
			a.setSearcher(this);
			lst.add(a);
		});
//		System.err.println("GOT " + listings.length + " listings");
		if (lastPage > page) {
			try {
				Thread.sleep(sleepBetweenReqs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lst.addAll(recursiveSearch(page + 1));
		}
		return lst;
	}

	@Override
	public String getPrettySearcherName() {
		return "🛵 Motors.co.uk Crawler";
	}

	private String getRequestPayload(int page) {
		return "{\"isNewSearch\":true,"//
				+ "\"pagination\":{\"TotalPages\":10,\"BasicResultCount\":100,\"TotalRecords\":100,\"FirstRecord\":1,\"LastRecord\":100,\"CurrentPage\":\""
				+ page
				+ "\",\"LastPage\":100,\"PageSize\":21,\"PageLinksPerPage\":5,\"PageLinks\":[{\"Name\":\"1\",\"Link\":\"1\"}],\"FirstPageLink\":{\"Name\":\"1\",\"Link\":\"1\"},\"Level\":null,\"Variants\":0},"
				+ "\"searchPanelParameters\":{"//
				+ "\"Doors\":[],"//
				+ "\"Seats\":[],"//
				+ "\"SafetyRatings\":[],"//
				+ "\"SelectedTopSpeed\":null,"//
				+ "\"SelectedPower\":null,"//
				+ "\"SelectedAcceleration\":null,"//
				+ "\"MinPower\":-1,"//
				+ "\"MaxPower\":-1,"//
				+ "\"MinEngineSize\":-1,"//
				+ "\"MaxEngineSize\":-1,"//
				+ "\"BodyStyles\":[],"//
				+ "\"DriveTrains\":[],"//
				+ "\"MakeModels\":[{\"Value\":\"" + make + "\",\"Models\":[\"" + model + "\"],\"Trims\":[]}],"//
				+ "\"FuelTypes\":[],"//
				+ "\"Transmissions\":[],"//
				+ "\"Colours\":[],"//
				+ "\"IsPaymentSearch\":false,"//
				+ "\"IsReduced\":false,"//
				+ "\"IsHot\":false,"//
				+ "\"IsRecentlyAdded\":false,"//
				+ "\"IsGroupStock\":false,"//
				+ "\"PartExAvailable\":false,"//
				+ "\"IsPriceAndGo\":false,"//
				+ "\"IsPriceExcludeVATSearch\":false,"//
				+ "\"IncludeOnlineOnlySearch\":false,"//
				+ "\"IsYearSearch\":true,"//
				+ "\"IsPreReg\":false,"//
				+ "\"IsExDemo\":false,"//
				+ "\"ExcludeExFleet\":false,"//
				+ "\"ExcludeExHire\":false,"//
				+ "\"Keywords\":[],"//
				+ "\"SelectedInsuranceGroup\":null,"//
				+ "\"SelectedFuelEfficiency\":null,"//
				+ "\"SelectedCostAnnualTax\":null,"//
				+ "\"SelectedCO2Emission\":null,"//
				+ "\"SelectedTowingBrakedMax\":null,"//
				+ "\"SelectedTowingUnbrakedMax\":null,"//
				+ "\"SelectedTankRange\":null,"//
				+ "\"DealerId\":0,"//
				+ "\"Age\":-1,"//
				+ "\"MinAge\":-1,"//
				+ "\"MaxAge\":-1,"//
				+ "\"MinYear\":\"" + minYear + "\","//
				+ "\"MaxYear\":\"" + maxYear + "\","//
				+ "\"Mileage\":-1,"//
				+ "\"MinMileage\":-1,"//
				+ "\"MaxMileage\":-1,"//
				+ "\"MinPrice\":-1,"//
				+ "\"MaxPrice\":" + maxPrice + ","//
				+ "\"MinPaymentMonthlyCost\":-1,"//
				+ "\"MaxPaymentMonthlyCost\":-1,"//
				+ "\"PaymentTerm\":60,"//
				+ "\"PaymentMileage\":10000,"//
				+ "\"PaymentDeposit\":1000,"//
				+ "\"SelectedSoldStatusV2\":\"notsold\","//
				+ "\"SelectedBatteryRangeMiles\":null,"//
				+ "\"SelectedBatteryFastChargeMinutes\":null,"//
				+ "\"BatteryIsLeased\":false,"//
				+ "\"BatteryIsWarrantyWhenNew\":false,"//
				+ "\"ExcludeImports\":false,"//
				+ "\"ExcludeHistoryCatNCatD\":true,"//
				+ "\"ExcludeHistoryCatSCatC\":true,"//
				+ "\"Type\":1,\"PostCode\":\"" + referencePostcode + "\","//
				+ "\"Distance\":2000,"//
				+ "\"SortOrder\":0,"//
				+ "\"DealerGroupId\":0,"//
				+ "\"MinImageCountActive\":false,"//
				+ "\"PaginationCurrentPage\":1}}";
	}

	private String sendPostRequest(String url, String postData, String contentType, Map<String, String> requestProperty)
			throws IOException {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		requestProperty.forEach((k, v) -> connection.setRequestProperty(k, v));
		connection.setRequestProperty("Content-Type", contentType);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		// Send POST data
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = postData.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		}

		InputStream is = connection.getInputStream();
		if ("gzip".equalsIgnoreCase(connection.getHeaderField("Content-Encoding"))) {
			is = new GZIPInputStream(is);
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = reader.readLine()) != null) {
			response.append(inputLine);
		}
		reader.close();
		return response.toString();
	}

}
