package click.nullpointer.carsearch.rac;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;
import click.nullpointer.carsearch.model.RequestUtils;

public class RACSearcher implements ICarSearcher {
	private static final Gson DEFAULT_PARSER = new GsonBuilder()
			.registerTypeAdapter(RACCarListing.class, new RACListingParser()).create();

	private int maxAge;
	private int maxPrice;
	private String referencePostcode;
	private long sleepBetweenRequests;

	public RACSearcher(RACConfig c) {
		this.maxAge = c.getMaxAge();
		this.maxPrice = c.getMaxPrice();
		this.referencePostcode = c.getReferencePostcode();
		this.sleepBetweenRequests = c.getSleepBetweenRequests();
	}

	@Override
	public Collection<AbstractCarListing> searchForListings() throws IOException {
		// TODO ONLY SEARCHES 1 PAGE!!!
		Map<String, String> headers = new HashMap<>(RequestUtils.STATIC_REQUEST_HEADERS);
		headers.put("X-Requested-With", "XMLHttpRequest");
		return recursiveSearch(1, headers);
	}

	private List<AbstractCarListing> recursiveSearch(int page, Map<String, String> headers) throws IOException {
		String req = getRequestJson(page);
		System.err.print("GET Page " + page);
		String response = RequestUtils.postString("https://www.raccars.co.uk/search/car/results", req,
				"application/json", headers);
		JsonObject obj = DEFAULT_PARSER.fromJson(response, JsonObject.class);
		int lastPage = obj.getAsJsonObject("Pagination").get("LastPage").getAsInt();
		System.err.println(" of " + lastPage);
		JsonArray arr = obj.getAsJsonArray("Results");
		Iterator<JsonElement> e = arr.iterator();
		// Remove non "UsedVehicleResult" entries.
		while (e.hasNext()) {
			JsonElement el = e.next();
			if (el.isJsonObject() && !"UsedVehicleResult".equals(el.getAsJsonObject().get("ObjectType").getAsString()))
				e.remove();
		}
		LinkedList<AbstractCarListing> lst = new LinkedList<>();
		RACCarListing[] res = DEFAULT_PARSER.fromJson(arr, RACCarListing[].class);
		Arrays.stream(res).forEach(a -> {
			a.setSearcher(this);
			lst.add(a);
		});
		if (page < lastPage) {
			try {
				Thread.sleep(sleepBetweenRequests);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			lst.addAll(recursiveSearch(page + 1, headers));
		}
		return lst;
	}

	@Override
	public String getPrettySearcherName() {
		return "🛺 RAC Crawler";
	}

	private String getRequestJson(int page) {
		//@formatter:off
		return "{"
				+ "  \"searchPanelParameters\": {"
				+ "    \"Doors\": [],"
				+ "    \"Seats\": [],"
				+ "    \"SafetyRatings\": [],"
				+ "    \"SelectedTopSpeed\": null,"
				+ "    \"SelectedPower\": null,"
				+ "    \"SelectedAcceleration\": null,"
				+ "    \"SelectedEngineSize\": null,"
				+ "    \"BodyStyles\": [],"
				+ "    \"SoldStatusTypes\": [],"
				+ "    \"MakeModels\": ["
				+ "      {"
				+ "        \"Value\": \"Land Rover\","
				+ "        \"Models\": ["
				+ "          \"Discovery 4\""
				+ "        ],"
				+ "        \"Trims\": []"
				+ "      }"
				+ "    ],"
				+ "    \"FuelTypes\": [],"
				+ "    \"Transmissions\": [],"
				+ "    \"Colours\": [],"
				+ "    \"IsPaymentSearch\": false,"
				+ "    \"IsReduced\": false,"
				+ "    \"IsPreReg\": false,"
				+ "    \"IsExDemo\": false,"
				+ "    \"Keywords\": [],"
				+ "    \"SelectedInsuranceGroup\": null,"
				+ "    \"SelectedFuelEfficiency\": null,"
				+ "    \"SelectedCostAnnualTax\": null,"
				+ "    \"SelectedCO2Emission\": null,"
				+ "    \"SelectedTowingBrakedMax\": null,"
				+ "    \"SelectedTowingUnbrakedMax\": null,"
				+ "    \"SelectedTankRange\": null,"
				+ "    \"DealerId\": 0,"
				+ "    \"Age\": "+maxAge+","
				+ "    \"Mileage\": -1,"
				+ "    \"MinPrice\": -1,"
				+ "    \"MaxPrice\": "+maxPrice+","
				+ "    \"MinPaymentMonthlyCost\": -1,"
				+ "    \"MaxPaymentMonthlyCost\": -1,"
				+ "    \"PaymentTerm\": 60,"
				+ "    \"PaymentDeposit\": 1000,"
				+ "    \"Type\": 1,"
				+ "    \"PostCode\": \""+referencePostcode+"\","
				+ "    \"Distance\": 1000,"
				+ "    \"PaginationCurrentPage\": 1,"
				+ "    \"SortOrder\": 0,"
				+ "    \"RACApproved\": false,"
				+ "    \"DealerGroupId\": 0"
				+ "  },"
				+ "  \"isNewSearch\": true,"
				+ "  \"pagination\": {"
				+ "    \"TotalPages\": 0,"
				+ "    \"BasicResultCount\": 14,"
				+ "    \"TotalRecords\": 14,"
				+ "    \"FirstRecord\": 1,"
				+ "    \"LastRecord\": 12,"
				+ "    \"CurrentPage\": \""+page+"\","
				+ "    \"LastPage\": 2,"
				+ "    \"PageSize\": 12,"
				+ "    \"PageLinksPerPage\": 5,"
				+ "    \"PageLinks\": ["
				+ "      {"
				+ "        \"Name\": \"1\","
				+ "        \"Link\": \"1\""
				+ "      },"
				+ "      {"
				+ "        \"Name\": \"2\","
				+ "        \"Link\": \"2\""
				+ "      }"
				+ "    ],"
				+ "    \"FirstPageLink\": {"
				+ "      \"Name\": \"1\","
				+ "      \"Link\": \"1\""
				+ "    },"
				+ "    \"Level\": null,"
				+ "    \"Variants\": 0"
				+ "  }"
				+ "}";
		//@formatter:on

	}

}
