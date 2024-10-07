package com.qe.commoncore.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.datastax.driver.core.ResultSet;
import com.qe.api.commoncore.BaseTest;
import com.qe.api.constants.DBConstants;
import com.qe.commoncore.constants.ContextConstant;

import java.sql.Connection;
import java.sql.DriverManager;

import com.datastax.driver.core.*;

import com.aventstack.extentreports.Status;
public class DBUtil {
	private static Configurator configurator = Configurator.getInstance();
	private final String db;
	private final String port;
	private final String host;
    private final String user;
    private final String password;
	private final String schema;
	public final Session session;
	public final Connection connection;


	// This DBUtils object is currently immutable. 
	// We parameterize it to allow changing the connection if required.
	public DBUtil(String DB) throws Throwable {
		this.db = DB;
		switch (DB) {
			case "DB1":
				this.host = configurator.getEnvironmentParameter(DBConstants.DB1Host);
				this.port = configurator.getEnvironmentParameter(DBConstants.DB1Port);
				this.user = configurator.getEnvironmentParameter(DBConstants.DB1User);
				this.password = configurator.getEnvironmentParameter(DBConstants.DB1Password);
				this.schema = configurator.getEnvironmentParameter(DBConstants.DB1Schema);
				this.session = getCassandraDbConnection();
				this.connection=null;
				break;

			case "DB2":
				this.host = configurator.getEnvironmentParameter(DBConstants.DB2Host);
				this.port = configurator.getEnvironmentParameter(DBConstants.DB2Port);
				this.user = configurator.getEnvironmentParameter(DBConstants.DB2User);
				this.password = configurator.getEnvironmentParameter(DBConstants.DB2Password);
				this.schema = configurator.getEnvironmentParameter(DBConstants.DB2Schema);
				this.connection = getSQLAzureDbConnection();
				this.session=null;
				break;

			case "TABLE3":
				this.host = configurator.getEnvironmentParameter(DBConstants.DB3Host);
				this.port = configurator.getEnvironmentParameter(DBConstants.DB3Port);
				this.user = configurator.getEnvironmentParameter(DBConstants.DB3User);
				this.password = configurator.getEnvironmentParameter(DBConstants.DB3Password);
				this.schema = configurator.getEnvironmentParameter(DBConstants.DB3Schema);
				this.session = getCassandraDbConnection();
				this.connection = null;
				break;
			default:
				throw new IllegalArgumentException("Invalid DBType: " + DB);
		}
	}

