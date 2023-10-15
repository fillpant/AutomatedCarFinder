package click.nullpointer.carsearch.rac;

import click.nullpointer.carsearch.model.ISearcherConfiguration;

public class RACConfig implements ISearcherConfiguration {

	private int maxAge;
	private int maxPrice;
	private String referencePostcode;
	private long sleepBetweenRequests;
	public RACConfig(int maxAge, int maxPrice, String referencePostcode, long sleepBetweenRequests) {
		this.maxAge = maxAge;
		this.maxPrice = maxPrice;
		this.referencePostcode = referencePostcode;
	}

	public int getMaxAge() {
		return maxAge;
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
