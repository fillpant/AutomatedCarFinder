package click.nullpointer.carsearch.motorscouk;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MotorsCoUkListingParser implements JsonDeserializer<MotorsCoUkCarListing> {

	public MotorsCoUkCarListing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
		try {
			JsonObject ob = (JsonObject) json;
			long id = ob.get("VehicleId").getAsLong();
			String bodyType = ob.get("BodyStyle").getAsString();
			String engineLitres = ob.get("EngineSizeLitres").getAsString();
			double distance = ob.get("Distance").getAsDouble();
			boolean isReduced = ob.get("Reduced").getAsBoolean();
			boolean isExDemo = ob.get("IsExDemo").getAsBoolean();
			boolean isLowMileage = ob.get("IsLowMileage").getAsBoolean();
			String colour = ob.has("Colour") ? ob.get("Colour").getAsString() : "";
			String listingURL = "https://motors.co.uk" + ob.get("DetailsPageUrl").getAsString();
			double price = ob.get("Price").getAsDouble();
			String originalPrice = ob.get("GBPOriginalPrice").getAsString();
			int imageCnt = ob.get("ImageCount").getAsInt();
			boolean isPriceExcludingVat = ob.has("IsPriceExcludingVAT") ? ob.get("IsPriceExcludingVAT").getAsBoolean()
					: false;
			int registrationYear = ob.get("RegistrationYear").getAsInt();
			boolean sold = ob.get("Sold").getAsBoolean();
			String varian = ob.get("Variant").getAsString();
			String model = ob.get("Model").getAsString();
			String title = ob.get("Title").getAsString();
			int mileage = ob.get("MileageInt").getAsInt();
			String fuelType = ob.get("FuelType").getAsString();
			String[] images;
			if (ob.has("FeaturedAdvertImages") && !ob.get("FeaturedAdvertImages").isJsonNull()) {
				images = new String[ob.getAsJsonArray("FeaturedAdvertImages").size()];
				JsonArray a = ob.getAsJsonArray("FeaturedAdvertImages");
				for (int i = 0; i < a.size(); ++i)
					images[i] = a.get(i).getAsJsonObject().get("Medium").getAsString();
			} else if (ob.has("MainImage") && !ob.get("MainImage").isJsonNull()) {
				images = new String[1];
				images[0] = ob.get("MainImage").getAsJsonObject().get("Medium").getAsString();
			} else {
				images = new String[0];
			}

			return new MotorsCoUkCarListing(id, price, listingURL, bodyType, engineLitres, distance, isReduced,
					isExDemo, isLowMileage, colour, originalPrice, imageCnt, isPriceExcludingVat, registrationYear,
					sold, varian, model, title, mileage, fuelType, images);
		} catch (Exception e) {
			System.err.println("Failed to parse Motors.co.uk listing! -- Ignoring.");
			e.printStackTrace();
			return null;
		}
	}

}
