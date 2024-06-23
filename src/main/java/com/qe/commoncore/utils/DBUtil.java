package com.qe.commoncore.utils;

import com.aventstack.extentreports.Status;
import com.qe.api.commoncore.BaseTest;
import com.qe.api.commoncore.Configurator;
import com.qe.commoncore.constants.ContextConstant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
	private static Configurator configurator = Configurator.getInstance();
	private static final String SQL_DB_URL="sql.db.url";
	private static final String SQL_DB_USER_NAME="sql.db.user.name";
	private static final String SQL_DB_PASSWORD="sql.db-password";
	private static final String AZURE_DB_URL="azure.db.url";
	private static final String AZURE_DB_USER_NAME="azure.db.user.name";
	private static final String AZURE_DB_PASSWORD="azure.db-password";
	
    private final String driver;
    private final String url;
    private final String user;
    private final String password;
	

	// This DBUtils object is currently immutable. 
	// We parameterize it to allow changing the connection if required.
    public DBUtil(String DBType) {
    	
		switch (DBType) {
		case "SQL_DB":
			this.driver="com.db.jcc.DBriver";
			this.url= configurator.getEnvironmentParameter(SQL_DB_URL);
			this.user=configurator.getEnvironmentParameter(SQL_DB_USER_NAME);
			this.password=configurator.getEnvironmentParameter(SQL_DB_PASSWORD);
			break;
		case "AZURE_MSSQL":
			this.driver="com.jdbc.SQLServerDriver";
			this.url= configurator.getEnvironmentParameter(AZURE_DB_URL);
			this.user=configurator.getEnvironmentParameter(AZURE_DB_USER_NAME);
			this.password=configurator.getEnvironmentParameter(AZURE_DB_PASSWORD);
			break;
		default:
            throw new IllegalArgumentException("Invalid DBType: " + DBType);
			
		}
	}

	/*
	 * getDbConnection method creates a connection to DB. It also throws an
	 * exception when the connection is not successful.
	 */
	private Connection getDbConnection(String DB) throws Throwable {
		Connection connection = null;
		Class.forName(this.driver);
		try {
			connection = DriverManager.getConnection(this.url, this.user,this.password);
		} catch (Exception t) {
			throw t;
		}
		return connection;
	}

	/*
	 * 1. returnDBValue method is used for fetching the single value obtained by
	 * executing the query. 2. Fetches only one value from the first column of the
	 * result set. 3. The Return type of this method is String. 4. Returns NULL when
	 * there is no data retrieved.
	 */
	public Object returnDBValue(String DB, String query) throws Throwable {
		Statement stmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		Connection con = getDbConnection(DB);
		try {
			long startTime = System.currentTimeMillis();
			prepStmt = con.prepareStatement(query);
			rs = prepStmt.executeQuery();
			long endTime = System.currentTimeMillis();
			
			while (rs.next()) {
				rowCounter++;
				value = rs.getObject(1);
			}

			String queryDetails = getQueryDetails(query, (value == null) ? "" : value.toString(), startTime, endTime);
			BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>" + queryDetails + "</pre>");

			if (rowCounter == 0) {
				BaseTest.reporter.logTestStepDetails(Status.INFO, "ERROR:::: No data found for the query-> " + query);
				System.err.println("ERROR:::: No data found for the query-> " + query);
				return null;
			} else {
				return value;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}
	}

	/*
	 * 1. returnListOfDBValues method is used for fetching multiple values obtained
	 * by executing the query. 2. Fetches values only from the first column of the
	 * result set. 3. The Return type of this method is List<String>. 4. Returns
	 * NULL when there is no data retrieved.
	 */
	public List<Object> returnListOfDBValues(String DB, String query) throws Throwable {
		List<Object> DBvalues = new ArrayList<Object>();
		Statement stmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		Connection con = getDbConnection(DB);
		try {
			long startTime = System.currentTimeMillis();
			prepStmt = con.prepareStatement(query);
			rs = prepStmt.executeQuery();
			long endTime = System.currentTimeMillis();

			while (rs.next()) {
				rowCounter++;
				value = rs.getObject(1);
				DBvalues.add(value);
			}

			String queryDetails = getQueryDetails(query, DBvalues.toString(), startTime, endTime);
			BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>" + queryDetails + "</pre>");

			if (rowCounter == 0) {
				BaseTest.reporter.logTestStepDetails(Status.INFO, "ERROR:::: No data found for the query-> " + query);
				System.err.println("ERROR:::: No data found for the query-> " + query);
				return null;
			} else {
				return DBvalues;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}

	}

	/*
	 * 1. returnDBResultSet method is used for fetching full result set obtained by
	 * executing the query. 2. The Return type of this method is
	 * List<HashMap<String, String>>. 3. Returns NULL when there is no data
	 * retrieved. 4. In the List of Hash Maps, each hash map is generated from each
	 * row of the result set. The KEYs of the Hash Map are the column headers and
	 * the VALUEs of the Hash Map are the actual values from the result set rows.
	 */
	public synchronized List<HashMap<String, Object>> returnDBResultSet(String DB, String query) throws Throwable {
		List<HashMap<String, Object>> resultSetMap = new ArrayList<HashMap<String, Object>>();
		LinkedHashMap<String, Object> headerAndValues = new LinkedHashMap<String, Object>();

		Statement stmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		Connection con = getDbConnection(DB);
		try {
			long startTime = System.currentTimeMillis();
			prepStmt = con.prepareStatement(query);
			rs = prepStmt.executeQuery();
			long endTime = System.currentTimeMillis();

			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			while (rs.next()) {
				rowCounter++;
				for (int i = 1; i <= columnCount; i++) {
					String colName = rsmd.getColumnName(i);
					headerAndValues.put(colName, rs.getObject(i));
				}
				resultSetMap.add(headerAndValues);
			}

			String queryDetails = getQueryDetails(query, resultSetMap.toString(), startTime, endTime);
			BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>" + queryDetails + "</pre>");

			if (rowCounter == 0) {
				BaseTest.reporter.logTestStepDetails(Status.INFO, "ERROR:::: No data found for the query-> " + query);
				System.err.println("ERROR:::: No data found for the query-> " + query);
				return null;
			} else {
				return resultSetMap;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}
	}

	/*
	 * updateDatabase method is used to update the values in DB based on the query
	 * passed.
	 */
	public synchronized String updateDB(String DB, String query) throws Throwable {
		Statement stmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		Connection con = getDbConnection(DB);
		try {
			long startTime = System.currentTimeMillis();
			prepStmt = con.prepareStatement(query);
			rowCounter = prepStmt.executeUpdate();
			long endTime = System.currentTimeMillis();
			String queryDetails = getQueryDetails(query, String.valueOf(rowCounter), startTime, endTime);
			{
				BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>" + queryDetails + "</pre>");
				BaseTest.reporter.logTestStepDetails(Status.INFO, "Total No Of Rows Affected:" + rowCounter);
			}
			return "Total rows affected:" + rowCounter;
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * @param qryParams as map
	 * @param qryString as string
	 * @return list of database result
	 * @throws Throwable 
	 * @throws DatabaseException
	 * This method maps a query parameter MAP to its's original query string and return an List of hash map object based on the query.
	 */
	public List<HashMap<String, Object>> getDataFromDB(String db, Map<String, String> qryParams, String qryString) throws Throwable   {

		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
			return returnDBResultSet(db, qryString);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param queryAndParams
	 * @param response
	 * @param startTime
	 * @param endTime
	 * @return String
	 * This method is use to encapsulate all DB request/response details within HTML tags for inclusion in Extent Report.
	 */
	private String getQueryDetails(String query, String response, long startTime,long endTime) {
		boolean isDeepReporting = Boolean.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.DEEP_REPORTING));
		if (isDeepReporting) {
			query = query + JavaUtils.getAsHTML("Result Set : ", "Updated Record Count = " + response)
					+ "QUERY EXECUTION TIME = " + (endTime - startTime) + " ms";
		}
		return query;
	}
	
	/**
	 * @param qryParams as map
	 * @param qryString as string
	 * @return list of database result
	 * @throws Throwable 
	 */
	public Object returnDBValue(String db, Map<String, String> qryParams, String qryString) throws Throwable {
		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
			return returnDBValue(db, qryString);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param db - Name of DB
	 * @param qryParams as map
	 * @param qryString as String
	 * @throws Throwable 
	 */
	public String updateDB(String db,Map<String, String> qryParams,String qryString) throws Throwable {
		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
			return updateDB(db,qryString);	
		} catch (Exception e) {
			throw e;
		}
		

	}
}
