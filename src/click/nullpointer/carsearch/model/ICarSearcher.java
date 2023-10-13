package click.nullpointer.carsearch.model;

import java.io.IOException;
import java.util.Collection;

public interface ICarSearcher {

	public Collection<AbstractCarListing> searchForListings() throws IOException;

	public String getPrettySearcherName();

}
