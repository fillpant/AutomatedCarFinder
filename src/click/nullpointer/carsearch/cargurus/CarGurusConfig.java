package click.nullpointer.carsearch.cargurus;

import click.nullpointer.carsearch.model.ISearcherConfiguration;

public class CarGurusConfig implements ISearcherConfiguration {

	private final int minYear;
	private final int maxPrice;
	private final String referencePostcode;
	private final long sleepBetweenRequests;

	public CarGurusConfig(int minYear, int maxPrice, String referencePostcode, long sleepBetweenRequests) {
		this.minYear = minYear;
		this.maxPrice = maxPrice;
		this.referencePostcode = referencePostcode;
		this.sleepBetweenRequests = sleepBetweenRequests;
	}

	public int getMinYear() {
		return minYear;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public String getReferencePostcode() {
		return referencePostcode;
	}

	public long getSleepBetweenRequests() {
		return sleepBetweenRequests;
	}

	@Override
	public int getConfigVersion() {
		return 0;
	}

}
