package click.nullpointer.carsearch;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import click.nullpointer.carsearch.model.AbstractCarListing;
import click.nullpointer.carsearch.model.ICarSearcher;
import click.nullpointer.carsearch.model.ListingDetail;

public class SimpleHTMLCarDetailsExporter {

	public static String getJsonPayloadForView(List<AbstractCarListing> lsts) {
		Map<ICarSearcher, List<AbstractCarListing>> perSearcher = new HashMap<>();
		lsts.forEach(a -> perSearcher.compute(a.getSearcher().orElse(null), (k, v) -> {
			if (v == null)
				return new ArrayList<>(Collections.singleton(a));
			else {
				v.add(a);
				return v;
			}
		}));
		AtomicInteger incrementalCounter = new AtomicInteger();
		Gson g = new GsonBuilder().registerTypeAdapter(ListingDetail.class, new DetailSerialiser()).create();
		JsonObject root = new JsonObject();
		root.addProperty("totalListingCount", lsts.size());
		JsonArray arr = new JsonArray();
		perSearcher.entrySet().stream().map(e -> {
			JsonObject entry = new JsonObject();
			entry.addProperty("crawler", e.getKey() == null ? "Unspecified" : e.getKey().getPrettySearcherName());
			entry.addProperty("listingCount", e.getValue().size());
			JsonArray listings = new JsonArray();
			e.getValue().stream().map(a -> {
				JsonObject o = new JsonObject();
				o.addProperty("firstSeen", a.getFirstSeen());
				o.addProperty("autoScrollImages", false);// for now. For some listings we may want this true.
				o.addProperty("price", a.getPrice());
				o.addProperty("priceString", String.format("%,.2f", a.getPrice()));
				o.addProperty("shortDescription", a.toNotificationText());
				o.addProperty("listingUrl", a.getListingURL());
				o.add("details", g.toJsonTree(a.getDetails()));
				JsonArray images = new JsonArray();
				for (String s : a.getPictureURLs())
					images.add(s);
				o.add("imageUrls", images);
				o.addProperty("imageCnt", images.size());
				String uid = a.getListingUniqueID() + "" + incrementalCounter.getAndIncrement();
				o.addProperty("listingUid", new BigInteger(uid).toString(Character.MAX_RADIX));
				return o;
			}).forEach(listings::add);
			entry.add("listings", listings);
			return entry;
		}).forEach(arr::add);
		root.add("results", arr);
		return g.toJson(root);
	}

	public static String getFrontEndView() throws IOException {
		return IOUtils.toString(SimpleHTMLCarDetailsExporter.class.getResourceAsStream("cars.html"), "UTF-8");
//		lsts.sort((a, b) -> {
//			ICarSearcher as = a.getSearcher().orElse(null);
//			ICarSearcher bs = b.getSearcher().orElse(null);
//			int cmp = 0;
//			if (as == null && bs != null)
//				cmp = -1;
//			if (as != null && bs == null)
//				cmp = 1;
//			if (cmp == 0)
//				cmp = as.getPrettySearcherName().compareTo(bs.getPrettySearcherName());
//			return cmp;
//		});
//
//		StringBuilder result = new StringBuilder();
//		//@formatter:off
//		String prefix = "\r\n"
//				+ "<!DOCTYPE html>\r\n"
//				+ "<html lang=\"en\">\r\n"
//				+ "<head>\r\n"
//				+ "    <meta charset=\"UTF-8\">\r\n"
//				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
//				+ "    <title>Cars!</title>\r\n"
//				+ "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\r\n"
//				+ "    <style> body {background-color: #333;color: #fff;}</style>\r\n"
//				+ "</head>\r\n"
//				+ "<body>\r\n"
//				+ "<div class=\"container mt-5\">";
//		//@formatter:on
//		result.append(prefix);
//		AbstractCarListing last = null;
//		for (int i = 0; i < lsts.size(); ++i) {
//			if (last == null || (last.getSearcher().isPresent()
//					&& !last.getSearcher().get().equals(lsts.get(i).getSearcher().orElse(null)))) {
//				result.append("<hr><center><h1>"
//						+ lsts.get(i).getSearcher().map(a -> a == null ? "N/A" : a.getPrettySearcherName()).get()
//						+ "</h1></center><hr>");
//
//			}
//			result.append(formatSingleListing(last = lsts.get(i)));
//		}
////		lsts.forEach(a -> result.append(formatSingleListing(a)));
//		String postfix = "</div></body></html>";
//		result.append(postfix);
//		return result.toString();

	}

//	private static String formatSingleListing(AbstractCarListing l) {
//		//@formatter:off
//		String imgURL = l.getPictureURLs().isEmpty()?"https://t4.ftcdn.net/jpg/04/00/24/31/360_F_400243185_BOxON3h9avMUX10RsDkt3pJ8iQx72kS3.jpg":l.getPictureURLs().iterator().next();
//		return "<div class=\"row mb-3\">"
//				+ "			<div class=\"col-3\">\r\n"
//				+ "            <img src=\""+imgURL+"\" class=\"img-fluid\">\r\n"
//				+ "        </div>\r\n"
//				+ "        <div class=\"col-6\">\r\n"
//				+ "            <h3><a href=\""+l.getListingURL()+"\">View Listing</a></h3>\r\n"
//				+ "            <p>"+l.toNotificationText().replaceAll("\r?\n", "<br>")+"</p>\r\n"
//				+ "        </div></div>";
//		//@formatter:on
//
//	}
	private static class DetailSerialiser implements JsonSerializer<ListingDetail<?>> {

		private static final Gson GSON = new Gson();

		@Override
		public JsonElement serialize(ListingDetail<?> src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = GSON.toJsonTree(src).getAsJsonObject();
			obj.addProperty("formattedValue", src.getFormattedValue());
			return obj;
		}
	}
}
