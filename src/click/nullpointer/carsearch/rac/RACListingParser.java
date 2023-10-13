package click.nullpointer.carsearch.rac;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RACListingParser implements JsonDeserializer<RACCarListing> {

	private static final Pattern DATE_TO_MS = Pattern.compile("\\/Date\\((-?\\d+)\\)", Pattern.CASE_INSENSITIVE);

	public RACCarListing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
		try {
			JsonObject ob = (JsonObject) json;
			long vehicleId = ob.get("VehicleId").getAsLong();
			int registeredKeepers = ob.get("registeredKeepers").getAsInt();
			LocalDate motExpiry = parseStrangeDate(ob.get("MOTExpiryDate").getAsString());
			LocalDate registrationDate = parseStrangeDate(ob.get("registrationDate").getAsString());
			String detailsPageURL = "https://raccars.co.uk" + ob.get("DetailsPageUrl").getAsString();
			double price = ob.get("PriceIncVAT").getAsDouble();
			double originalPrice = ob.get("OriginalPrice").getAsDouble();
			String shortFeaturesSummary = ob.get("ShortFeaturesSummary").getAsString();
			String featuresSummary = ob.get("FeaturesSummary").getAsString();
			String shortDescription = ob.get("ShortDescription").getAsString();
			String description = ob.get("Description").getAsString();
			String historyCheckDescription = ob.get("HistoryCheckDescription").getAsString();
			String historyCheckName = ob.get("HistoryCheckName").getAsString();
			String title = ob.get("Title").getAsString();
			String variant = ob.get("Variant").getAsString();
			String manufacturer = ob.get("Manufacturer").getAsString();
			String model = ob.get("Model").getAsString();
			int distance = ob.get("Distance").getAsInt();
			int mileage = ob.get("MileageInt").getAsInt();
			String transmission = ob.get("Transmission").getAsString();
			String colour = ob.get("Colour").getAsString();
			int registrationYear = ob.get("RegistrationYear").getAsInt();
			String[] imageURLs;
			if (ob.has("Images") && ob.get("Images").isJsonArray()) {
				JsonArray imgs = ob.getAsJsonArray("Images");
				imageURLs = new String[imgs.size()];
				for (int i = 0; i < imgs.size(); ++i) {
					imageURLs[i] = imgs.get(i).getAsJsonObject().get("Normal").getAsString();
				}
			} else {
				imageURLs = new String[0];
			}

			int priceDiscount = ob.get("PriceDiscount").getAsInt();
			boolean reduced = ob.get("Reduced").getAsBoolean();
			boolean exHire = ob.get("IsExHire").getAsBoolean();
			boolean exDemo = ob.get("IsExDemo").getAsBoolean();
			boolean exFleet = ob.get("IsExFleet").getAsBoolean();
			boolean hot = ob.get("Hot").getAsBoolean();
			boolean isLowMileage = ob.get("IsLowMileage").getAsBoolean();
			String roadTax = ob.get("RoadTax").getAsString().replaceAll("[^0-9]", "");
			int roadTaxInt = Integer.parseInt("0" + roadTax);
			return new RACCarListing(vehicleId, registeredKeepers, motExpiry, registrationDate, detailsPageURL, price,
					originalPrice, shortFeaturesSummary, featuresSummary, shortDescription, description,
					historyCheckDescription, historyCheckName, title, variant, manufacturer, model, distance, mileage,
					transmission, colour, registrationYear, imageURLs, priceDiscount, reduced, exHire, exDemo, exFleet,
					hot, isLowMileage, roadTaxInt);
		} catch (Exception e) {
			System.err.println("Failed to parse RAC listing! -- Ignoring.");
			e.printStackTrace();
			return null;
		}
	}

	private LocalDate parseStrangeDate(String dat) {
		if (dat != null && !dat.isEmpty()) {
			Matcher m = DATE_TO_MS.matcher(dat);
			if (m.find()) {
				long ms = Long.parseLong(m.group(1));
				return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate();
			}
		}
		return null;
	}

}
