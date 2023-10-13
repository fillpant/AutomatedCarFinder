package click.nullpointer.carsearch.ebay;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class EbaySearchListingParser implements JsonDeserializer<EbaySearchListing> {

	public EbaySearchListing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
		try {
			JsonObject ob = (JsonObject) json;
			long itemId = ob.getAsJsonArray("itemId").get(0).getAsLong();
			String title = getFirstStringOrDefault(ob.getAsJsonArray("title"), "<blank>");
			String subtitle = getFirstStringOrDefault(ob.getAsJsonArray("subtitle"), "");
			String galleryURL = getFirstStringOrDefault(ob.getAsJsonArray("galleryURL"), "https://ebay.co.uk/");
			String viewItemURL = getFirstStringOrDefault(ob.getAsJsonArray("viewItemURL"), "https://ebay.co.uk/");
			String location = getFirstStringOrDefault(ob.getAsJsonArray("location"), "");
			String country = getFirstStringOrDefault(ob.getAsJsonArray("country"), "");
			String currency = ob.getAsJsonArray("sellingStatus").get(0).getAsJsonObject().getAsJsonArray("currentPrice")
					.get(0).getAsJsonObject().get("@currencyId").getAsString();
			double currentPrice = ob.getAsJsonArray("sellingStatus").get(0).getAsJsonObject()
					.getAsJsonArray("currentPrice").get(0).getAsJsonObject().get("__value__").getAsDouble();
			String timeLeft = ob.getAsJsonArray("sellingStatus").get(0).getAsJsonObject().getAsJsonArray("timeLeft")
					.get(0).getAsString();
			JsonObject linfo = ob.getAsJsonArray("listingInfo").get(0).getAsJsonObject();
			boolean acceptOffers = linfo.getAsJsonArray("bestOfferEnabled").get(0).getAsBoolean();
			boolean acceptBuyNow = linfo.getAsJsonArray("buyItNowAvailable").get(0).getAsBoolean();
			Optional<Double> bid = Optional.empty();
			if (linfo.has("buyItNowPrice")) {
				bid = Optional.of(currentPrice);
				currentPrice = linfo.getAsJsonArray("buyItNowPrice").get(0).getAsJsonObject().get("__value__")
						.getAsDouble();
			}
			String startTime = getFirstStringOrDefault(linfo.getAsJsonArray("startTime"), "");
			String endTime = getFirstStringOrDefault(linfo.getAsJsonArray("endTime"), "");
			String listingType = getFirstStringOrDefault(linfo.getAsJsonArray("listingType"), "");
			int watchCount = linfo.has("watchCount") ? linfo.getAsJsonArray("watchCount").get(0).getAsInt() : 0;

			double distance = ob.getAsJsonArray("distance").get(0).getAsJsonObject().get("__value__").getAsDouble();
			String distanceUnit = ob.getAsJsonArray("distance").get(0).getAsJsonObject().get("@unit").getAsString();
			return new EbaySearchListing(itemId, title, subtitle, galleryURL, viewItemURL, location, country, currency,
					currentPrice, bid, timeLeft, acceptOffers, acceptBuyNow, startTime, endTime, listingType,
					watchCount, distance, distanceUnit);
		} catch (Exception e) {
			System.err.println("Failed to parse ebay listing! -- Ignoring.");
			e.printStackTrace();
			return null;
		}
	}

	private static String getFirstStringOrDefault(JsonArray arr, String defaultz) {
		if (arr == null || arr.size() < 1 || arr.get(0) == null)
			return defaultz;
		return arr.get(0).getAsString();
	}

}