	/*
	 * getCassandraDbConnection method creates a connection to DB. It throws an
	 * exception when the connection is not successful.
	 */
	private Session getCassandraDbConnection() throws Throwable {
		System.out.println("Creating Cassendra Server connection...");
		try {
			Cluster.Builder builder = null;
			builder = Cluster.builder().withPort(Integer.parseInt(this.port));
			builder.addContactPoint(this.host);
			Cluster clusterPricing = builder.withCredentials(this.user,this.password).build();
			Session session=  clusterPricing.connect(this.schema);
			System.out.println(this.db+" DB Connection Successful");
			return session;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * getAzureDbConnection method creates a connection to DB. It throws an
	 * exception when the connection is not successful.
	 */
	private Connection getSQLAzureDbConnection() throws Throwable {
		System.out.println("Creating SQL Server connection...");
		try {
		String serverName = this.host;
		String databaseName = this.schema; // Replace with your Azure SQL database name
		String user =this.user; // Replace with your Azure AD username (e.g., john@contoso.com)
		String password = this.password;
		String connectionUrl = String.format(
				"jdbc:sqlserver://%s;database=%s;authentication=ActiveDirectoryPassword;user=%s;password=%s;",
				serverName, databaseName, user, password
		);
		 Connection connection = DriverManager.getConnection(connectionUrl);
			System.out.println(this.db+" connection created successfully");
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * 1. returnDBValue method is used for fetching the single value obtained by
	 * executing the query. 2. Fetches only one value from the first column of the
	 * result set. 3. The Return type of this method is String. 4. Returns NULL when
	 * there is no data retrieved.
	 */
	public Object returnCassandraDBValue(String query) throws Throwable {
		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		try (Session con = this.session){
			long startTime = System.currentTimeMillis();
			rs = (ResultSet) con.execute(query);
			long endTime = System.currentTimeMillis();

			List<Row> rows = rs.all();
			for (Row row : rows) {
				value =row.getObject(0) ;
				break;
			}

			String queryDetails =getQueryDetails(query, (value == null) ? "" : value.toString(), startTime, endTime);
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
		}
	}

	/*
	 * 1. returnListOfDBValues method is used for fetching multiple values obtained
	 * by executing the query. 2. Fetches values only from the first column of the
	 * result set. 3. The Return type of this method is List<String>. 4. Returns
	 * NULL when there is no data retrieved.
	 */
	public List<Object> returnListOfCassandraDBValues(String query) throws Throwable {
		List<Object> DBvalues = new ArrayList<Object>();
		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		try(Session con =this.session) {
			long startTime = System.currentTimeMillis();
			rs = (ResultSet) con.execute(query);
			long endTime = System.currentTimeMillis();

			List<Row> rows = rs.all();
			for (Row row : rows) {
				rowCounter++;
				value = row.getObject(0);
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
	public  List<HashMap<String, Object>> returnCassandraDBResultSet(String query) throws Throwable {
		List<HashMap<String, Object>> resultSetMap = new ArrayList<HashMap<String, Object>>();
		LinkedHashMap<String, Object> headerAndValues = new LinkedHashMap<String, Object>();

		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		try(Session con = this.session) {
			long startTime = System.currentTimeMillis();
			rs = (ResultSet) con.execute(query);
			long endTime = System.currentTimeMillis();
			List<Row> rows = rs.all();
			List<ColumnDefinitions.Definition> column = rs.getColumnDefinitions().asList();

			System.out.println("Row count : "+rows.size());
			for (Row row : rows) {
				rowCounter++;
				for (int i = 0; i < column.size(); i++) {
					String colName = column.get(i).getName();
					headerAndValues.put(colName, row.getObject(i));
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
		}
	}

	/*
	 * updateDatabase method is used to update the values in DB based on the query
	 * passed.
	 * Row count issue need to be fixed>..
	 */
	public String updateCassandraDB(String query) throws Throwable {
		int rowCounter = 0;
		try{
		    Session con = this.session;
			long startTime = System.currentTimeMillis();
			com.datastax.driver.core.PreparedStatement prps= con.prepare(query);
			BoundStatement bound = prps.bind();
			com.datastax.driver.core.ResultSet rs=session.execute(bound);
			long endTime = System.currentTimeMillis();
			String queryDetails = getQueryDetails(query, String.valueOf(rowCounter), startTime, endTime);
			{
				BaseTest.reporter.logTestStepDetails(Status.INFO, "<pre>" + queryDetails + "</pre>");
				BaseTest.reporter.logTestStepDetails(Status.INFO, "Total No Of Rows Affected:" + rowCounter);
			}
			return "Total rows affected:" + rowCounter;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param qryParams as map
	 * @param qryString as string
	 * @return list of database result
	 * @throws Throwable
	 * This method maps a query parameter MAP to its's original query string and return an List of hash map object based on the query.
	 */
	public List<HashMap<String, Object>> getDataFromCassandraDB(Map<String, String> qryParams, String qryString) throws Throwable   {

		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw e;
		}
		return returnCassandraDBResultSet(qryString);
	}

	/**
	 *
	 * @param response
	 * @param startTime
	 * @param endTime
	 * @return String
	 * This method is use to encapsulate all DB request/response details within HTML tags for inclusion in Extent Report.
	 */
	private String getQueryDetails(String query, String response, long startTime,long endTime) {
		boolean isDeepReporting = Boolean.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.DEEP_REPORTING));
			query = query + JavaUtils.getAsHTML("Result Set : ", "Updated Record Count = " + response)
					+ "QUERY EXECUTION TIME = " + (endTime - startTime) + " ms";
		return query;
	}


	public Object returnCassandraDBValue(Map<String, String> qryParams, String qryString) throws Throwable {
		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw e;
		}
		return returnCassandraDBValue(qryString);
	}


	public String updateCassandraDB(Map<String, String> qryParams,String qryString) throws Throwable {
		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw e;
		}
		return updateCassandraDB(qryString);

	}

	/*
	 * 1. returnDBValue method is used for fetching the single value obtained by
	 * executing the query. 2. Fetches only one value from the first column of the
	 * result set. 3. The Return type of this method is String. 4. Returns NULL when
	 * there is no data retrieved.
	 */
	public Object returnAzureSQLDBValue(String query) throws Throwable {
		Statement stmt = null;
		PreparedStatement prepStmt = null;
		java.sql.ResultSet  rs = null;
		Object value = null;
		int rowCounter = 0;

		try (Connection con = this.connection){
			long startTime = System.currentTimeMillis();
			prepStmt = con.prepareStatement(query);
			rs = prepStmt.executeQuery();
			long endTime = System.currentTimeMillis();

			while (rs.next()) {
				rowCounter++;
				value = rs.getObject(1);
			}

			String queryDetails =getQueryDetails(query, (value == null) ? "" : value.toString(), startTime, endTime);
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
		}
	}

	/*
	 * 1. returnListOfDBValues method is used for fetching multiple values obtained
	 * by executing the query. 2. Fetches values only from the first column of the
	 * result set. 3. The Return type of this method is List<String>. 4. Returns
	 * NULL when there is no data retrieved.
	 */
	public List<Object> returnListOfAzureSQLDBValues(String query) throws Throwable {
		List<Object> DBvalues = new ArrayList<Object>();
		Statement stmt = null;
		PreparedStatement prepStmt = null;
		java.sql.ResultSet  rs = null;
		Object value = null;
		int rowCounter = 0;

		try(Connection con = this.connection) {
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
	public List<HashMap<String, Object>> returnAzureSQLDBResultSet(String query) throws Throwable {
		List<HashMap<String, Object>> resultSetMap = new ArrayList<HashMap<String, Object>>();
		LinkedHashMap<String, Object> headerAndValues = new LinkedHashMap<String, Object>();

		Statement stmt = null;
		PreparedStatement prepStmt = null;
		java.sql.ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		try(Connection con = this.connection) {
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
		}
	}


	public String updateAzureSQLDB(String query) throws Throwable {
		Statement stmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		Object value = null;
		int rowCounter = 0;

		try(Connection con = this.connection) {
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
		}
	}


	public List<HashMap<String, Object>> getDataFromAzureSQLDB(Map<String, String> qryParams, String qryString) throws Throwable   {

		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw e;
		}
		return returnAzureSQLDBResultSet(qryString);
	}



	public Object returnAzureSQLDBValue(Map<String, String> qryParams, String qryString) throws Throwable {
		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw e;
		}
		return returnAzureSQLDBValue(qryString);
	}


	public String updateAzureSQLDB(Map<String, String> qryParams,String qryString) throws Throwable {
		try {
			for (Map.Entry<String, String> entry : qryParams.entrySet()) {
				qryString = qryString.replaceAll(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw e;
		}
		return updateAzureSQLDB(qryString);

	}
}
