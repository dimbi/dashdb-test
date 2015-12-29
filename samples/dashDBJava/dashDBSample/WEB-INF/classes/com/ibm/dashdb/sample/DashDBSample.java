package com.ibm.dashdb.sample;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;

// @author Prrsahoo, IBM 2014
// @version 0.2

/**
 * Servlet implementation class BLUDWSample
 */
@WebServlet("/DashDBSample")
public class DashDBSample extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DashDBSample() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		writer.println("IBM dashDB, Java Demo Application using DB2 drivers");
		writer.println("Servlet: " + this.getClass().getName());
		writer.println();
		writer.println("VCAP Host:" + System.getenv("VCAP_APP_HOST") + ":"
				+ System.getenv("VCAP_APP_PORT"));
		writer.println("Host IP:" + InetAddress.getLocalHost().getHostAddress());

		BasicDBObject bludb = null;

		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the for DB2 connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		writer.println("VCAP_SERVICES content: " + VCAP_SERVICES);

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			String thekey = null;
			Set<String> keys = obj.keySet();
			writer.println("Searching through VCAP keys");
			// Look for the VCAP key that holds the dashDB information
			for (String eachkey : keys) {
				writer.println("Key is: " + eachkey);
				// The old name for this service was AnalyticsWarehouse
				if (eachkey.contains("dashDB")) {
					thekey = eachkey;
				}
			}
			if (thekey == null) {
				writer.println("Cannot find any dashDB service in the VCAP; exiting");
				return;
			}

			BasicDBList list = (BasicDBList) obj.get(thekey);
			bludb = (BasicDBObject) list.get("0");
			writer.println("Service found: " + bludb.get("name"));
			bludb = (BasicDBObject) bludb.get("credentials");
			
		} else {
			writer.println("VCAP_SERVICES is null");
			return;
		}

		writer.println();
		writer.println("full database uri: " + bludb.get("uri"));
	
		Connection con = null;

		try {
			writer.println();
			writer.println("Loading the Database Driver");
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			// Connect to the database
			writer.println("Connecting to the database");
			writer.println();
			
			String jdbcurl =  (String) bludb.get("jdbcurl");
			String user = (String) bludb.get("username");
		    String password = (String) bludb.get("password");
			
			con = DriverManager.getConnection(jdbcurl, user,password);
			// or if the username & password are in the url:
			// con = DriverManager.getConnection(databaseUrl);
			// Commit changes manually
			con.setAutoCommit(false);
		} catch (SQLException e) {
			writer.println("Error connecting to database");
			writer.println("SQL Exception: " + e);
			return;
		} catch (ClassNotFoundException e) {
			writer.println("Error loading driver");
			writer.println("Error: " + e);
			return;
		}

		Statement stmt = null;
		String sqlStatement = "";
				
		// Execute some SQL statements on the database 
		try {
			stmt = con.createStatement();
			sqlStatement = "SELECT FIRST_NAME, LAST_NAME from GOSALESHR.employee FETCH FIRST 10 ROWS ONLY";
			ResultSet rs = stmt.executeQuery(sqlStatement);
			writer.println("Executing: " + sqlStatement);

			writer.println("FIRST_NAME \t\t LAST_NAME");
			// Process the result set
			while (rs.next()) {
				writer.println(rs.getString(1) + "\t\t" +  rs.getString(2));
			}
			// Close the ResultSet
			rs.close();

		} catch (SQLException e) {
			writer.println("Error executing:" + sqlStatement);
			writer.println("SQL Exception: " + e);
		}

		// Close everything off
		try {
			// Close the Statement
			stmt.close();
			// Connection must be on a unit-of-work boundary to allow close
			con.commit();
			// Close the connection
			con.close();
			writer.println("Finished");

		} catch (SQLException e) {
			writer.println("Error closing things off");
			writer.println("SQL Exception: " + e);
		}

		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
