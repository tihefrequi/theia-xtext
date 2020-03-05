package {service.namespace}.utils.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import {service.namespace}.utils.data.DataUtils.Metadata;
{customcode.import}

/**
 * CSV Helper Methods for e.g. MetaData incl. DataType Detection in Columns
 */
public class CSVUtils {

	{customcode.start}

	public static Map<String, Metadata> getCSVMetaData(CSVReader csvReader) throws IOException {
		Map<String, Metadata> dataTypeMap = new LinkedHashMap<>();
		String[] headerNames = null;
		int columnNo = 0;
		try {
			headerNames = csvReader.readNext();
			List<String[]> allRows = csvReader.readAll();
			csvReader.close();
			for (String headerName : headerNames) {
				List<String> columnValues = new ArrayList<>();
				for (String[] row : allRows) {
					columnValues.add(row[columnNo]);
				}
				Metadata metadata = new Metadata();
				metadata.type = DataUtils.detectType(columnValues);
				metadata.htmlcomment = "";
				columnValues.stream().distinct().limit(3).filter(v -> StringUtils.isNotBlank(v)).forEach(v -> metadata.htmlcomment += "<br>" + v);
				if (StringUtils.isNotBlank(metadata.htmlcomment)) {
					metadata.htmlcomment = "<br>Example Values" + metadata.htmlcomment;
				}
				metadata.htmlcomment = "CSV Column No. " + columnNo + " '" + headerName + "' " + metadata.htmlcomment;
				dataTypeMap.put(headerName, metadata);
				columnNo++;
			}
		} catch (Exception e) {
			throw new RuntimeException("CSV MetaData Read failed. Probably CSV Customizing like Separator is wrong. Current Column No=" + columnNo
					+ " Count of Header Columns=" + headerNames.length + " header columns=" + Arrays.asList(headerNames));
		}
		return dataTypeMap;
	}

	/**
	 * Get a CSV Reader for a given Path and Parser
	 * 
	 * @param path
	 * @param csvParser
	 * @return
	 * @throws FileNotFoundException
	 */
	public static CSVReader getCSVReader(Path path, CSVParser csvParser) throws FileNotFoundException {
		return getCSVReaderBuilder(path, csvParser).build();
	}

	/**
	 * Get a CSV Reader for a given inputStream and Parser
	 * 
	 * @param inputStream
	 * @param csvParser
	 * @return
	 */
	public static CSVReader getCSVReader(InputStream inputStream, CSVParser csvParser) {
		return getCSVReaderBuilder(inputStream, csvParser).build();
	}

	/**
	 * Get a CSV Reader for a given inputStream and Parser
	 * 
	 * @param inputStream
	 * @param csvParser
	 * @param charset
	 * @return
	 */
	public static CSVReader getCSVReader(InputStream inputStream, CSVParser csvParser, Charset charset) {
		return getCSVReaderBuilder(inputStream, csvParser, charset).build();
	}

	/**
	 * Get a CSV Reader Builder for a given Parser
	 * 
	 * @param fullpath
	 * @param csvParser
	 * @return
	 * @throws FileNotFoundException
	 */
	public static CSVReaderBuilder getCSVReaderBuilder(Path fullpath, CSVParser csvParser) throws FileNotFoundException {
		return new CSVReaderBuilder(DataUtils.getReader(fullpath)).withCSVParser(csvParser);
	}

	/**
	 * Get a CSV Reader Builder for a given InputStream
	 * 
	 * @param inputStream
	 * @param csvParser
	 * @return
	 */
	public static CSVReaderBuilder getCSVReaderBuilder(InputStream inputStream, CSVParser csvParser) {
		return new CSVReaderBuilder(DataUtils.getReader(inputStream)).withCSVParser(csvParser);
	}

	/**
	 * Get a CSV Reader Builder for a given InputStream
	 * 
	 * @param inputStream
	 * @param csvParser
	 * @param charset
	 * @return
	 */
	public static CSVReaderBuilder getCSVReaderBuilder(InputStream inputStream, CSVParser csvParser, Charset charset) {
		return new CSVReaderBuilder(DataUtils.getReader(inputStream, charset)).withCSVParser(csvParser);
	}
	
	{customcode.end}

}
