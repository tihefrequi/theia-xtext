package {service.namespace}.utils.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.math.NumberUtils;
{customcode.import}

/**
 * Data Utils for DataType Detection or CSV Access
 * 
 */
public class DataUtils {

	{customcode.start}
	
	public enum DataType {
		Integer, Long, Float, Double, String;
	}

	public static class Metadata {
		DataType type;
		String htmlcomment;

		public DataType getType() {
			return type;
		}

		public String getHtmlComment() {
			return htmlcomment;
		}

	}

	public static InputStreamReader getReader(InputStream inputStream) {
		return new InputStreamReader(inputStream);
	}

	public static InputStreamReader getReader(InputStream inputStream, Charset charset) {
		return new InputStreamReader(inputStream, charset);
	}

	public static InputStreamReader getReader(Path fullpath) throws FileNotFoundException {
		return new InputStreamReader(getInputStream(fullpath));
	}

	/**
	 * Detects and automatically skips BOM UTF-8 byte order mark
	 *
	 * @param fullpath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static InputStream getInputStream(Path fullpath) throws FileNotFoundException {
		File file = fullpath.toFile();
		FileInputStream inputStream = new FileInputStream(file);
		BOMInputStream bomSkippingInputStream = new BOMInputStream(inputStream);
		return bomSkippingInputStream;
	}

	/**
	 * Try to Detect dataType from a List of String Values
	 * 
	 * @param values
	 * @return found datatype or string
	 */
	public static DataType detectType(Iterable<String> values) {
		for (DataType type : DataType.values()) {
			if (allValuesOfType(type, values)) {
				return type;
			}
		}
		return DataType.String;
	}

	/**
	 * Try to Detect dataType from a String
	 * 
	 * @param expectedType is needed as "0" is otherwise not clear int or long e.g.
	 * @param any
	 * @return
	 */
	public static DataType detectType(DataType expectedType, String any) {
		DataType type = DataType.String;
		boolean isNumber = NumberUtils.isCreatable(any);
		if (isNumber && NumberUtils.toInt(any) != 0 || ("0".equals(any) && expectedType.equals(DataType.Integer))) {
			type = DataType.Integer;
		} else if (isNumber && NumberUtils.toLong(any) != 0L || ("0".equals(any) && expectedType.equals(DataType.Long))) {
			type = DataType.Long;
		} else if (isNumber && NumberUtils.toFloat(any) != 0.0f || ("0.0".equals(any) && expectedType.equals(DataType.Float))) {
			type = DataType.Float;
		} else if (isNumber && NumberUtils.toDouble(any) != 0.0d || ("0.0".equals(any) && expectedType.equals(DataType.Double))) {
			type = DataType.Double;
		}
		return type;
	}

	/**
	 * Try to Detect dataType from a List of String Values, expecting the given type in the data
	 * 
	 * @param expectedType
	 * @param values
	 * @return
	 */
	private static boolean allValuesOfType(DataType expectedType, Iterable<String> values) {
		for (String value : values) {
			if (!expectedType.equals(detectType(expectedType, value))) {
				return false;
			}
		}
		return true;
	}

	{customcode.end}
}
