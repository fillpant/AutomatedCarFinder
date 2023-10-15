package click.nullpointer.carsearch.autotrader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ListingDetail;
import click.nullpointer.carsearch.model.ListingDetail.StandardListingDetails;

public class AutotraderSearchListings extends AbstractCarListing {

	public long advertId; // Seems to be a unique listing ID?
	public String type;// We're not concerned with GPT_LISTING, just NATURAL_LISTING
	public String title;
	public String subTitle;
	public String attentionGrabber;
	public String price;
	public String bodyType;// SUV etc.
	public int distance;// distance of car from request postcode
	public String description;
	public String location;
	@SerializedName("images")
	public String[] someImageURLs;// URLs to images from the listing, just a handfull
	public int numberOfImages;// Total number of images the listing has
	public String sellerType; // "PRIVATE" etc
	public String fpaLink; // Link to listing
	public boolean hasVideo;
	public boolean has360Spin;
	public String mileageText;// Mileage as text (...)
	public String yearAndPlateText; // Naturaly... -_- "2015 (15 reg)" or something.
	public String[] specs;// Array of specs. Who knows what it contains! Fuel type, Transmission type,
	// horse power, miles, type, year, etc.

	private Integer parsedPrice, parsedMileage, parsedYear;

	public AutotraderSearchListings(double price, String listingURL) {
		super(0L, 0, "");// Dummy constuctor. Getters are overriden as this object is filled by gson.
	}

	@Override
	public long getListingUniqueID() {
		return advertId;
	}

	@Override
	public String getListingURL() {
		return "https://autotrader.co.uk" + fpaLink;
	}

	@Override
	public double getPrice() {
		return tryParsePrice().orElse(-1);
	}

	@Override
	public Collection<String> getPictureURLs() {
		return Collections.unmodifiableCollection(Arrays.asList(someImageURLs));
	}

	public Optional<Integer> tryExtractOwnerCnt() {
		if (specs != null) {
			return tryParse(Arrays.stream(specs).filter(a -> a.contains("owner")).findAny().orElse(""));
		}
		return Optional.empty();
	}

	public Optional<String> tryGetTransmission() {
		if (specs != null) {
			List<String> sp = Arrays.asList(specs);
			boolean auto = sp.contains("Automatic");
			boolean man = sp.contains("Manual");
			if (auto ^ man) {
				return Optional.of(auto ? "Automatic" : "Manual");
			}
		}
		return Optional.empty();
	}

	public Optional<String> tryExtractOwnerInfo() {
		if (specs != null) {
			return Arrays.stream(specs).filter(a -> a.contains("owner")).findAny();
		}
		return Optional.empty();
	}

	public Optional<Integer> tryParseYear() {
		if (yearAndPlateText != null && yearAndPlateText.indexOf(' ') != -1) {
			return parsedYear == null ? Optional
					.of(parsedYear = tryParse(yearAndPlateText.substring(0, yearAndPlateText.indexOf(' '))).orElse(-1))
					: Optional.of(parsedYear);
		}
		return Optional.empty();
	}

	public Optional<Integer> tryParseMileage() {
		return parsedMileage == null ? Optional.of(parsedMileage = tryParse(mileageText).orElse(-1))
				: Optional.of(parsedMileage);
	}

	public Optional<Integer> tryParsePrice() {
		return parsedPrice == null ? Optional.of(parsedPrice = tryParse(price).orElse(-1)) : Optional.of(parsedPrice);
	}

	private Optional<Integer> tryParse(String pr) {
		if (pr != null) {
			pr = pr.replaceAll("[^0-9]", "");
			if (!pr.isEmpty())
				return Optional.of(Integer.valueOf(pr));
		}
		return Optional.empty();
	}

	@Override
	public List<ListingDetail<?>> getDetails() {
		List<ListingDetail<?>> details = new ArrayList<>();
		details.add(StandardListingDetails.title(title));
		details.add(StandardListingDetails.subtitle(subTitle));
		details.add(StandardListingDetails.price(getPrice()));
		if (tryParseMileage().isPresent())
			details.add(StandardListingDetails.mileage(tryParseMileage().get()));
		else
			details.add(new ListingDetail<>("Mileage", mileageText, f -> f));
		if (tryParseYear().isPresent())
			details.add(StandardListingDetails.year(tryParseYear().get()));
		else
			details.add(new ListingDetail<>("Year", yearAndPlateText, f -> f));
		if (tryGetTransmission().isPresent())
			details.add(StandardListingDetails.transmission(tryGetTransmission().get()));
		if (tryExtractOwnerCnt().isPresent())
			details.add(StandardListingDetails.prevKeepers(tryExtractOwnerCnt().get()));
		details.add(StandardListingDetails.distance(distance));
		if (description != null)
			details.add(StandardListingDetails.description(description));
		details.add(StandardListingDetails.location(location));

		if (attentionGrabber != null)
			details.add(StandardListingDetails.shortDescription(attentionGrabber));
		details.add(StandardListingDetails.photoCount(numberOfImages));
		return details;
	}
//
//	@Override
//	public String toNotificationText() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(title).append('\n');
//		if (subTitle != null && !subTitle.trim().isEmpty())
//			sb.append(subTitle).append('\n');
//		sb.append("💰 " + price);
//		sb.append('\n');
//		int miles = tryParseMileage().orElse(-1);
//		int km = (int) (miles * 1.609344);
//		sb.append("📏 " + (miles == -1 ? mileageText : (String.format("%,d miles (%,d km)", miles, km))));
//		sb.append('\n');
//		sb.append("📖 Year " + (tryParseYear().isPresent() ? tryParseYear().get() : yearAndPlateText)).append('\n');
//		if (tryGetTransmission().isPresent())
//			sb.append("⚙️ " + tryGetTransmission().get()).append('\n');
//		if (tryExtractOwnerCnt().isPresent())
//			sb.append("🧍 " + tryExtractOwnerCnt().get()).append(" owners").append('\n');
//		sb.append("📸 " + numberOfImages);
//		return sb.toString();
//	}

}
