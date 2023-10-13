package click.nullpointer.carsearch.autotrader;

public enum NotificationType {
	NEW_LISTING, PRICE_CHANGED, LISTING_GONE;

	public String toTitle() {
		if (this == NEW_LISTING)
			return "💥 New Listing!";
		if (this == PRICE_CHANGED)
			return "🔥 Listing Price Change";
		if (this == LISTING_GONE)
			return "💨 Listing Gone";
		return toString();
	}
}
