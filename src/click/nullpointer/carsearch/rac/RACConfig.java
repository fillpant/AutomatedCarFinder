package click.nullpointer.carsearch.rac;

import click.nullpointer.carsearch.model.ISearcherConfiguration;

public class RACConfig implements ISearcherConfiguration {

	private int maxAge;
	private int maxPrice;
	private String referencePostcode;

	public RACConfig(int maxAge, int maxPrice, String referencePostcode) {
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

	@Override
	public int getConfigVersion() {
		return 0;
	}

}
