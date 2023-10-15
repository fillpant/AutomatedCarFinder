package click.nullpointer.carsearch.cargurus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;
import click.nullpointer.carsearch.model.RequestUtils;

public class CarGurusSearcher implements ICarSearcher {

	private static final Gson COMMON_GSON = new Gson();
	private static final int MAX_PAGES = 25;
	private static final int RESULTS_PER_PAGE = 15;
	private final int minYear;
	private final String referencePostcode;
	private final int maxPrice;
	private final long sleepBetweenRequests;

	public CarGurusSearcher(CarGurusConfig conf) throws IOException {
		this.minYear = conf.getMinYear();
		this.referencePostcode = URLEncoder.encode(conf.getReferencePostcode(), "UTF-8");
		this.maxPrice = conf.getMaxPrice();
		this.sleepBetweenRequests = conf.getSleepBetweenRequests();
	}

	@Override
	public Collection<AbstractCarListing> searchForListings() throws IOException {
		List<AbstractCarListing> listings = new ArrayList<>();
		for (int i = 0; i < MAX_PAGES; ++i) {
			if (i > 0) {
				try {
					Thread.sleep(sleepBetweenRequests);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			String request = getPage(i);
			String json = RequestUtils.get(request, RequestUtils.STATIC_REQUEST_HEADERS);
			if (!json.trim().isEmpty() && json.trim().equalsIgnoreCase("null"))
				break;
			CarGurusCarListing[] lsts = COMMON_GSON.fromJson(json, CarGurusCarListing[].class);
			listings.addAll(Arrays.asList(lsts));
		}
		listings.forEach(a -> a.setSearcher(this));
		System.err.println("GOT " + listings.size());
		return listings;
	}

	//@formatter:off
//	private String getInitialRequest() {
//		return "https://www.cargurus.co.uk/Cars/preflightResults.action?"
//				+ "searchId=8e209604-0af6-4799-bf70-b2ff8874225d&"
//				+ "zip="+referencePostcode+"&distance=1660&"
//				+ "entitySelectingHelper.selectedEntity=d3422&"
//				+ "sourceContext=cargurus&"
//				+ "inventorySearchWidgetType=AUTO&"
//				+ "sortDir=ASC&"
//				+ "sortType=DEAL_SCORE&"
//				+ "shopByTypes=MIX&"
//				+ "nonShippableBaseline=15&"
//				+ "startYear="+minYear+"&"
//				+ "maxPrice="+maxPrice+"&"
//				+ "showNegotiable=false&"
//				+ "maxResults="+RESULTS_PER_PAGE;
//	}

	private String getPage(int page) {
		return "https://www.cargurus.co.uk/Cars/searchResults.action?"
				+ "searchId=a6950fb4-ab71-4453-82e3-02f1a67dd990&"
				+ "zip="+referencePostcode+"&"
				+ "distance=1660&"
				+ "entitySelectingHelper.selectedEntity=d3422&"
				+ "sourceContext=carGurusHomePageModel&"
				+ "inventorySearchWidgetType=AUTO&"
				+ "sortDir=ASC&"
				+ "sortType=DEAL_SCORE&"
				+ "shopByTypes=MIX&"
				+ "nonShippableBaseline=15&"
				+ "startYear="+minYear+"&"
				+ "showNegotiable=false&"
				+ "maxPrice="+maxPrice+"&"
				+ "offset="+RESULTS_PER_PAGE*page+"&"
				+ "maxResults="+RESULTS_PER_PAGE+"&"
				+ "filtersModified=true";

	}
	//@formatter:on

	@Override
	public String getPrettySearcherName() {
		return "🚚 CarGurus Crawler";
	}

}
