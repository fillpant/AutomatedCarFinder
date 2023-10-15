package click.nullpointer.carsearch.model;

import java.util.function.Function;

public class ListingDetail<T> {

	private final String detailType;
	private final T value;
	private transient final Function<T, String> formatter;
	private final String emoji;

	public ListingDetail(String detailType, T value) {
		this(detailType, value, (String) null);
	}

	public ListingDetail(String detailType, T value, Function<T, String> prettyFormat) {
		this(detailType, value, prettyFormat, null);
	}

	public ListingDetail(String detailType, T value, String emoji) {
		this(detailType, value, Object::toString, emoji);
	}

	public ListingDetail(String detailType, T value, Function<T, String> prettyFormat, String emoji) {
		this.value = value;
		this.detailType = detailType;
		this.formatter = prettyFormat;
		this.emoji = emoji;
	}

	public T getValue() {
		return value;
	}

	public String getFormattedValue() {
		return formatter.apply(getValue());
	}

	public String getType() {
		return detailType;
	}

	public String getEmoji() {
		return emoji;
	}

	@Override
	public String toString() {
		return (getEmoji() == null ? "" : (getEmoji() + " ")) + getType() + ": " + getFormattedValue();
	}

	public static final class StandardListingDetails {

		public static ListingDetail<Double> originalPrice(double valGBP) {
			return new ListingDetail<Double>("Original Price", valGBP, f -> String.format("£%,.2f", f), "💰");
		}

		public static ListingDetail<Double> valuationPrice(double valGBP) {
			return new ListingDetail<Double>("Valuation Price", valGBP, f -> String.format("£%,.2f", f), "💰");
		}

		public static ListingDetail<Double> price(double valGBP) {
			return new ListingDetail<Double>("Price", valGBP, f -> String.format("£%,.2f", f), "💰");
		}

		public static ListingDetail<Double> priceDifference(double current, double old) {
			return new ListingDetail<Double>("Price", current, f -> String.format("£%,.2f was £%,.2f (%c£%,.2f)", f,
					old, (current - old) < 0 ? '-' : '+', Math.abs(current - old)), "💰");
		}

		public static ListingDetail<Double> tax(double valGBP) {
			return new ListingDetail<Double>("Tax", valGBP, f -> String.format("£%,.2f", f), "💸");
		}

		public static ListingDetail<Double> mileage(double miles) {
			return new ListingDetail<Double>("Miles", miles, f -> String.format("%,.2f (%,.2fkm)", f, f * 1.60934),
					"📏");
		}

		public static ListingDetail<Double> distance(double miles) {
			return new ListingDetail<Double>("Distance", miles, f -> String.format("%,.2f (%.2fkm)", f, f * 1.60934),
					"📍");
		}

		public static ListingDetail<String> transmission(String trans) {
			return new ListingDetail<String>("Transmission", trans, f -> f, "⚙️");
		}

		public static ListingDetail<String> mot(String mot) {
			return new ListingDetail<String>("MOT", mot, f -> f, "🛠️");
		}

		public static ListingDetail<String> vin(String vin) {
			return new ListingDetail<String>("VIN", vin, f -> f, "🆔");
		}

		public static ListingDetail<Double> mpgUrban(double vin) {
			return new ListingDetail<Double>("Urban MPG", vin, f -> String.format("%.2f", f), "🏙️");
		}

		public static ListingDetail<Double> mpgMotorway(double vin) {
			return new ListingDetail<Double>("Motorway MPG", vin, f -> String.format("%.2f", f), "🛣️");
		}

		public static ListingDetail<Double> mpgCombined(double vin) {
			return new ListingDetail<Double>("Combined MPG", vin, f -> String.format("%.2f", f), "🌃");
		}

		public static ListingDetail<String> make(String m) {
			return new ListingDetail<String>("Make", m, f -> f, "🚘");
		}

		public static ListingDetail<String> model(String m) {
			return new ListingDetail<String>("Model", m, f -> f, "🚡");
		}

		public static ListingDetail<String> colour(String colour) {
			return new ListingDetail<String>("Colour", colour, f -> f, "🎨");
		}

		public static ListingDetail<String> title(String titl) {
			return new ListingDetail<String>("Title", titl, f -> f);
		}

		public static ListingDetail<String> subtitle(String subtitl) {
			return new ListingDetail<String>("Subtitle", subtitl, f -> f);
		}

		public static ListingDetail<String> description(String desc) {
			return new ListingDetail<String>("Description", desc, f -> f, "📰");
		}

		public static ListingDetail<String> shortDescription(String desc) {
			return new ListingDetail<String>("Short Description", desc, f -> f, "📰");
		}

		public static ListingDetail<Integer> year(int yr) {
			return new ListingDetail<Integer>("Year", yr, f -> f.toString(), "📖");
		}

		public static ListingDetail<Integer> photoCount(int cnt) {
			return new ListingDetail<Integer>("Photos count", cnt, f -> f.toString(), "📸");
		}

		public static ListingDetail<Integer> prevKeepers(int cnt) {
			return new ListingDetail<Integer>("Keepers", cnt, f -> f.toString(), "🧍");
		}

		public static ListingDetail<String> fuelType(String fuel) {
			return new ListingDetail<String>("Fuel", fuel, f -> f, "⛽");
		}

		public static ListingDetail<String> location(String loc) {
			return new ListingDetail<String>("Location", loc, f -> f, "🗺️");
		}

	}

}
