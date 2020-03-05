package {service.namespace}.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 * Different util methods, which can be used in the own code
 */
public class Utils {

	private static DateTimeFormatter dateFormatShort = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
	private static DateTimeFormatter dateTimeFormatShort = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm",
			Locale.ENGLISH);

	/**
	 * Convert byte count to the human readable string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String formatFileSize(Long bytes) {
	    if(bytes == null) {
	     bytes = 0L;
	    }
		int unit = 1000;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = String.valueOf("kMGTPE".charAt(exp - 1));
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Convert Date to human readable String 2018-07-13
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateShort(Calendar date) {
		if (date == null)
			return null;
		return dateFormatShort.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	}

	/**
	 * Convert DateTime to human readable String 2018-07-13 09:46
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateTimeShort(Calendar date) {
		if (date == null)
			return null;
		return dateTimeFormatShort.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	}

  /**
   * Build RFC 1766 Language Tag e.g. de or de-de from locale
   * 
   * @see https://tools.ietf.org/html/rfc1766
   * 
   * @param locale
   * @return
   */
  public static String toRfc1766Tag(Locale locale) {
    if (locale != null) {
      return locale.getLanguage()
          + (StringUtils.isNoneEmpty(locale.getCountry()) ? "-" + locale.getCountry() : "");
    }
    return "";
  }
  
  /**
   * Build Locale from RFC 1766 Langugage Tag ( e.g. de or de-de)
   * 
   * @param tag
   * @return
   */
  public static Locale fromRfc1766Tag(String tag) {
    String[] tagParts = StringUtils.split(tag, "-");
    if (tagParts != null) {
      return tagParts.length > 1 ? new Locale(tagParts[0], tagParts[1]) : new Locale(tagParts[0]);
    }
    return null;
  }
}