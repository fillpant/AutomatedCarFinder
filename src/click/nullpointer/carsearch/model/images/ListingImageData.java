package click.nullpointer.carsearch.model.images;

public class ListingImageData {
	private long firstSeenOn;
	private String imageHash;
	private String imageURL;

	public ListingImageData(long firstSeenOn, String imageHash, String imageURL) {
		this.firstSeenOn = firstSeenOn;
		this.imageHash = imageHash;
		this.imageURL = imageURL;
	}

	public long getFirstSeenOn() {
		return firstSeenOn;
	}

	public String getImageHash() {
		return imageHash;
	}

	public String getImageURL() {
		return imageURL;
	}

}
