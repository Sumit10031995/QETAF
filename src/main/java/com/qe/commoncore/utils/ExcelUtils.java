package com.qe.commoncore.utils;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author sumit
 *
 */
public class ExcelUtils {
	private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);
	private static final String sheetName = "SheetName";
	private static ExcelUtils instance;
	public static List<Map<String, String>> failedTCList;
	private static Set methodSet = new HashSet();
	private static final List<String> headings = Arrays.asList("H-1", "H-2", "H-3",
			"H-4", "H-5");
	private static int rowCount = 1;

	private ExcelUtils() {

	}

	public static synchronized ExcelUtils getInstance() {
		if (instance == null) {
			instance = new ExcelUtils();
			failedTCList = new ArrayList();
		}
		return instance;
	}

	/**
	 * 
	 * @param xlsPath
	 * @return
	 * @throws Exception
	 * @use Create workbook instance
	 */
	private static Workbook getWorkBookInstance() throws Exception {
		try {
			 return new HSSFWorkbook();
		} catch (Exception e) {
			throw new Exception("Error creating Workbook instance", e);
		}
	}

	/**
	 * 
	 * @param workBook
	 * @return
	 * @throws Exception
	 * @use Delete and create new sheet 
	 */
	private static Sheet createSheet(Workbook workBook) throws Exception {
	    try {
	        Sheet sheet = workBook.getSheet(sheetName);
	        if (sheet != null) {
	            workBook.removeSheetAt(workBook.getSheetIndex(sheet));
	        }
	        sheet = workBook.createSheet(sheetName);
	        if (workBook.getSheetIndex(sheetName) != -1) {
	            System.out.println("Sheet with name \"" + sheetName + "\" has been created");
	        } else {
	            log.error("Failed to create a sheet with name \"" + sheetName + "\"");
	        }
	        return sheet;
	    } catch (Exception e) {
	        log.error("Error creating sheet", e);
	        throw new Exception("Error creating sheet", e);
	    }
	}

	/**
	 * 
	 * @param sheet
	 * @return
	 * @throws Exception
	 * @use Add headings to xls file
	 */
	private static boolean addHeadings(Sheet sheet) throws Exception {
		Row headerRow = sheet.createRow(0);
		int count = 0;
		try {
			for (int i = 0; i < headings.size(); i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headings.get(i));
				count++;
			}
			return (count == headings.size()) ? true : false;
		} catch (Exception e) {
			throw new Exception("Failed To Add Headings To XL Sheet");
		}
	}

	/**
	 * 
	 * @param data
	 * @param xmlPath
	 * @throws Exception
	 * @use This method write details to a xls file
	 */
	public synchronized void writeToXL(List<Map<String, String>> data, String xmlPath) throws Exception {
		FileOutputStream os = null;
		int row = 1;
		Workbook workbook = getWorkBookInstance();
		if (FileUtil.createFile(xmlPath)) {
		try {
			Sheet sheet = createSheet(workbook);
			if (addHeadings(sheet) && data.size() > 0) {
				for (Map<String, String> obj : data) {
					Row newRow = sheet.createRow(row);
					Cell cellOne = newRow.createCell(headings.indexOf("H-1"));
					cellOne.setCellValue(obj.get("rowNo"));
					Cell cellTwo = newRow.createCell(headings.indexOf("H-2"));
					cellTwo.setCellValue(obj.get("package"));
					Cell cellThree = newRow.createCell(headings.indexOf("H-3"));
					cellThree.setCellValue(obj.get("class"));
					Cell cellFour = newRow.createCell(headings.indexOf("H-4"));
					cellFour.setCellValue(obj.get("method"));
					Cell cellFive = newRow.createCell(headings.indexOf("H-5"));
					cellFive.setCellValue(obj.get("status"));
					os = new FileOutputStream(xmlPath);
					workbook.write(os);
					row++;
				}
				System.out.println(".xls file has been updated successfully.Failure count = " + data.size());
			} else {
				os = new FileOutputStream(xmlPath);
				workbook.write(os);
				System.out.println("Failure count = " + data.size());
			}
			workbook.close();
			os.close();
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("File Not Found");
		}
		}
	}
}
