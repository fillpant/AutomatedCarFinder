package click.nullpointer.carsearch.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractCarListing {

	protected long firstSeen;
	protected long listingUniqueID;
	protected double aclPrice;
	protected String aclListingURL;
	protected transient ICarSearcher theSearcher;

	public AbstractCarListing(long listingUniqueID, double price, String listingURL) {
		this.listingUniqueID = listingUniqueID;
		this.aclPrice = price;
		this.aclListingURL = listingURL;
		this.firstSeen = System.currentTimeMillis();
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

	public void setFirstSeen(long firstSeen) {
		this.firstSeen = firstSeen;
	}

	public long getFirstSeen() {
		return firstSeen;
	}

	public abstract List<ListingDetail<?>> getDetails();

	public final String toNotificationText() {
		final int MAX_LINE_LENGTH = 45;
		final int LINES_IN_NOTIF = 10;
		StringBuilder sb = new StringBuilder();
		List<ListingDetail<?>> ld = getDetails();
		String title = ld.stream().filter(a -> a.getType().equals("Title")).findAny().map(a -> a.getFormattedValue())
				.orElse(null);
		String subTitle = ld.stream().filter(a -> a.getType().equals("Subitle")).findAny()
				.map(a -> a.getFormattedValue()).orElse(null);
		if (title != null)
			sb.append(title).append("\n");
		if (subTitle != null)
			sb.append(subTitle).append("\n");

		int cnt = LINES_IN_NOTIF - (title == null ? 0 : 1) - (subTitle == null ? 0 : 1);
		ld.removeIf(a -> a.getType().equals("Title") || a.getType().equals("Subtitle"));
		ld.stream().limit(cnt).map(a -> trimToLength(a.toString(), MAX_LINE_LENGTH) + "\n").forEach(sb::append);
		return sb.toString();
	}

	// Assymes length >3
	private String trimToLength(String s, int length) {
		if (s.length() > length) {
			return s.substring(0, length - 3) + "...";
		}
		return s;
	}

//	@Deprecated
//	public abstract String toNotificationText();

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
