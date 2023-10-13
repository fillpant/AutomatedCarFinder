package click.nullpointer.carsearch.ebay;

import click.nullpointer.carsearch.model.ISearcherConfiguration;

public class EbaySearcherConfig implements ISearcherConfiguration {

	private static final int VERSION = 0;
	private int categoryId;
	private String searchQuerry;
	private String appKey;
	private String[] modelYears;
	private double minPrice, maxPrice;
	private String referencePostcode;

	public EbaySearcherConfig(int categoryId, String searchQuerry, String appKey, String[] modelYears, double minPrice,
			double maxPrice, String referencePostcode) {
		this.categoryId = categoryId;
		this.searchQuerry = searchQuerry;
		this.appKey = appKey;
		this.modelYears = modelYears;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.referencePostcode = referencePostcode;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getSearchQuerry() {
		return searchQuerry;
	}

	public void setSearchQuerry(String searchQuerry) {
		this.searchQuerry = searchQuerry;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String[] getModelYears() {
		return modelYears;
	}

	public void setModelYears(String[] modelYears) {
		this.modelYears = modelYears;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getReferencePostcode() {
		return referencePostcode;
	}

	public void setReferencePostcode(String referencePostcode) {
		this.referencePostcode = referencePostcode;
	}

	@Override
	public int getConfigVersion() {
		return VERSION;
	}

}
