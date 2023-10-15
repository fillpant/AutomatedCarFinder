package click.nullpointer.carsearch.cargurus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ListingDetail;
import click.nullpointer.carsearch.model.ListingDetail.StandardListingDetails;

public class CarGurusCarListing extends AbstractCarListing {
	private long id;
	private String listingTitle;
	private String makeName;
	private String modelName;
	private int carYear;
	private String trimName;
	private String[] options;
	private int mileage;
	private double price;
	@SerializedName("expectedPrice")
	private double valuationPrice;
	private CarGurusListingPictureData originalPictureData;
	private String normalizedExteriorColor;
	private int daysOnMarket;
	private String dealRating;
	private String sellerType;
	private String sellerCity;
	private String sellerPostcode;
	private String phoneNumber;
	private CarGurusListingUnitData cityFuelEconomy;
	private CarGurusListingUnitData highwayFuelEconomy;
	private CarGurusListingUnitData combinedFuelEconomy;
	private String localizedFuelType;
	private boolean hasVehicleHistory;
	private double distance;
	private String vin;
	private String modelId;

	public CarGurusCarListing() {
		super(0, 0, "");
	}

	@Override
	public List<ListingDetail<?>> getDetails() {
		List<ListingDetail<?>> details = new ArrayList<>();
		details.add(StandardListingDetails.title(listingTitle));
		details.add(StandardListingDetails.valuationPrice(valuationPrice));
		details.add(StandardListingDetails.price(price));
		details.add(StandardListingDetails.mileage(mileage));
		details.add(StandardListingDetails.year(carYear));
		details.add(StandardListingDetails.vin(vin));
		details.add(StandardListingDetails.distance(distance));
		if (sellerCity != null)
			details.add(StandardListingDetails
					.location(sellerCity + (sellerPostcode == null ? "" : (" " + sellerPostcode))));
		details.add(new ListingDetail<>("Days on Market", daysOnMarket, f -> f.toString(), "📅"));
		details.add(new ListingDetail<>("Deal Rating", dealRating, f -> f, "⭐️"));
		details.add(new ListingDetail<>("Sale Type", sellerType, f -> f, "👤"));
		if (phoneNumber != null)
			details.add(new ListingDetail<>("Phone", phoneNumber, f -> f, "☎️"));
		details.add(StandardListingDetails.colour(normalizedExteriorColor));
		details.add(StandardListingDetails.make(makeName));
		details.add(StandardListingDetails.model(modelName));
		details.add(
				new ListingDetail<>("Options", options, f -> Arrays.stream(f).collect(Collectors.joining(", ")), "💺"));
		details.add(StandardListingDetails.fuelType(localizedFuelType));
		if (cityFuelEconomy != null)
			details.add(StandardListingDetails.mpgUrban(cityFuelEconomy.value));
		if (highwayFuelEconomy != null)
			details.add(StandardListingDetails.mpgMotorway(highwayFuelEconomy.value));
		if (combinedFuelEconomy != null)
			details.add(StandardListingDetails.mpgCombined(combinedFuelEconomy.value));
		details.add(StandardListingDetails.photoCount(originalPictureData == null ? 0 : 1));
		details.add(StandardListingDetails.subtitle(trimName));
		details.add(
				new ListingDetail<>("Vehicle History", hasVehicleHistory ? "Available" : "Unavailable", f -> f, "📜"));
		return details;
	}

//	@Override
//	public String toNotificationText() {
//		StringBuilder sb = new StringBuilder();
//		double priceDiff = getValuationPrice() - getPrice();
//		String prefix = "";
//		if (Math.abs(priceDiff) > 1000) {
//			if (priceDiff > 0)
//				prefix = "🔥 ";
//			else
//				prefix = "👎 ";
//		}
//		sb.append(prefix + getListingTitle()).append('\n');
//		sb.append(getTrimName()).append('\n');
//		sb.append("💰 " + String.format("£%,.0f (valued: £%,.0f)", getPrice(), getValuationPrice())).append('\n');
//		sb.append("📏 " + String.format("%,d miles (%,d km)", getMileage(), (int) (getMileage() * 1.609344)))
//				.append('\n');
//		sb.append("📖 Year " + getCarYear()).append('\n');
//		sb.append("📍 Distance from swansea: " + String.format("%.1f", getDistance()) + "mi").append('\n');
//		return sb.toString();
//	}

	@Override
	public long getFirstSeen() {
		return System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysOnMarket);
	}

	@Override
	public long getListingUniqueID() {
		return id;
	}

	@Override
	public String getListingURL() {
		return "https://www.cargurus.co.uk/Cars/inventorylisting/viewDetailsFilterViewInventoryListing.action?sourceContext=carGurusHomePageModel&entitySelectingHelper.selectedEntity="
				+ modelId + "#listing=" + id + "/NONE/DEFAULT";
	}

	@Override
	public Collection<String> getPictureURLs() {
		return Collections.singleton(originalPictureData == null ? null : originalPictureData.url);
	}

	public long getId() {
		return id;
	}

	public String getListingTitle() {
		return listingTitle;
	}

	public String getMakeName() {
		return makeName;
	}

	public String getModelName() {
		return modelName;
	}

	public int getCarYear() {
		return carYear;
	}

	public String getTrimName() {
		return trimName;
	}

	public String[] getOptions() {
		return options;
	}

	public int getMileage() {
		return mileage;
	}

	public double getPrice() {
		return price;
	}

	public double getValuationPrice() {
		return valuationPrice;
	}

	public double getDistance() {
		return distance;
	}

	public String getVin() {
		return vin;
	}

	public String getModelId() {
		return modelId;
	}

	private static class CarGurusListingUnitData {
		double value;
		String unit;
	}

	private static class CarGurusListingPictureData {
		String url;
	}

}
