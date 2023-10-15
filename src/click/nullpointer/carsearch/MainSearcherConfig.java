package click.nullpointer.carsearch;

import java.io.File;
import java.time.ZoneId;
import java.util.TimeZone;

public class MainSearcherConfig {

	private String notificationURL = "https://maker.ifttt.com/trigger/notify/with/key/nZ7AUfIHSFPuvwatJfFdHLCNckGKKlzCPvYpOnAnoOU?value1=%s&value2=%s&value3=%s";
	private int sleepBetweenRequests = 1000;
	private int runEverySeconds = 67 * 3;
	private String timezone = "Europe/London";
	private int stopSearchingAtHour = 1;
	private int startSearchingAtHour = 7;
	private String simpleWebPageViewFile = "./cars.html";
	private boolean keepKnownListingsBetweenRestarts = true;
	private String knownListingsCacheFile = "./last_known_listings.json";
	private int notificationsPerMinuteLimit = 120;
	private int maximumSearchRepeatAttempts = 2;

	public int getSleepBetweenRequests() {
		return sleepBetweenRequests;
	}

	public int getRunEverySeconds() {
		return runEverySeconds;
	}

	public String getNotificationURL() {
		return notificationURL;
	}

	public int getStopSearchingAtHour() {
		return stopSearchingAtHour;
	}

	public int getStartSearchingAtHour() {
		return startSearchingAtHour;
	}

	public ZoneId getTimezone() {
		return TimeZone.getTimeZone(timezone).toZoneId();
	}

	public File getSimpleWebpageViewFile() {
		return new File(simpleWebPageViewFile);
	}

	public boolean isKeepKnownListingsBetweenRestarts() {
		return keepKnownListingsBetweenRestarts;
	}

	public File getKnownListingsCacheFile() {
		return new File(knownListingsCacheFile);
	}

	public int getNotificationsPerMinute() {
		return notificationsPerMinuteLimit;
	}

	public int getMaximumSearchRepeatAttempts() {
		return maximumSearchRepeatAttempts;
	}

}
