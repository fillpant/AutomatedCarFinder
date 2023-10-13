package click.nullpointer.carsearch.rac;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import click.nullpointer.carsearch.model.AbstractCarListing;

public class RACCarListing extends AbstractCarListing {

	private long vehicleId;
	private int registeredKeepers;
	private LocalDate motExpiry, registrationDate;
	private String detailsPageURL;
	private double price, originalPrice;
	private String shortFeaturesSummary;
	private String featuresSummary;
	private String shortDescription;
	private String description;
	private String historyCheckDescription;
	private String historyCheckName;
	private String title;
	private String variant;
	private String manufacturer;
	private String model;
	private int distance;
	private int mileage;
	private String transmission;
	private String colour;
	private int registrationYear;
	private String[] imageURLs;
	private int priceDiscount;
	private boolean reduced;
	private boolean exHire;
	private boolean exDemo;
	private boolean exFleet;
	private boolean hot;
	private boolean isLowMileage;
	private int roadTaxCost;

	public RACCarListing(long vehicleId, int registeredKeepers, LocalDate motExpiry, LocalDate registrationDate,
			String detailsPageURL, double price, double originalPrice, String shortFeaturesSummary,
			String featuresSummary, String shortDescription, String description, String historyCheckDescription,
			String historyCheckName, String title, String variant, String manufacturer, String model, int distance,
			int mileage, String transmission, String colour, int registrationYear, String[] imageURLs,
			int priceDiscount, boolean reduced, boolean exHire, boolean exDemo, boolean exFleet, boolean hot,
			boolean isLowMileage, int roadTaxCost) {
		super(vehicleId, price, detailsPageURL);
		this.vehicleId = vehicleId;
		this.registeredKeepers = registeredKeepers;
		this.motExpiry = motExpiry;
		this.registrationDate = registrationDate;
		this.detailsPageURL = detailsPageURL;
		this.price = price;
		this.originalPrice = originalPrice;
		this.shortFeaturesSummary = shortFeaturesSummary;
		this.featuresSummary = featuresSummary;
		this.shortDescription = shortDescription;
		this.description = description;
		this.historyCheckDescription = historyCheckDescription;
		this.historyCheckName = historyCheckName;
		this.title = title;
		this.variant = variant;
		this.manufacturer = manufacturer;
		this.model = model;
		this.distance = distance;
		this.mileage = mileage;
		this.transmission = transmission;
		this.colour = colour;
		this.registrationYear = registrationYear;
		this.imageURLs = imageURLs;
		this.priceDiscount = priceDiscount;
		this.reduced = reduced;
		this.exHire = exHire;
		this.exDemo = exDemo;
		this.exFleet = exFleet;
		this.hot = hot;
		this.isLowMileage = isLowMileage;
		this.roadTaxCost = roadTaxCost;
	}

	@Override
	public String toNotificationText() {
		StringBuilder sb = new StringBuilder();
		sb.append(cropToLength(getTitle(), 42)).append('\n');
		sb.append(cropToLength(getShortFeaturesSummary(), 42)).append('\n');
		sb.append("💰 " + String.format("£%,.2f", getPrice()));
		if (getOriginalPrice() > 0 && getOriginalPrice() != getPrice())
			sb.append(String.format(" (was £%,.2f)", getOriginalPrice()));
		sb.append('\n');
		int km = (int) (getMileage() * 1.609344);
		sb.append("📏 " + String.format("%,d miles (%,d km)", getMileage(), km)).append('\n');
		sb.append("📖 Year " + getRegistrationYear());
		sb.append(" (💸 Tax " + getRoadTaxCost() + ")").append('\n');
		if (!getHistoryCheckDescription().contains("Passed"))
			sb.append("📜 " + getHistoryCheckDescription()).append('\n');
		sb.append("🧍 " + getRegisteredKeepers()).append(" owners").append('\n');
		sb.append("⚙️ " + getTransmission());
		sb.append(", 🎨 " + getColour()).append('\n');
		sb.append("📍 Distance: " + getDistance() + " miles").append('\n');
		sb.append("📰 " + getShortDescription());
		return sb.toString();
	}

	private String cropToLength(String s, int chars) {
		if (s.length() > chars - 3) {
			return s.substring(0, chars) + "...";
		}
		return s;
	}

	@Override
	public Collection<String> getPictureURLs() {
		return Collections.unmodifiableCollection(Arrays.asList(imageURLs));
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public int getRegisteredKeepers() {
		return registeredKeepers;
	}

	public LocalDate getMotExpiry() {
		return motExpiry;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public String getDetailsPageURL() {
		return detailsPageURL;
	}

	public double getPrice() {
		return price;
	}

	public double getOriginalPrice() {
		return originalPrice;
	}

	public String getShortFeaturesSummary() {
		return shortFeaturesSummary;
	}

	public String getFeaturesSummary() {
		return featuresSummary;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getDescription() {
		return description;
	}

	public String getHistoryCheckDescription() {
		return historyCheckDescription;
	}

	public String getHistoryCheckName() {
		return historyCheckName;
	}

	public String getTitle() {
		return title;
	}

	public String getVariant() {
		return variant;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModel() {
		return model;
	}

	public int getDistance() {
		return distance;
	}

	public int getMileage() {
		return mileage;
	}

	public String getTransmission() {
		return transmission;
	}

	public String getColour() {
		return colour;
	}

	public int getRegistrationYear() {
		return registrationYear;
	}

	public String[] getImageURLs() {
		return imageURLs;
	}

	public int getPriceDiscount() {
		return priceDiscount;
	}

	public boolean isReduced() {
		return reduced;
	}

	public boolean isExHire() {
		return exHire;
	}

	public boolean isExDemo() {
		return exDemo;
	}

	public boolean isExFleet() {
		return exFleet;
	}

	public boolean isHot() {
		return hot;
	}

	public boolean isLowMileage() {
		return isLowMileage;
	}

	public int getRoadTaxCost() {
		return roadTaxCost;
	}

}
