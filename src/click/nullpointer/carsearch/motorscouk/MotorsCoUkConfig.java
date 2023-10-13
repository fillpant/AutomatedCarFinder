package click.nullpointer.carsearch.motorscouk;

import click.nullpointer.carsearch.model.ISearcherConfiguration;

public class MotorsCoUkConfig implements ISearcherConfiguration {
	private final String make;
	private final String model;
	private final int minYear;
	private final int maxYear;
	private final int maxPrice;
	private final String referencePostcode;

	public MotorsCoUkConfig(String make, String model, int minYear, int maxYear, int maxPrice,
			String referencePostcode) {
		this.make = make;
		this.model = model;
		this.minYear = minYear;
		this.maxYear = maxYear;
		this.maxPrice = maxPrice;
		this.referencePostcode = referencePostcode;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public int getMinYear() {
		return minYear;
	}

	public int getMaxYear() {
		return maxYear;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public String getReferencePostcode() {
		return referencePostcode;
	}

	@Override
	public int getConfigVersion() {
		return 0;
	}

}
