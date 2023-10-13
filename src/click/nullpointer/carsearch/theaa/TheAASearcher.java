package click.nullpointer.carsearch.theaa;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;
import click.nullpointer.carsearch.model.RequestUtils;

public class TheAASearcher implements ICarSearcher {
	private static final Pattern PRICE_PARSER = Pattern.compile("£(\\d+,?\\d+)( \\+ VAT)?( was £(\\d+,\\d+))?",
			Pattern.CASE_INSENSITIVE);

	private String referencePostcode;
	private int minPrice;
	private int maxPrice;
	private int ageYears;
	private int modelID;
	private int makeID;

	public TheAASearcher(TheAAConfig conf) {
		this.referencePostcode = conf.getReferencePostcode();
		this.minPrice = conf.getMinPrice();
		this.maxPrice = conf.getMaxPrice();
		this.ageYears = conf.getAgeYears();
		this.modelID = conf.getModelID();
		this.makeID = conf.getMakeID();
	}

	@Override
	public Collection<AbstractCarListing> searchForListings() throws IOException {
		//@formatter:off
		String url = "\r\n"
				+ "https://www.theaa.com/used-cars/displaycars?"
				+ "fullpostcode=SA11XX&"
				+ "pricefrom=0&"
				+ "priceto=16000&"
				+ "travel=2000&"
				+ "age=9&"
				+ "mymodelid=102&"
				+ "mymakeid=121&";
		//@formatter:on
		String html = RequestUtils.get(url, RequestUtils.STATIC_REQUEST_HEADERS);
		Document doc = Jsoup.parse(html);

		return doc.getElementsByClass("vl-item").stream().parallel().map(el -> {
			try {
				String title = el.getElementsByClass("vl-title").get(0).attr("title");
				String listingLink = "https://www.theaa.com" + el.getElementsByClass("image-link").get(0).attr("href");
				String imageLink = el.getElementsByClass("image-link").get(0).getElementsByAttribute("srcset").get(0)
						.attr("src");
				long id = Long
						.parseLong(listingLink.substring(listingLink.indexOf("cardetails/")).replaceAll("[^0-9]", ""));
				String priceString = el.getElementsByClass("total-price").get(0).text();
				Matcher priceMatch = PRICE_PARSER.matcher(priceString);
				double price = -1d;
				Optional<Double> oldPrice = Optional.empty();
				boolean includesVAT = true;
				if (priceMatch.find()) {
					price = Double.parseDouble(priceMatch.group(1).replace(",", ""));
					if (priceMatch.group(4) != null && !priceMatch.group(4).trim().isEmpty())
						oldPrice = Optional.of(Double.parseDouble(priceMatch.group(4).replace(",", "")));
					includesVAT = priceMatch.group(2) == null;
				}

				String distance = el.getElementsByClass("vl-location").get(0).getElementsByClass("strong-inline")
						.text();
				List<String> details = el.getElementsByClass("vl-specs").get(0).getElementsByTag("li").stream()
						.filter(a -> !a.hasAttr("aria-hidden")).map(a -> a.text()).collect(Collectors.toList());
				String yearString = details.get(0);
				String milesString = details.get(1);
				int year = Integer.parseInt(yearString.replaceAll("[^0-9]", ""));
				int miles = Integer.parseInt(milesString.replaceAll("[^0-9]", ""));
				TheAACarListing c = new TheAACarListing(id, price, listingLink, title, oldPrice, includesVAT, distance,
						year, miles, new String[] { imageLink });
				c.setSearcher(this);
				return c;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}).filter(a -> a != null).collect(Collectors.toList());
		/*@formatter:off
		 * £15,995
		 * £14,990 was £16,850
		 * £15,890 was £15,990
		 * £13,990 + VAT was £14,990
		 * £13,490
		 * £13,895
		 * £14,995
		 * £14,000
		 * £13,500 was £14,500
		 * £11,990
		 * £8,995 was £9,995
		 * £14,950 was £18,950
		 * @formatter:on
		 */
	}

	@Override
	public String getPrettySearcherName() {
		// TODO Auto-generated method stub
		return "🚕 TheAA Crawler";
	}

}
