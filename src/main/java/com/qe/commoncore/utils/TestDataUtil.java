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
import com.qe.api.commoncore.BaseTest;

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


	/**
	 * @param filePath
	 * @return List<Map<String, String>>
	 * @throws Exception
	 * Read data from a .CSV file and store the values in a list of maps
	 */
	public static List<Map<String, String>> readDataFromCSVFile(String filePath) throws Exception {
		List<Map<String, String>> objectsList = new ArrayList();

		try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
			String[] headings = csvReader.readNext();
			
			if (headings.length == 0) {
				throw new Exception("Missing Headings in the .CSV file");
			}
			
			List<String[]> readAllRows = csvReader.readAll();
			
			if (readAllRows.size() == 0) {
				throw new Exception("Please add column details to the .CSV file");
			}
			testDataHeader.set(Arrays.asList(readAllRows.get(0)));

			for (String[] row : readAllRows) {
				Map<String, String> mapObject = new LinkedHashMap();
				for (int i = 0; i < row.length; i++) {
					mapObject.put(headings[i], row[i]);
				}
				objectsList.add(mapObject);
			}
		}
		return objectsList;
	}
	
	/**
	 * @param filePath
	 * @param columnNmae
	 * @param identityList
	 * @return
	 * @throws Exception
	 * Read data from a .CSV file ,Filter the given inputs and store the values in a list of maps
	 */
	public static List<Map<String, String>> readDataFromCSVFile(String filePath,String columnNmae,List<String> identityList) throws Exception {
		List<Map<String, String>> objectsList = new ArrayList();

		try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
			String[] headings = csvReader.readNext();
			for(int i=0;i<headings.length;i++) {
				headings[i]=headings[i].toLowerCase();
				testDataHeader.set(Arrays.asList(headings));
			}
			
			if (headings.length == 0) {
				throw new Exception("Missing Headings in the .CSV file");
			}
			
			int identityIndex=Arrays.asList(headings).indexOf(columnNmae);
			
			List<String[]> readAllRows = csvReader.readAll();
			List<String[]> filteredRows = new ArrayList();
			
			for(int i=0;i<readAllRows.size();i++) {
					if(identityList.contains(readAllRows.get(i)[identityIndex])) {
						filteredRows.add(readAllRows.get(i));
						if(identityList.size()==filteredRows.size()) {
						break;
					}
				}
			}

			if (filteredRows.size() == 0) {
				throw new Exception("Unable to find provided details in the .CSV file");
			}

			for (String[] row : filteredRows) {
				Map<String, String> mapObject = new LinkedHashMap();
				for (int i = 0; i < row.length; i++) {
					mapObject.put(headings[i], row[i]);
				}
				objectsList.add(mapObject);
			}
		}
		return objectsList;
	}
	
	/**
	 * 
	 * @param filePath
	 * @param columnName
	 * @return
	 * @throws Exception
	 * This method use to fetch a column details from CSV file
	 */
	public static List<String> readDataColumnFromCSVFile(String filePath,String columnName) throws Exception {
		List<String> objectsList = new ArrayList();

		try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
			String[] headings = csvReader.readNext();
			for(int i=0;i<headings.length;i++) {
				headings[i]=headings[i].toLowerCase();
			}
			
			if (headings.length == 0) {
				throw new Exception("Missing Headings in the .CSV file");
			}
			
			List<String[]> readAllRows = csvReader.readAll();
			int identifier=Arrays.asList(headings).indexOf(columnName.toLowerCase());
			
			if (readAllRows.size() == 0) {
				throw new Exception("Please add column details to the .CSV file");
			}

			for (String[] row : readAllRows) {
				objectsList.add(row[identifier]);
			}
		}
		return objectsList;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 * Read data from a .CSV file and store the two dimensional array
	 */
	public static String[][] readDataFromCSV_File(String filePath) throws Exception {
		String[][] objectArray = null;
		
		try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
			List<String[]> readAllRows = csvReader.readAll();

			if (readAllRows.size() == 0) {
				throw new Exception("Missing data in the .CSV file");
			}
			testDataHeader.set(Arrays.asList(readAllRows.get(0)));

			objectArray = new String[readAllRows.size()][readAllRows.get(0).length];

			for (int j = 0; j < readAllRows.size(); j++) {
				for (int i = 0; i < readAllRows.get(j).length; i++) {
					objectArray[j][i] = readAllRows.get(j)[i];
				}

			}
		}
		return objectArray;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 * Read data from a .CSV file ,Filter the given inputs and store it to a two dimensional object
	 */
	public static String[][] readDataFromCSV_File(String filePath,String columnNmae,List<String> identityList) throws Exception {
		String[][] objectArray = null;
		
		try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
			String[] headings = csvReader.readNext();
			for(int i=0;i<headings.length;i++) {
				headings[i]=headings[i].toLowerCase();
				testDataHeader.set(Arrays.asList(headings));
			}
			
			if (headings.length == 0) {
				throw new Exception("Missing Headings in the .CSV file");
			}
			
			int identityIndex=Arrays.asList(headings).indexOf(columnNmae);
 
			List<String[]> readAllRows = csvReader.readAll();
            List<String[]> filteredRows = new ArrayList();
			
			for(int i=0;i<readAllRows.size();i++) {
				if(identityList.contains(readAllRows.get(i)[identityIndex])) {
					filteredRows.add(readAllRows.get(i));
					if(identityList.size()==filteredRows.size()) {
					break;
				}
			}
		 }
			
			if (readAllRows.size() == 0) {
				throw new Exception("Missing data in the .CSV file");
			}

			objectArray = new String[filteredRows.size()+1][filteredRows.get(0).length];
			for(int i=0;i<headings.length;i++) {
				objectArray[0][i]=headings[i];
			}

			for (int j = 0; j <filteredRows.size(); j++) {
				for (int i = 0; i < filteredRows.get(0).length; i++) {
					objectArray[j+1][i] = filteredRows.get(j)[i];
				}
			}
		}
		return objectArray;
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
