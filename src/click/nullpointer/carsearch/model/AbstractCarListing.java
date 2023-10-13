package click.nullpointer.carsearch.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractCarListing {

	protected long listingUniqueID;
	protected double aclPrice;
	protected String aclListingURL;
	protected ICarSearcher theSearcher;

	public AbstractCarListing(long listingUniqueID, double price, String listingURL) {
		this.listingUniqueID = listingUniqueID;
		this.aclPrice = price;
		this.aclListingURL = listingURL;
	}

	public Optional<ICarSearcher> getSearcher() {
		return Optional.ofNullable(theSearcher);
	}

	// Below methods are likely getting overriden...
	public double getPrice() {
		return aclPrice;
	}

	public String getListingURL() {
		return aclListingURL;
	}

	public long getListingUniqueID() {
		return listingUniqueID;
	}

	public abstract String toNotificationText();

	public Collection<String> getPictureURLs() {
		return Collections.emptyList();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getListingURL(), getPrice(), getListingUniqueID());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCarListing other = (AbstractCarListing) obj;
		return Objects.equals(getListingURL(), other.getListingURL())
				&& Double.doubleToLongBits(getPrice()) == Double.doubleToLongBits(other.getPrice())
				&& getListingUniqueID() == other.getListingUniqueID();
	}

	public void setSearcher(ICarSearcher theSearcher) {
		this.theSearcher = theSearcher;
	}

}
