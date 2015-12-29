
<?php

//parse vcap
if( getenv("VCAP_SERVICES") ) {
    $json = getenv("VCAP_SERVICES");
} 
# No DB credentials
else {
    echo "No vcap services available.";
    return;
}

# Decode JSON and gather DB Info
$services_json = json_decode($json,true);
$blu = $services_json["dashDB"];
if (empty($blu)) {
    echo "No dashDB service instance is bound. Please bind a dashDB service instance";
    return;
}

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
else {
    print_r("Connection to dashDB failed.");
}
?>