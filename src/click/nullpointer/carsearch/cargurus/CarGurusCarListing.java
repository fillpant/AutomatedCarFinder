package click.nullpointer.carsearch.cargurus;

import java.util.Collection;
import java.util.Collections;

import com.google.gson.annotations.SerializedName;

import click.nullpointer.carsearch.model.AbstractCarListing;

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
	private double distance;
	private String vin;
	private String modelId;

	public CarGurusCarListing() {
		super(0, 0, "");
	}

	@Override
	public String toNotificationText() {
		StringBuilder sb = new StringBuilder();
		double priceDiff = getValuationPrice() - getPrice();
		String prefix = "";
		if (Math.abs(priceDiff) > 1000) {
			if (priceDiff > 0)
				prefix = "🔥 ";
			else
				prefix = "👎 ";
		}
		sb.append(prefix + getListingTitle()).append('\n');
		sb.append(getTrimName()).append('\n');
		sb.append("💰 " + String.format("£%,.0f (valued: £%,.0f)", getPrice(), getValuationPrice())).append('\n');
		sb.append("📏 " + String.format("%,d miles (%,d km)", getMileage(), (int) (getMileage() * 1.609344)))
				.append('\n');
		sb.append("📖 Year " + getCarYear()).append('\n');
		sb.append("📍 Distance from swansea: " + String.format("%.1f", getDistance()) + "mi").append('\n');
		return sb.toString();
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

	private static class CarGurusListingPictureData {
		String url;
	}

}
