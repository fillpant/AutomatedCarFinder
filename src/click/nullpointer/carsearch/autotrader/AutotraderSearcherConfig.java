package click.nullpointer.carsearch.autotrader;

import click.nullpointer.carsearch.model.ISearcherConfiguration;

public class AutotraderSearcherConfig implements ISearcherConfiguration {
	private static final int VERSION = 0;
	private String make;
	private String model;
	private int minYear;
	private int maxPrice;
	private String referencePostcode;
	private int sleepForMsBetweenRequests;

	public AutotraderSearcherConfig(String make, String model, int minYear, int maxPrice, String referencePostcode,
			int sleepForMsBetweenRequests) {
		this.make = make;
		this.model = model;
		this.minYear = minYear;
		this.maxPrice = maxPrice;
		this.referencePostcode = referencePostcode;
		this.sleepForMsBetweenRequests = sleepForMsBetweenRequests;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getMinYear() {
		return minYear;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getReferencePostcode() {
		return referencePostcode;
	}

	public void setReferencePostcode(String referencePostcode) {
		this.referencePostcode = referencePostcode;
	}

	public int getSleepForMsBetweenRequests() {
		return sleepForMsBetweenRequests;
	}

	public void setSleepForMsBetweenRequests(int sleepForMsBetweenRequests) {
		this.sleepForMsBetweenRequests = sleepForMsBetweenRequests;
	}

	@Override
	public int getConfigVersion() {
		return VERSION;
	}

}
