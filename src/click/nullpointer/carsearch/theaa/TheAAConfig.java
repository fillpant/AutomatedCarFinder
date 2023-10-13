package click.nullpointer.carsearch.theaa;

import click.nullpointer.carsearch.model.ISearcherConfiguration;

public class TheAAConfig implements ISearcherConfiguration {
	private final String referencePostcode;
	private final int minPrice;
	private final int maxPrice;
	private final int ageYears;
	private final int modelID;
	private final int makeID;

	public TheAAConfig(String referencePostcode, int minPrice, int maxPrice, int ageYears, int modelID, int makeID) {
		this.referencePostcode = referencePostcode;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.ageYears = ageYears;
		this.modelID = modelID;
		this.makeID = makeID;
	}

	public String getReferencePostcode() {
		return referencePostcode;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public int getAgeYears() {
		return ageYears;
	}

	public int getModelID() {
		return modelID;
	}

	public int getMakeID() {
		return makeID;
	}

	@Override
	public int getConfigVersion() {
		return 0;
	}

}
