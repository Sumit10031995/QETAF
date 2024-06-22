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

	private String driver;
	private String url;
	private String user;
	private String password;

	public DBUtil(String connectionName, String DBType, String dbUrl, String dbUsername,String dbPassword) {
		switch (DBType) {
		case "SQL_DB":
			this.driver="com.ibm.db2.jcc.DB2Driver";
			this.url= dbUrl;
			this.user=dbUsername;
			this.password=dbPassword;
			break;
		case "AZURE_MSSQL":
			this.driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			this.url= dbUrl;
			this.user=dbUsername;
			this.password=dbPassword;
			break;
		default:
			
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
	@SuppressWarnings("unchecked")
	public synchronized Object returnDBValue(String DB, String query) throws Throwable {
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepStmt = null;
		Connection con = getDbConnection(DB);
		ArrayList<Object> DBParams, queryAndParams;
       try {
		queryAndParams = CreatePreparedStatement(query);
		query = (String) queryAndParams.get(0);
		DBParams = (ArrayList<Object>) queryAndParams.get(1);
		long startTime=System.currentTimeMillis();
		if (DBParams.size() > 0) {
			prepStmt = setPreparedStatement(con, query, DBParams);
			rs = prepStmt.executeQuery();
		} else {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
		}
		long endTime=System.currentTimeMillis();
		Object value = null;
		int rowCounter = 0;
		while (rs.next()) {
			rowCounter++;
			value = rs.getObject(1);
		}
		String queryDetails=getQueryDetails(queryAndParams,(value==null)?"":value.toString(),startTime,endTime);
		BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>"+queryDetails+ "</pre>");
		if (rowCounter == 0) {
			BaseTest.reporter.logTestStepDetails(Status.INFO, "ERROR:::: No data found for the query-> " + query);
			System.err.println("ERROR:::: No data found for the query-> " + query);
			return null;
		} else {
			return value;
		}
       }catch(Exception e) {
    	   throw e;
       }finally {
    	   con.close();
       }
	}

	private PreparedStatement setPreparedStatement(Connection con, String query, ArrayList<Object> DBParams)
			throws Throwable {
		PreparedStatement prepStmt = null;
		prepStmt = con.prepareStatement(query);
		for (int i = 0; i < DBParams.size(); i++) {
			prepStmt.setObject(i + 1, DBParams.get(i));
		}
		return prepStmt;
	}

	private synchronized static ArrayList<Object> CreatePreparedStatement(String query) {

		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> queryAndParams = new ArrayList<Object>();

		for (int i = 0; i < query.length(); i++) {

			if (query.charAt(i) == '=') {
				boolean found = false;
				for (int j = i + 1; j < query.length(); j++) {
					if (query.charAt(j) != ' ') {
						if (query.charAt(j) != '?') {
							if (query.charAt(j) == '\'') {
								for (int k = j + 1; k < query.length(); k++) {
									if (query.charAt(k) == '\'') {
										found = true;
										String valueToRestore = query.substring(j + 1, k);
										System.out.println(valueToRestore);
										params.add(valueToRestore);
										query = query.substring(0, j) + "?" + query.substring(k + 1, query.length());
										i = 0;
										break;
									}
								}
							} else {
								for (int l = j + 1; l < query.length(); l++) {
									if (query.charAt(l) == ' ') {
										String toReplace = query.substring(j, l);
										found = true;
										try {
											long number = Integer.parseInt(toReplace);
											params.add(number);
											query = query.substring(0, j) + "?" + query.substring(l, query.length());
											i = 0;
										} catch (Exception e) {
											i = l;
										}
										break;
									}
									if (l == query.length() - 1) {
										String toReplace = query.substring(j, l + 1);
										found = true;
										try {
											long number = Integer.parseInt(toReplace);
											params.add(number);
											query = query.substring(0, j) + "?"
													+ query.substring(l + 1, query.length());
											i = 0;
										} catch (Exception e) {
											i = l + 1;
										}
										break;
									}
								}
							}
						} else {
							break;
						}
					}
					if (found) {
						break;
					}
				}
			}

		}

		System.out.println(query);
		System.out.println(params);
		queryAndParams.add(query);
		queryAndParams.add(params);
		return queryAndParams;

	}

	/*
	 * 1. returnListOfDBValues method is used for fetching multiple values obtained
	 * by executing the query. 2. Fetches values only from the first column of the
	 * result set. 3. The Return type of this method is List<String>. 4. Returns
	 * NULL when there is no data retrieved.
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<Object> returnListOfDBValues(String DB, String query) throws Throwable {
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = getDbConnection(DB);
		PreparedStatement prepStmt = null;
		ArrayList<Object> DBParams, queryAndParams;
       try {
		queryAndParams = CreatePreparedStatement(query);
		query = (String) queryAndParams.get(0);
		DBParams = (ArrayList<Object>) queryAndParams.get(1);
		long startTime=System.currentTimeMillis();
		if (DBParams.size() > 0) {
			prepStmt = setPreparedStatement(con, query, DBParams);
			rs = prepStmt.executeQuery();
		} else {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
		}
		long endTime=System.currentTimeMillis();
		Object value = "";
		List<Object> DBvalues = new ArrayList<Object>();
		int rowCounter = 0;
		while (rs.next()) {
			rowCounter++;
			value = rs.getObject(1);
			DBvalues.add(value);
		}
		String queryDetails=getQueryDetails(queryAndParams,DBvalues.toString(),startTime,endTime);
		BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>"+queryDetails+ "</pre>");
		if (rowCounter == 0) {
			BaseTest.reporter.logTestStepDetails(Status.INFO, "ERROR:::: No data found for the query-> " + query);
			System.err.println("ERROR:::: No data found for the query-> " + query);
			return null;
		} else {
			return DBvalues;
		}
       }catch(Exception e) {
    	   throw e;
       }finally {
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
	@SuppressWarnings("unchecked")
	public synchronized List<HashMap<String, Object>> returnDBResultSet(String DB, String query) throws Throwable {
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = getDbConnection(DB);
		PreparedStatement prepStmt = null;

		ArrayList<Object> DBParams, queryAndParams;
       try {
		queryAndParams = CreatePreparedStatement(query);
		query = (String) queryAndParams.get(0);
		DBParams = (ArrayList<Object>) queryAndParams.get(1);
		long startTime=System.currentTimeMillis();
		if (DBParams.size() > 0) {
			prepStmt = setPreparedStatement(con, query, DBParams);
			rs = prepStmt.executeQuery();
		} else {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
		}
		long endTime=System.currentTimeMillis();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		List<HashMap<String, Object>> resultSetMap = new ArrayList<HashMap<String, Object>>();
		int rowCounter = 0;
		while (rs.next()) {
			rowCounter++;
			LinkedHashMap<String, Object> headerAndValues = new LinkedHashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				String colName = rsmd.getColumnName(i);
				headerAndValues.put(colName, rs.getObject(i));
			}
			resultSetMap.add(headerAndValues);
		}
		String queryDetails=getQueryDetails(queryAndParams,resultSetMap.toString(),startTime,endTime);
		BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>"+queryDetails+ "</pre>");
		if (rowCounter == 0) {
			BaseTest.reporter.logTestStepDetails(Status.INFO, "ERROR:::: No data found for the query-> " + query);
			System.err.println("ERROR:::: No data found for the query-> " + query);
			return null;
		} else {
			return resultSetMap;
		}
       }catch(Exception e) {
    	   throw e;
       }finally {
    	   con.close();
       }
	}

	/*
	 * updateDatabase method is used to update the values in DB based on the query
	 * passed.
	 */
	@SuppressWarnings("unchecked")
	public synchronized String updateDB(String DB, String query) throws Throwable {
		Statement stmt = null;
		Connection con = getDbConnection(DB);
		PreparedStatement prepStmt = null;
		int response;
		ArrayList<Object> DBParams, queryAndParams;
       try {
		queryAndParams = CreatePreparedStatement(query);
		query = (String) queryAndParams.get(0);
		DBParams = (ArrayList<Object>) queryAndParams.get(1);
		long startTime=System.currentTimeMillis();
		if (DBParams.size() > 0) {
			prepStmt = setPreparedStatement(con, query, DBParams);
			response = prepStmt.executeUpdate();
		} else {
			stmt = con.createStatement();
			response = stmt.executeUpdate(query);
		}
		long endTime=System.currentTimeMillis();
		String queryDetails=getQueryDetails(queryAndParams,String.valueOf(response),startTime,endTime);
		 {
		BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>" + queryDetails + "</pre>");
		BaseTest.reporter.logTestStepDetails(Status.INFO, "Total No Of Rows Affected:" + response);
		}
		return "Total rows affected:" + response;
       }catch(Exception e) {
    	   throw e;
       }finally {
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
	public synchronized List<HashMap<String, Object>> getDataFromDB(String db, Map<String, String> qryParams, String qryString) throws Throwable   {

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
	private static String getQueryDetails(ArrayList<Object> queryAndParams, String response, long startTime,long endTime) {
		boolean isDeepReporting = Boolean.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.DEEP_REPORTING));
		String queryDetails = "Query" + "<br><br>" + queryAndParams.get(0) + "<br>" + "PARAMS = "+ queryAndParams.get(1);
		if (isDeepReporting) {
			queryDetails = queryDetails + JavaUtils.getAsHTML("Result Set : ", "Updated Record Count = " + response)
					+ "QUERY EXECUTION TIME = " + (endTime - startTime) + " ms";
		}
		return queryDetails;
	}
	
	/**
	 * @param qryParams as map
	 * @param qryString as string
	 * @return list of database result
	 * @throws Throwable 
	 */
	public  synchronized Object returnDBValue(String db, Map<String, String> qryParams, String qryString) throws Throwable {
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
	public synchronized String updateDB(String db,Map<String, String> qryParams,String qryString) throws Throwable {
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
