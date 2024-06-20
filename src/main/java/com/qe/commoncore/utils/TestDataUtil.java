package com.qe.commoncore.utils;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.MappingIterator;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvException;
import com.qe.commoncore.BaseTest;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TestDataUtil {
	
	// To store the column header list from the test data CSV file (used only when using data provider).
	public static ThreadLocal<List<String>> testDataHeader = new ThreadLocal<List<String>>();
	
	// To store the list of map where each map is like headerColumn=rowValue (used only when using data provider).
	public static ThreadLocal<Map<String, String>> testDataMapWithHeaders = new ThreadLocal<Map<String, String>>();
	
	// used to store the scenario. Scenario is fetched from scenario column from the 'testDataMapWithHeaders' map.
    	public static final String scenarioClmName="scenario";

    private TestDataUtil() {
    }

    public static List<List<String>> fetchListOfAllRows(List<String[]> allData)  {
        List<String> eachRowsList;
        String[] allRows;
        List<List<String>> allRowsList = new ArrayList<>();

        for (int i = 0; i < allData.size(); i++) {
            try {
                allRows = allData.get(i);
                eachRowsList = new ArrayList<>(Arrays.asList(allRows));
                allRowsList.add(eachRowsList);
            } catch (Exception exception) {
                throw exception;
            }
        }
        return allRowsList;
    }

    public static List<String> fetchListOfCellValues(List<String[]> allData)  {
        List<String> eachRowsList;
        String[] allRows;
        List<List<String>> allRowsList = new ArrayList<>();
        List<String> allValuesList = new ArrayList<>();

        for (int i = 0; i < allData.size(); i++) {
            try {
                allRows = allData.get(i);
                eachRowsList = new ArrayList<>(Arrays.asList(allRows));
                allRowsList.add(eachRowsList);
            } catch (Exception exception) {
                throw exception;
            }
        }
        for (List<String> eachRow : allRowsList) {
            allValuesList.addAll(eachRow);
        }

        return allValuesList;
    }

    /**
     * 
     * @param testDataFilePath
     * @return
     * @throws Exception 
     * @throws TestDataExceptions
     * @throws CsvException
     */
	public static List<Map<String, String>> getCSVRowsAsListOfMaps(String testDataFilePath) throws Exception
  {

		FileReader filereader;
		CSVReader csvReader;
		String[] headerRow;
		List<String> headerValues;
		List<Map<String, String>> finalData = new ArrayList<>();
		List<String[]> allData = null;

		try {
			filereader = new FileReader(testDataFilePath.trim());
			csvReader = new CSVReaderBuilder(filereader).build();
			headerRow = csvReader.readNext();
			headerValues = new ArrayList<>(Arrays.asList(headerRow));

			// fetching all values from csv table
			allData = csvReader.readAll();
		
		List<List<String>> processedData = fetchListOfAllRows(allData);

		for (List<String> eachProcessedData : processedData) {
			Map<String, String> rawFinalData = new HashMap<>();
			for (int j = 0; j < headerValues.size(); j++) {
				rawFinalData.put(headerValues.get(j).toLowerCase(), eachProcessedData.get(j).trim());
			}
			finalData.add(rawFinalData);
		}
		} catch (Exception exception) {
			throw exception;
		}

		return finalData;
	}

    /**
     * 
     * 
     * @param testDataFilePath
     * @param keyName
     * @param givenSearchKeys
     * @return
     * @throws Exception 
     */
	public static List<Map<String, String>> getCSVRowsAsListOfMaps(String testDataFilePath, String keyName,
			List<String> givenSearchKeys) throws Exception {

		FileReader filereader;
		CSVReader csvReader;
		String[] headerRow;
		keyName = keyName.toLowerCase();
		List<String> headerValues;
		List<Map<String, String>> finalData = new ArrayList<>();

		try {
			filereader = new FileReader(testDataFilePath.trim());
			csvReader = new CSVReaderBuilder(filereader).build();
			headerRow = csvReader.readNext();
			headerValues = new ArrayList<>(Arrays.asList(headerRow));

			int columnNumberOfKey = -1;

			headerValues = new ArrayList<>(Arrays.asList(headerRow));
			System.out.println("headerValues = " + headerValues);
			for (int i = 0; i < headerValues.size(); i++) {
				if (headerValues.get(i) != null && StringUtils.containsIgnoreCase(headerValues.get(i), keyName)) {
					columnNumberOfKey = i;
				}
			}

			System.out.println("columnNumberOfKey = " + columnNumberOfKey);

			// fetching all values from csv table
			List<String[]> allData = null;

			allData = csvReader.readAll();

			List<List<String>> processedData = fetchListOfAllRows(allData);

			for (List<String> eachProcessedData : processedData) {
				Map<String, String> rawFinalData = new HashMap<>();
				for (int j = 0; j < headerValues.size(); j++) {
					rawFinalData.put(headerValues.get(j).toLowerCase(), eachProcessedData.get(j).trim());
				}
				if (columnNumberOfKey != -1) {
					for (String eachKey : givenSearchKeys) {
						if (eachProcessedData.get(columnNumberOfKey).trim().equalsIgnoreCase(eachKey.trim())) {
							finalData.add(rawFinalData);
						}
					}
				} 
			}

			for (String eachKey : givenSearchKeys) {
				int count = 0;
				for (Map<String, String> eachCellOfFinalData : finalData) {
					if (StringUtils.containsIgnoreCase(eachCellOfFinalData.get(keyName), eachKey.trim())) {
						count++;
					}
				}
				if (count == 0) {
					System.out.println(
							"Supplied search key " + eachKey.trim() + " was not found in the given CSV sheet ");
				}
			}
		} catch (Exception exception) {
			throw exception;
		}
		return finalData;
	}

	public static Object[][] getCSVRowsAsTwoDimentionalObject(String testDataFilePath) throws Exception {

		FileReader filereader;
		CSVReader csvReader;
		String[] headerRow;
		List<String> headerValues;
		List<String> listOfAllValues = new ArrayList<>();
		Object[][] allValues = null;

		try {
			filereader = new FileReader(testDataFilePath.trim());
			csvReader = new CSVReaderBuilder(filereader).build();

			// fetching only header values

			headerRow = csvReader.readNext();
			// Loop through each header value and convert it to lower case
			for (int i = 0; i < headerRow.length; i++) {
				headerRow[i] = headerRow[i].toLowerCase();
			}

			headerValues = new ArrayList<>(Arrays.asList(headerRow));
			testDataHeader.set(headerValues);

			// fetching all values from csv table
			List<String[]> allData = csvReader.readAll();

			// converting List<String[]> to List<List<String>>
			List<List<String>> processedData = fetchListOfAllRows(allData);

			// picking rows for supplied search keys only
			for (List<String> eachProcessedData : processedData) {
				listOfAllValues.addAll(eachProcessedData);
			}

			// Adding listOfAllValues into Object[][]

			int objectRowSize = (listOfAllValues.size() / headerValues.size());
			allValues = new String[objectRowSize][headerValues.size()];
			int countOfListOfAllValues = 0;

			for (int j = 0; j < objectRowSize; j++) {
				for (int k = 0; k < headerValues.size(); k++) {
					if (countOfListOfAllValues < listOfAllValues.size()) {
						allValues[j][k] = listOfAllValues.get(countOfListOfAllValues);
					}
					countOfListOfAllValues++;
				}
			}
		} catch (Exception exception) {
			throw exception;
		}

		return allValues;
	}

	public static Object[][] getCSVRowsAsTwoDimentionalObject(String testDataFilePath, String keyName,
			List<String> givenSearchKeys) throws Exception {

		FileReader filereader;
		CSVReader csvReader;
		String[] headerRow;
		keyName = keyName.toLowerCase();
		List<String> headerValues;
		List<String> listOfAllValues = new ArrayList<>();
		Object[][] allValues = null;

		try {
			filereader = new FileReader(testDataFilePath.trim());
			csvReader = new CSVReaderBuilder(filereader).build();

			// fetching only header values

			headerRow = csvReader.readNext();
			// Loop through each header value and convert it to lower case
			for (int i = 0; i < headerRow.length; i++) {
				headerRow[i] = headerRow[i].toLowerCase();
			}
			headerValues = new ArrayList<>(Arrays.asList(headerRow));
			testDataHeader.set(headerValues);

			int columnNumberOfKey = -1;

			headerValues = new ArrayList<>(Arrays.asList(headerRow));
			System.out.println("headerValues = " + headerValues);
			for (int i = 0; i < headerValues.size(); i++) {
				if (headerValues.get(i) != null && StringUtils.containsIgnoreCase(headerValues.get(i), keyName)) {
					columnNumberOfKey = i;
				}
			}

			System.out.println("columnNumberOfKey = " + columnNumberOfKey);

			// fetching all values from csv table
			List<String[]> allData = null;

			allData = csvReader.readAll();

			// converting List<String[]> to List<List<String>>
			List<List<String>> processedData = fetchListOfAllRows(allData);

			// picking rows for supplied search keys only
			for (List<String> eachProcessedData : processedData) {
				if (columnNumberOfKey != -1) {
					for (String eachKey : givenSearchKeys) {
						if (eachProcessedData.get(columnNumberOfKey).trim().equalsIgnoreCase(eachKey.trim())) {
							listOfAllValues.addAll(eachProcessedData);
						}
					}
				} else {
					throw new Exception(
							"Could not find KEY column in the header row in the given csv file hence could not fetch rows specific to given keys. Specify KEY column in the csv sheet and re-run ");
				}
			}

			for (String eachKey : givenSearchKeys) {
				int count = 0;
				for (List<String> eachProcessedData : processedData) {
					if (eachProcessedData.get(columnNumberOfKey).trim().equalsIgnoreCase(eachKey.trim())) {
						count++;
					}
				}
				if (count == 0) {
					System.err.println(
							"Supplied search key " + eachKey.trim() + " was not found in the given CSV sheet ");

				}
			}

			// Adding listOfAllValues into Object[][]
			int objectRowSize = (listOfAllValues.size() / headerValues.size());
			allValues = new String[objectRowSize][headerValues.size()];
			int countOfListOfAllValues = 0;

			for (int j = 0; j < objectRowSize; j++) {
				for (int k = 0; k < headerValues.size(); k++) {
					if (countOfListOfAllValues < listOfAllValues.size()) {
						allValues[j][k] = listOfAllValues.get(countOfListOfAllValues);
					}
					countOfListOfAllValues++;
				}
			}
		} catch (Exception exception) {
			throw exception;
		}

		return allValues;
	}

    /**
     * This method reads CSV file and return as list of java beans.
     *
     * @param mapping  <Map<String, String>> map with key as CSV headers and value as corresponding java field name.
     * @param beanType <T>     of any Type
     * @param filePath <String>    CSV file path
     * @return <List<T>>    list of objects
     * @throws FileNotFoundException 
     */
    public static <T> List readCSV(Map<String, String> mapping, Class<T> beanType, String filePath) throws FileNotFoundException  {

        // HeaderColumnNameTranslateMappingStrategy for beanType
        HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
        strategy.setType(beanType);
        strategy.setColumnMapping(mapping);

        CSVReader csvReader = null;
        try {
//            csvReader = new CSVReader(new FileReader(System.getProperty("user.dir") + filePath));
            csvReader = new CSVReader(new FileReader(FileUtil.getFile(filePath)));
        } catch (FileNotFoundException fileNotFoundException) {
            throw fileNotFoundException;
        }

        CsvToBean csvToBean = new CsvToBean();
        csvToBean.setCsvReader(csvReader);
        csvToBean.setMappingStrategy(strategy);
        return csvToBean.parse();
    }

    //Read csv per test data id starts.

/*    /**
     * This method reads CSV file and return as list of map.
     *
     * @param csvFilePath <String>    CSV file path
     * @return List<Map < ?, ?>>    list of Maps. Map will contain csv row data.
     *//*
    public static List<Map<?, ?>> readCSV(String csvFilePath) throws TestDataExceptions {

//        File input = new File(System.getProperty("user.dir") + csvFilePath);
        File input = FileUtil.getFile(csvFilePath);

        try {
            CsvSchema csv = CsvSchema.emptySchema().withHeader();
            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader().forType(Map.class).with(csv).readValues(input);
            List<Map<?, ?>> list = mappingIterator.readAll();
            return list;
        } catch (Exception e) {
            throw new TestDataExceptions("Exception occurs : ", e);
        }
    }*/

    /**
     * This method reads CSV file and return as list of map by TestDataId.
     *
     * @param csvFilePath <String>    CSV file path
     * @param testDataId  <String>    test Data Id
     * @return List<Map < ?, ?>>    list of Maps. Map will contain csv row data.
     * @throws Exception 
     */
    public static List<Map<?, ?>> readCSVByTestDataId(String csvFilePath, String testDataId) throws Exception  {
        try {
        return removeTestDataIdField(readCSV(csvFilePath, ',').stream().filter(map -> map.get("testDataId").toString().equalsIgnoreCase(testDataId)).collect(Collectors.toList()));
        }catch(Exception e) {
			BaseTest.reporter.logTestStepDetails(Status.FAIL, JavaUtils.getStackTrace(e));
			throw new Exception("Exception occurs : ", e);
        }
    }

    /**
     * This method removes TestDataId from list of map.
     *
     * @param list List<Map<?, ?>>    list of map
     * @return List<Map < ?, ?>>    list of Maps without TestDataId field. Map will contain csv row data.
     */
    private static List<Map<?, ?>> removeTestDataIdField(List<Map<?, ?>> list) {

        for (Map<?, ?> map : list) {
            removeTestDataIdFieldFromMap(map);
        }
        return list;
    }

    /**
     * This method removes TestDataId from map.
     *
     * @param map Map<?, ?>   map
     * @return Map<?, ?>    Map without TestDataId field. Map will contain csv row data.
     */
    private static Map<?, ?> removeTestDataIdFieldFromMap(Map<?, ?> map) {

        map.remove("testDataId");
        return map;
    }

    //Read csv per test data id ends.

    /**
     * This method generates random email-id.
     *
     * @return String alphanumeric mail Id
     */
    public static String generateRandomEmailId() {

        String emailAddress = "";
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        while (emailAddress.length() <= 10) {
            int character = (int) (Math.random() * 26);
            emailAddress += alphabet.substring(character, character + 1);
            emailAddress += Integer.valueOf((int) (Math.random() * 99)).toString();
        }
        return "SysGen_" + emailAddress + "@mailinator.com";
    }

    /**
     * Read CSV file with provided delimiter.
     *
     * @param csvFilePath
     * @return
     * @throws Exception 
     * @throws TestDataExceptions
     */
    private static List<Map<?, ?>> readCSV(String csvFilePath, char delimiter) throws Exception {

        File input = FileUtil.getFile(csvFilePath);

        try {
            CsvSchema csv = CsvSchema.emptySchema().withHeader().withColumnSeparator(delimiter);
            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader().forType(Map.class).with(csv).readValues(input);
            List<Map<?, ?>> list = mappingIterator.readAll();
            return list;
        } catch (Exception e) {
			BaseTest.reporter.logTestStepDetails(Status.FAIL, "EXCEPTION:::: " + e.getMessage());
            throw new Exception("Exception occurs : ", e);
        }
    }

    /**
     * Read pipe delimited CSV file with testDataId.
     *
     * @param csvFilePath
     * @param testDataId
     * @return
     * @throws Exception 
     * @throws TestDataExceptions
     */
    public static List<Map<?, ?>> readPipeDelimitedCSVByTestDataId(String csvFilePath, String testDataId) throws Exception  {

        return removeTestDataIdField(readCSV(csvFilePath, '|').stream().filter(map -> map.get("testDataId").toString().trim().equalsIgnoreCase(testDataId)).collect(Collectors.toList()));
    }
    
	/**
	 * @param array
	 * This method is use to create a mapping between test data headers and values when data provider is used.
	 * @throws Exception 
	 */
	public static void setTestDataMapWithHeaders(Object[] testDataRow) throws Exception {
		
		//If data provider is not being used 
    	if(testDataRow.length==0) {
			return ;
		}
		
		//In-case data provider is used but not constructed using CSV file then testDataHeader list will be null.
		if(testDataHeader.get()==null) {
			return;
		}
		
		Map<String, String> map = new HashMap<>();
		if (testDataRow.length == testDataHeader.get().size()) {
			for (int i = 0; i < testDataRow.length; i++) {
				map.put(testDataHeader.get().get(i), testDataRow[i].toString());
			}
		}else {
            System.out.println("The header element count and data provider element count fetched from the CSV file should match. Both should contain the same number of elements");
		}
		testDataMapWithHeaders.set(map);
	}
}
