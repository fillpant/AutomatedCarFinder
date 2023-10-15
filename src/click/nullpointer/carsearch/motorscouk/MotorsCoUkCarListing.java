package click.nullpointer.carsearch.motorscouk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ListingDetail;
import click.nullpointer.carsearch.model.ListingDetail.StandardListingDetails;

public class MotorsCoUkCarListing extends AbstractCarListing {

	private long id;
	private String bodyType;
	private String engineLitres;
	private double distance;
	private boolean isReduced;
	private boolean isExDemo;
	private boolean isLowMileage;
	private String colour;
	private String listingURL;
	private double price;
	private String originalPrice;
	private int imageCnt;
	private boolean isPriceExcludingVat;
	private int registrationYear;
	private boolean sold;
	private String varian;
	private String model;
	private String title;
	private int mileage;
	private String transmission;
	private String fuelType;
	private String[] images;

	public MotorsCoUkCarListing(long listingUniqueID, double price, String listingURL, String bodyType,
			String engineLitres, double distance, boolean isReduced, boolean isExDemo, boolean isLowMileage,
			String colour, String originalPrice, int imageCnt, boolean isPriceExcludingVat, int registrationYear,
			boolean sold, String varian, String model, String title, int mileage, String transmission, String fuelType,
			String[] images) {
		super(listingUniqueID, price, listingURL);
		this.id = listingUniqueID;
		this.bodyType = bodyType;
		this.engineLitres = engineLitres;
		this.distance = distance;
		this.isReduced = isReduced;
		this.isExDemo = isExDemo;
		this.isLowMileage = isLowMileage;
		this.colour = colour;
		this.listingURL = listingURL;
		this.price = price;
		this.originalPrice = originalPrice;
		this.imageCnt = imageCnt;
		this.isPriceExcludingVat = isPriceExcludingVat;
		this.registrationYear = registrationYear;
		this.sold = sold;
		this.varian = varian;
		this.model = model;
		this.title = title;
		this.mileage = mileage;
		this.transmission = transmission;
		this.fuelType = fuelType;
		this.images = images;
	}

	@Override
	public List<ListingDetail<?>> getDetails() {
		List<ListingDetail<?>> details = new ArrayList<>();
		details.add(StandardListingDetails.title(title));
		details.add(StandardListingDetails.subtitle(varian));
		if (getOriginalPriceParsed() > 0 && getOriginalPriceParsed() != getPrice())
			details.add(StandardListingDetails.priceDifference(price, getOriginalPriceParsed()));
		details.add(StandardListingDetails.price(price));
		details.add(StandardListingDetails.mileage(mileage));
		details.add(StandardListingDetails.year(registrationYear));
		details.add(StandardListingDetails.distance(distance));
		details.add(StandardListingDetails.colour(colour));
		details.add(new ListingDetail<>("Engine Litres", engineLitres, f -> f, "🛢️"));
		details.add(StandardListingDetails.transmission(transmission));
		details.add(StandardListingDetails.fuelType(fuelType));
		details.add(StandardListingDetails.photoCount(imageCnt));
		details.add(StandardListingDetails.model(model));
		if (isSold())
			details.add(new ListingDetail<>("Sale", "SOLD", f -> f, "🤝"));
		details.add(new ListingDetail<>("Ex-Demo", isExDemo ? "Yes" : "No", f -> f, "👀"));
		details.add(new ListingDetail<>("Low Mileage", isLowMileage ? "Yes" : "No", f -> f, "💡"));
		return details;
	}

	private double getOriginalPriceParsed() {
		return getOriginalPrice() == null ? 0 : Double.parseDouble("0" + getOriginalPrice().replaceAll("[^0-9]", ""));
	}
//
//	@Override
//	public String toNotificationText() {
//		StringBuilder sb = new StringBuilder();
//		String title = getTitle();
//		if (title.length() > 42)
//			title = title.substring(0, 39) + "...";
//		String subtitle = getVarian();
//		if (subtitle.length() > 42)
//			subtitle = subtitle.substring(0, 39) + "...";
//		if (isSold())
//			title = "💸 SOLD " + title;
//		sb.append(title).append('\n');
//		if (!getVarian().trim().isEmpty())
//			sb.append(subtitle).append('\n');
//
//		double originalPriceParsed = getPrice();
//		if (getOriginalPrice() != null && !getOriginalPrice().isEmpty())
//			originalPriceParsed = Double.parseDouble(getOriginalPrice().replaceAll("[^0-9]", ""));
//		double price = getPrice();
//		if (isPriceExcludingVat()) {
//			price += price * .2;
//			originalPriceParsed += originalPriceParsed * .2;
//		}
//		sb.append("💰 " + String.format("£%,.2f %s%s", getPrice(), isReduced ? "👇 Reduced" : "",
//				originalPriceParsed == price ? "" : String.format(" (was £%,.2f)", originalPriceParsed))).append('\n');
//
//		sb.append("📏 " + String.format("%,d miles (%,d km) %s", getMileage(), (int) (getMileage() * 1.60934),
//				isLowMileage() ? "👇 Low" : "")).append('\n');
//		sb.append("📖 Year " + getRegistrationYear()).append('\n');
//		sb.append("📸 " + getImageCnt());
//		if (isExDemo())
//			sb.append('\n').append("💄 Ex-Demo Vehicle");
//		return sb.toString();
//	}

	@Override
	public Collection<String> getPictureURLs() {
		return Collections.unmodifiableCollection(Arrays.asList(images));
	}

	public long getId() {
		return id;
	}

	public String getBodyType() {
		return bodyType;
	}

	public String getEngineLitres() {
		return engineLitres;
	}

	public double getDistance() {
		return distance;
	}

	public boolean isReduced() {
		return isReduced;
	}

	public boolean isExDemo() {
		return isExDemo;
	}

	public boolean isLowMileage() {
		return isLowMileage;
	}

	public String getColour() {
		return colour;
	}

	public String getListingURL() {
		return listingURL;
	}

	public double getPrice() {
		return price;
	}

	public String getOriginalPrice() {
		return originalPrice;
	}

	public int getImageCnt() {
		return imageCnt;
	}

	public boolean isPriceExcludingVat() {
		return isPriceExcludingVat;
	}

	public int getRegistrationYear() {
		return registrationYear;
	}

	public boolean isSold() {
		return sold;
	}

	public String getVarian() {
		return varian;
	}

	public String getModel() {
		return model;
	}

	public String getTitle() {
		return title;
	}

	public int getMileage() {
		return mileage;
	}

	public String getFuelType() {
		return fuelType;
	}

	public String[] getImages() {
		return images;
	}

}
