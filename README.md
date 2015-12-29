# Overview

Welcome to the sample applications repository for dashDB on Bluemix. These applications demonstrate how to connect to the dashDB service and insert or retrieve data.


# Contents

It contains the following samples.

 [**dashDBJava**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBJava) : A Java Servlet using straightforward JDBC to connect to the SQL Database  
 
 [**dashDBLibertyProfile**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBLibertyProfile) : A Java Servlet using straightforward JDBC to connect to the SQL Database. 
 For this sample make sure you name your service "SampleAnalyticsWarehouse" as that is the name the sample looks for.  
 
 [**dashDBRuby**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBRuby) : A Ruby Sinatra Sample. 
 
 [**dashDBNodeJS**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBNodeJS) : A Node.js Sample. 
 
 [**dashDBPython**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBPython) : A Python Sample. 
		
 [**dashDBPHP**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBPHP) : A PHP Sample. 
 
 [**dashDBGo**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBGo) : A Go Sample.  

 [**dashDBScala**](https://hub.jazz.net/project/ibmdatabase/dashDB/overview#https://hub.jazz.net/git/ibmdatabase%252FdashDB/list/master/samples/dashDBScala-Play) : A Scala Play framework Sample.  
# Usage

You can use the VCAP_SERVICES environment variable to connect to the dashDB. The Java API requires DB2 client JAR files to be in the class path for your application.

Refer to the following information for using the dashDB service within your application. You can use the [Bluemix user interface](https://ace.ng.bluemix.net/) to create applications and services or you can do the same from command line interface by using the Cloud Foundry **cf tool**. You need this tool to deploy applications into the Bluemix cloud.

Before we get started you need to install the Cloud Foundry **cf tool** on your desktop. Instructions on how to do this can be found in the [Bluemix Quick Start](http://www.ng.bluemix.net/docs/#starters/BuildingWeb.html%23building_web) guide

### Managing dashDB service instance with CLI

First, create a new service instance with the cf create-service command. Type the following command at terminal:

`$ cf create-service dashDB Entry <service-instance-name>`

Here <service-instance-name> is the name of the service instance. You can give any name to your service instance.

After creating a new service, you can use the cf services command to list all available service instances that you have created.

`$ cf services`

Before using a service in application, you must bind the service to your application. Use the cf bind-service command:

`$ cf bind-service <app-name> <service-instance-name>`

Here <app-name> is the name of your application.

You need to select one of the applications and one of the services from the list of existing applications and services. Once the binding action succeeds, cf will return a message to you.

### Connecting to the service in your application

After binding a service instance to the application, the following configuration is added to your VCAP_SERVICES environment variable.

```
{
  "dashDB": {
    "name": "service-instance-name",
    "label": "dashDB",
    "plan": "Entry",
    "credentials": {
      "port": 50000,
      "db": "BLUDB",
      "username": "username",
      "password": "password",
      "host": "bluemix05.bluforcloud.com",
      "hostname": "bluemix05.bluforcloud.com",
      "jdbcurl": "jdbc:db2://bluemix05.bluforcloud.com:50000/BLUDB",
      "uri": "db2://username:password@bluemix05.bluforcloud.com:50000/BLUDB"
    }
  }
}
```   			

As you can see, the VCAP_SERVICE environment variable includes the following items:

- key: The name and version of the service [AnalyticsWarehouse]
- name: The name of the service instance
- hostname: The host name of the database server
- host: The IP address of the database server
- port: The port number of the database server
- username: The user name for authentication
- password: The password for authentication
- db: The database name
- jdbcurl: The JDBC connection URL for the database instance
- uri: The URI of the database instance

> **Note:** There might be slight differences between the VCAP_Services shown here, the one visible through the BlueMix user interface and the one visible to the code. The main structure and most important fields are shown above.

Now you can use this dashDB service instance from a variety of Bluemix run-times like Java web applications, Node.js applications, Ruby applications, Python applications and PHP applications. The following code snippet shows how to obtain the service credential information and connect to the dashDB instance using basic Java JDBC code:
```
	  import com.ibm.nosql.json.api.*; 
	  import com.ibm.nosql.json.util.*;
	   
	  ...
	  String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
	  if (VCAP_SERVICES != null) {
		BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
		String thekey = null;
		Set<String> keys = obj.keySet();
		for (String eachkey : keys)
		  if (eachkey.contains("dashDB"))
			 thekey = eachkey;
		BasicDBList list = (BasicDBList) obj.get(thekey);
		BasicDBObject bludb = (BasicDBObject) list.get("0");
		bludb = (BasicDBObject) bludb.get("credentials");
	  }
```

The code listed above imports some utility packages to help with the JSON parsing of the VCAP_SERVICES structure. It first checks whether this VCAP_SERVICES environment variable exists. If it does exist, then it searches for a key containing the string "AnalyticsWarehouse". If it finds this string then it navigates to the first element of the list and get the credentials structure. This contains all the elements needed to access the dashDB Database.
    
### Interacting with the dashDB service instance to insert and query records

You can interact with the dashDB service instance by using the credential information. The following example demonstrates how to connect to the database and execute a SQL statement:

```
  Connection con = null;
  try {
	Class.forName("com.ibm.db2.jcc.DB2Driver");
	String jdbcurl =  (String) bludb.get("jdbcurl");
	String user = (String) bludb.get("username");
	String password = (String) bludb.get("password");
	con = DriverManager.getConnection(jdbcurl, user,password);
	con.setAutoCommit(false);
  } catch (SQLException e) {
	writer.println("SQL Exception: " + e);
	return;
  } catch (ClassNotFoundException e) {
	writer.println("ClassNotFound Exception: " + e);
	return;
  }
  Statement stmt = null;
  String sqlStatement = "";
  try {
	stmt = con.createStatement();
    sqlStatement = "SELECT FIRST_NAME, LAST_NAME from GOSALESHR.employee FETCH FIRST 10 ROWS ONLY";
	ResultSet rs = stmt.executeQuery(sqlStatement);
  } catch (SQLException e) {
	writer.println("Error creating table: " + e);
  }
``` 			

### Pushing your application to Bluemix

After coding is finished, you can deploy your application to the Bluemix environment for verification. To deploy an application, enter the root directory of the Java application and use the following command:

`$ cf push <app-name>`

### Unbinding or deleting a service instance

To unbind a service instance from an application, use the following command:
`cf unbind-service <app-name> <service-instance-name>`

To delete a service instance use:
`cf delete-service <service-instance-name>`

