package click.nullpointer.carsearch.model.images;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ListingImageTracker {
	private static final String TABLE_NAME = "images";
	private static final Gson SHARED_GSON = new GsonBuilder().serializeNulls().create();
	// SQL statements
	private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
			+ "image_hash TEXT PRIMARY KEY, " + "image_data TEXT)";
	private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (image_hash, image_data) VALUES (?, ?)";
	private static final String SELECT_IMAGE_DATA_SQL = "SELECT image_data FROM " + TABLE_NAME
			+ " WHERE image_hash = ?";
	private static final String COUNT_ROWS_SQL = "SELECT COUNT(*) FROM " + TABLE_NAME;
	private Connection connection;

	private WeakHashMap<String, List<ListingImageData>> cache = new WeakHashMap<>();

	public ListingImageTracker(File dbFile) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
			connection.createStatement().execute(CREATE_TABLE_SQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertData(String imageHash, List<ListingImageData> data) {
		try {
			cache.remove(imageHash);
			String jsonData = SHARED_GSON.toJson(data);
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL);
			preparedStatement.setString(1, imageHash.toLowerCase());
			preparedStatement.setString(2, jsonData);
			preparedStatement.executeUpdate();
			cache.put(imageHash, new LinkedList<>(data));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void registerImage(String hash, ListingImageData data) {
		hash = hash.toLowerCase();
		List<ListingImageData> img = getData(hash);
		if (img.isEmpty())
			img = new LinkedList<>(Collections.singleton(data));
		insertData(hash, img);
	}

	public List<ListingImageData> getData(String imageHash) {
		imageHash = imageHash.toLowerCase();
		if (cache.containsKey(imageHash))
			return Collections.unmodifiableList(cache.get(imageHash));
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IMAGE_DATA_SQL);
			preparedStatement.setString(1, imageHash);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				String json = resultSet.getString("image_data");
				List<ListingImageData> imgs = new LinkedList<>(
						Arrays.asList(SHARED_GSON.fromJson(json, ListingImageData[].class)));
				cache.put(imageHash, imgs);
				return Collections.unmodifiableList(imgs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public int getKnownImageCount() {
		try {
			ResultSet resultSet = connection.createStatement().executeQuery(COUNT_ROWS_SQL);
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
