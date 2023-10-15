package click.nullpointer.carsearch.theaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ListingDetail;
import click.nullpointer.carsearch.model.ListingDetail.StandardListingDetails;

public class TheAACarListing extends AbstractCarListing {

	private final String title;
	private Optional<Double> previousParsedPrice;
	private String distance;
	private int year;
	private int miles;
	private String[] imageURLs;

	public TheAACarListing(long listingUniqueID, double price, String listingURL, String title,
			Optional<Double> previousParsedPrice, boolean includesVAT, String distance, int year, int miles,
			String[] imageUrls) {
		super(listingUniqueID, price, listingURL);
		this.title = title;
		this.previousParsedPrice = previousParsedPrice;
		this.distance = distance;
		this.year = year;
		this.miles = miles;
		this.imageURLs = imageUrls;
	}

	public String getTitle() {
		return title;
	}

	public Optional<Double> getPreviousParsedPrice() {
		return previousParsedPrice;
	}

	public String getDistance() {
		return distance;
	}

	public int getYear() {
		return year;
	}

	public int getMiles() {
		return miles;
	}

	@Override
	public List<ListingDetail<?>> getDetails() {
		List<ListingDetail<?>> details = new ArrayList<>();
		details.add(StandardListingDetails.title(title));
		double priceDiff = getPreviousParsedPrice().isPresent() ? (getPreviousParsedPrice().get() - getPrice()) : 0;
		if (priceDiff != 0)
			details.add(StandardListingDetails.priceDifference(getPrice(), getPreviousParsedPrice().get()));
		else
			details.add(StandardListingDetails.price(getPrice()));
		details.add(StandardListingDetails.mileage(getMiles()));
		details.add(StandardListingDetails.year(getYear()));
		details.add(StandardListingDetails.distance(getDistanceInt()));
		details.add(StandardListingDetails.photoCount(getPictureURLs().size()));
		return details;
	}

	private int getDistanceInt() {
		return Integer.parseInt("0" + getDistance().replaceAll("[^0-9]", ""));
	}

//	@Override
//	public String toNotificationText() {
//		StringBuilder sb = new StringBuilder();
//		double priceDiff = getPreviousParsedPrice().isPresent() ? (getPreviousParsedPrice().get() - getPrice()) : 0;
//		String prefix = "";
//		if (Math.abs(priceDiff) > 1000) {
//			if (priceDiff > 0)
//				prefix = "🔥 ";
//			else
//				prefix = "👎 ";
//		}
//		sb.append(prefix + getTitle()).append('\n');
//		sb.append("💰 " + String.format("£%,.0f%s", getPrice(),
//				getPreviousParsedPrice().isPresent() ? String.format(" (was £%,.0f)", getPreviousParsedPrice().get())
//						: ""))
//				.append('\n');
//		sb.append("📏 " + String.format("%,d miles (%,d km)", getMiles(), (int) (getMiles() * 1.609344))).append('\n');
//		sb.append("📖 Year " + getYear()).append('\n');
//		sb.append("📍 Distance: " + getDistance()).append('\n');
//		return sb.toString();
//	}

	@Override
	public Collection<String> getPictureURLs() {
		return Collections.unmodifiableList(Arrays.asList(imageURLs));
	}

}
