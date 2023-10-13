package click.nullpointer.carsearch.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.brotli.dec.BrotliInputStream;

public class RequestUtils {
	public static final Map<String, String> STATIC_REQUEST_HEADERS = new HashMap<>();

	static {
		// STATIC_REQUEST_HEADERS.put("Origin", "https://motors.co.uk");
		STATIC_REQUEST_HEADERS.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
		STATIC_REQUEST_HEADERS.put("Accept", "*/*");
		STATIC_REQUEST_HEADERS.put("Accept-Language", "en");
		STATIC_REQUEST_HEADERS.put("Accept-Encoding", "gzip, deflate, br");
		STATIC_REQUEST_HEADERS.put("Connection", "keep-alive");
		STATIC_REQUEST_HEADERS.put("Cache-Control", "no-cache");
		STATIC_REQUEST_HEADERS.put("Dnt", "1");
		STATIC_REQUEST_HEADERS.put("Sec-Ch-Ua",
				"\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"");
		STATIC_REQUEST_HEADERS.put("Sec-Ch-Ua-Mobile", "?0");
		STATIC_REQUEST_HEADERS.put("Sec-Ch-Ua-Platform", "Windows");
		STATIC_REQUEST_HEADERS.put("Upgrade-Insecure-Requests", "1");
		STATIC_REQUEST_HEADERS.put("Sec-Fetch-Dest", "empty");
		STATIC_REQUEST_HEADERS.put("Sec-Fetch-Mode", "cors");
		STATIC_REQUEST_HEADERS.put("Sec-Fetch-Site", "same-origin");
		STATIC_REQUEST_HEADERS.put("Sec-Gpc", "1");
	}

	public static String postString(String url, String data, String contentType, Map<String, String> requestHeaders)
			throws IOException {
		byte[] dat = post(url, data.getBytes(StandardCharsets.UTF_8), contentType, requestHeaders);
		return new String(dat);
	}

	public static byte[] post(String url, byte[] data, String contentType, Map<String, String> requestHeaders)
			throws IOException {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
		requestHeaders.forEach((k, v) -> connection.setRequestProperty(k, v));
		connection.setRequestProperty("Content-Type", contentType);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		try (OutputStream os = connection.getOutputStream()) {
			os.write(data, 0, data.length);
		}
		InputStream is = connection.getInputStream();
		if ("gzip".equalsIgnoreCase(connection.getHeaderField("Content-Encoding")))
			is = new GZIPInputStream(is);
		else if ("br".equalsIgnoreCase(connection.getHeaderField("Content-Encoding")))
			is = new BrotliInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		return baos.toByteArray();
	}

	public static String get(String url, Map<String, String> requestHeaders) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
		requestHeaders.forEach((k, v) -> connection.setRequestProperty(k, v));
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.getHeaderFields().entrySet().stream().map(a -> a.getKey() + "->" + a.getValue())
				.collect(Collectors.joining("\n"));
		InputStream is = connection.getInputStream();
		if ("gzip".equalsIgnoreCase(connection.getHeaderField("Content-Encoding")))
			is = new GZIPInputStream(is);
		else if ("br".equalsIgnoreCase(connection.getHeaderField("Content-Encoding")))
			is = new BrotliInputStream(is);
		return IOUtils.toString(is, StandardCharsets.UTF_8);
	}
}
