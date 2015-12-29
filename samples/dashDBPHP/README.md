#dashDB and PHP

For issues that you encounter with this service, go to [Get help](https://www.ibmdw.net/bluemix/get-help/) in the Bluemix development community.

You can bind a dashDB service instance to a PHP runtime and work with your data in a dashDB database. To enable this connection, the PHP runtime needs the ibm_db2 PECL module for connecting to the dashDB Database. This topic explains how to get your code running using this method.

###Required components

The following components are required to connect dashDB to a PHP application. The are all described in further detail in this topic.

    index.php
    Buildpack to install dependencies

##index.php

Write an index.php file and in it connect to dashDB database from PHP code

In the PHP code, parse the VCAP_SERVICES environment variable to retrieve the database connection information and create a connection string as shown in the following example:

```
//parse vcap
if( getenv("VCAP_SERVICES") ) {
    $json = getenv("VCAP_SERVICES");
} 
# No DB credentials
else {
    throw new Exception("No Database Information Available.", 1);
}

# Decode JSON and gather DB Info
$services_json = json_decode($json,true);
$bludb_config = $services_json["dashDB"][0]["credentials"];

// create DB connect string
$conn_string = "DRIVER={IBM DB2 ODBC DRIVER};DATABASE=".
   $bludb_config["db"].
   ";HOSTNAME=".
   $bludb_config["host"].
   ";PORT=".
   $bludb_config["port"].
   ";PROTOCOL=TCPIP;UID=".
   $bludb_config["username"].
   ";PWD=".
   $bludb_config["password"].
   ";";
```

Now connect to BLUDB, run your SQL query and process the resultset as shown below 

```
// connect to BLUDB
$conn = db2_connect($conn_string, '', '');
if ($conn) {

    //prepare, execute SQL and iterate through resultset
    $sql = "SELECT FIRST_NAME, LAST_NAME, EMAIL, WORK_PHONE from GOSALESHR.employee FETCH FIRST 10 ROWS ONLY";
	
    $stmt = db2_prepare($conn, $sql);
    $result = db2_execute($stmt);
?>

   <h1>10 rows from GOSALESHR.EMPLOYEE table </h1>
     <table border=\"1\">
     <tr><td><b>Firstname</b></td><td><b>LastName</b></td><td><b>Email</b></td><td><b>WorkPhone</b></td></tr>

<?php

	while ($row = db2_fetch_array($stmt)) {
?> 
      <tr>
	  <td><?php print_r($row[0]);?></td>
	  <td><?php print_r($row[1]);?></td>
	  <td><?php print_r($row[2]);?></td>
	  <td><?php print_r($row[3]);?></td>
	  </tr>
<?php		
	}
?>
    </table>

<?php
    db2_close($conn);
}  
```

Please download and examine the attached sample for the rest of the code. If you use this sample, please edit the manifest.yml and specify a unique 'host' value.

###Uploading your application to bluemix 

Use the -b option of the 'cf push' command to specify either the zend buildpack 

```
cf push <app-name> -b https://github.com/zendtech/zend-server-php-buildpack-bluemix
```
or the db2phpbuildpack 

```
cf push <app-name> -b https://github.com/ibmdb/db2heroku-buildpack-php
```
when pushing your application to Bluemix.
###Related Links
- [DB2 PHP API](http://in1.php.net/ibm_db2)
- [IBM DB2 v10.5 Knowledge Center](https://www-01.ibm.com/support/knowledgecenter/SSEPGG_10.5.0/com.ibm.db2.luw.kc.doc/welcome.html)
- [IBM DB2 developerWorks](http://www.ibm.com/developerworks/data/products/db2/)