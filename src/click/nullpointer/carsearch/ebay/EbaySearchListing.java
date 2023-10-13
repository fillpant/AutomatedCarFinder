package click.nullpointer.carsearch.ebay;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import click.nullpointer.carsearch.model.AbstractCarListing;

public class EbaySearchListing extends AbstractCarListing {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final Pattern CAT_SCRAP = Pattern.compile("cat(egory)? [absn]{1} ", Pattern.CASE_INSENSITIVE);
	private static final Pattern GALERY_IMAGE_RESIZER = Pattern.compile("(.+?s-l)(\\d+)(\\..+)");
	private long itemId;
	private String title;
	private String subtitle;
	private String galleryURL;
	private String viewItemURL;
	private String location;
	private String country;
	private String currency;
	private double currentPrice;
	private Optional<Double> currentBid;
	private String timeLeft;
	private boolean acceptOffers;
	private boolean acceptBuyNow;
	private String startTime;
	private String endTime;
	private String listingType;
	private int watchCount;
	private double distance;
	private String distanceUnit;

	protected EbaySearchListing(long itemId, String title, String subtitle, String galleryURL, String viewItemURL,
			String location, String country, String currency, double currentPrice, Optional<Double> currentBid,
			String timeLeft, boolean acceptOffers, boolean acceptBuyNow, String startTime, String endTime,
			String listingType, int watchCount, double distance, String distanceUnit) {
		super(itemId, currentPrice, viewItemURL);
		this.itemId = itemId;
		this.title = title;
		this.subtitle = subtitle;
		this.galleryURL = galleryURL;
		this.viewItemURL = viewItemURL;
		this.location = location;
		this.country = country;
		this.currency = currency;
		this.currentPrice = currentPrice;
		this.currentBid = currentBid;
		this.timeLeft = timeLeft;
		this.acceptOffers = acceptOffers;
		this.acceptBuyNow = acceptBuyNow;
		this.startTime = startTime;
		this.endTime = endTime;
		this.listingType = listingType;
		this.watchCount = watchCount;
		this.distance = distance;
		this.distanceUnit = distanceUnit;
	}

	public long getItemId() {
		return itemId;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getGalleryURL() {
		return galleryURL;
	}

	public String getViewItemURL() {
		return viewItemURL;
	}

	public String getLocation() {
		return location;
	}

	public String getCountry() {
		return country;
	}

	public String getCurrency() {
		return currency;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public Optional<Double> getCurrentBid() {
		return currentBid;
	}

	public Duration getTimeLeft() {
		return Duration.parse(timeLeft);
	}

	public boolean isAcceptOffers() {
		return acceptOffers;
	}

	public boolean isAcceptBuyNow() {
		return acceptBuyNow;
	}

	public LocalDateTime getStartTime() {
		return LocalDateTime.parse(startTime, DATE_FORMATTER);
	}

	public LocalDateTime getEndTime() {
		return LocalDateTime.parse(endTime, DATE_FORMATTER);
	}

	public String getListingType() {
		return listingType;
	}

	public int getWatchCount() {
		return watchCount;
	}

	public double getDistance() {
		return distance;
	}

	public String getDistanceUnit() {
		return distanceUnit;
	}

	@Override
	public String toNotificationText() {
		StringBuilder sb = new StringBuilder();
		String title = getTitle();
		if (title.length() > 42)
			title = title.substring(0, 39) + "...";
		if (containsBadWords(getTitle() + " " + getSubtitle()))
			title = "⚠️⚠️" + title;
		String subtitle = getSubtitle();
		if (subtitle.length() > 42)
			subtitle = subtitle.substring(0, 39) + "...";

		sb.append(title).append('\n');
		if (!getSubtitle().trim().isEmpty())
			sb.append(subtitle).append('\n');
		sb.append("💰 " + String.format("£%,.2f", getCurrentPrice()));
		if (getCurrentBid().isPresent())
			sb.append(String.format(" (bid: £%,.2f)", getCurrentBid().get()));
		sb.append('\n');
		sb.append("🤓 " + getWatchCount() + " watches").append('\n');
		Duration durLeft = getTimeLeft();
		long dh = durLeft.getSeconds() / 3600;
		long dm = (durLeft.getSeconds() % 3600) / 60;
		long ds = durLeft.getSeconds() % 60;
		String left = String.format("%d:%02d:%02d", dh, dm, ds);
		sb.append("⌚ " + left).append('\n');
		sb.append("🏁 " + getEndTime().toString().replace("T", " ")).append('\n');
		sb.append("🧮 Type: " + getListingType());
		return sb.toString();
	}

	// Ebay does something funky. Galery images are sized based on the last
	// parameter:
	// https://i.ebayimg.com/thumbs/images/g/3M4AAOSwau5izAHm/s-l720.jpg
	// 720 is the size. Other sizes are 280 (Default?) 480, 720.
	// This method auto-replaces this with 720.
	@Override
	public Collection<String> getPictureURLs() {
		Matcher m = GALERY_IMAGE_RESIZER.matcher(galleryURL);
		if (m.find()) {
			int size = Integer.parseInt(m.group(2));
			if (size < 720)
				galleryURL = m.replaceFirst("$1720$3");
		}
		return Collections.singleton(galleryURL);

	}

	private static boolean containsBadWords(String title) {
		title = title.toLowerCase();
		title = title.replaceAll(" +", " ");
		boolean damage = title.contains("damage") && !title.contains("no damage") && !title.contains("damage free");
		boolean accident = title.contains("crash") && !title.contains("no crash") && !title.contains("crash free");
		boolean salvage = title.contains("salvage");
		boolean catABSN = CAT_SCRAP.matcher(title).find();
		boolean scrap = title.contains("scrap") || title.contains("write-off") || title.contains("writeoff")
				|| title.contains("written off");
		boolean rubbish = title.contains("upgrade only");

		return damage | accident | salvage | catABSN | scrap | rubbish;
	}

}
