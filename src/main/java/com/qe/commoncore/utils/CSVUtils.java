package com.qe.commoncore.utils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import com.opencsv.CSVReader;

public class CSVUtils {

	/**
	 * @param filePath
	 * @return List<Map<String, String>>
	 * @throws Exception
	 * Read data from a .CSV file and store the values in a list of maps
	 */
	public static List<Map<String, String>> readDataFromCSVFile(String fileName) throws Exception {
		List<Map<String, String>> objectsList = new ArrayList();

		try (CSVReader csvReader = new CSVReader(new FileReader(CSVReader.class.getClassLoader().getResource(fileName).getFile()))) {
			String[] headings = csvReader.readNext();
			
			if (headings.length == 0) {
				throw new Exception("Missing Headings in the .CSV file");
			}
			
			List<String[]> readAllRows = csvReader.readAll();
			
			if (readAllRows.size() == 0) {
				throw new Exception("Please add column details to the .CSV file");
			}

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
	public static List<Map<String, String>> readDataFromCSVFile(String fileName,String columnNmae,List<String> identityList) throws Exception {
		List<Map<String, String>> objectsList = new ArrayList();

		try (CSVReader csvReader = new CSVReader(new FileReader(CSVReader.class.getClassLoader().getResource(fileName).getFile()))) {
			String[] headings = csvReader.readNext();
			for(int i=0;i<headings.length;i++) {
				headings[i]=headings[i].toLowerCase();
			}
			
			if (headings.length == 0) {
				throw new Exception("Missing Headings in the .CSV file");
			}
			
			int identityIndex=Arrays.asList(headings).indexOf(columnNmae.toLowerCase());
			
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
	public static List<String> readDataColumnFromCSVFile(String fileName,String columnName) throws Exception {
		List<String> objectsList = new ArrayList();

		try (CSVReader csvReader = new CSVReader(new FileReader(CSVReader.class.getClassLoader().getResource(fileName).getFile()))) {
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
	public static String[][] readDataFromCSV_File(String fileName) throws Exception {
		String[][] objectArray = null;
		
		try (CSVReader csvReader = new CSVReader(new FileReader(CSVReader.class.getClassLoader().getResource(fileName).getFile()))) {
			List<String[]> readAllRows = csvReader.readAll();

			if (readAllRows.size() == 0) {
				throw new Exception("Missing data in the .CSV file");
			}

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
	public static String[][] readDataFromCSV_File(String fileName,String columnNmae,List<String> identityList) throws Exception {
		String[][] objectArray = null;
		
		try (CSVReader csvReader = new CSVReader(new FileReader(CSVReader.class.getClassLoader().getResource(fileName).getFile()))) {
			String[] headings = csvReader.readNext();
			for(int i=0;i<headings.length;i++) {
				headings[i]=headings[i].toLowerCase();
			}
			
			if (headings.length == 0) {
				throw new Exception("Missing Headings in the .CSV file");
			}
			
			int identityIndex=Arrays.asList(headings).indexOf(columnNmae.toLowerCase());
 
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
}
