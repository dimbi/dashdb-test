#dashDB and Node.js

For issues that you encounter with this service, go to [**Get help**](https://www.ibmdw.net/bluemix/get-help/) in the Bluemix development community.

You can bind a dashDB service instance to a node.js runtime in Bluemix and then work with the data in the dashDB database. The Bluemix Node.js runtime will automatically lay down native driver dependencies when you have a dashDB Service instance bound to your app. The sample illustrated here uses express and jade node modules.

###Required components

The following components are required to connect dashDB service from a Node.js application. The are all described in further detail in this topic.

- package.json
- Node.js program
- A dashDB service instance

####package.json
The package.json contains information about your app and its dependencies. It is used by npm tool to install, update, remove and manage the node modules you need. Add the ibm_db dependency to your package.json:
```
  {
    "engines": { "node": "0.10.21" },
    "name": "dashDBsample",
    "version": "0.0.1",
    "description": "A sample node app",
    "dependencies" : {
      "ibm_db" : "~0.0.1",
	  "express": "3.x",
	  "jade": "x"
    },
    "scripts": {
       "start": "node app.js"
	}
}
```


###Connecting to dashDB from Node.js code

In your Node.js code, parse the VCAP_SERVICES environment variable to retrieve the database connection information and connect to the database as shown in the following example.

For more information on the structure of the VCAP_SERVICES environment variable see [Getting Started with dashDB Service](http://www.ng.bluemix.net/docs/#services/dashDB/index.html#dashDB)

```
var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
...
var db2;

var env = JSON.parse(process.env.VCAP_SERVICES);
db2 = env['dashDB'][0].credentials;

var connString = "DRIVER={DB2};DATABASE=" + db2.db + ";UID=" + db2.username + ";PWD=" + db2.password + ";HOSTNAME=" + db2.hostname + ";port=" + db2.port;

app.get('/', routes.listSysTables(ibmdb,connString));
```

In the routes/index.js file, open the connection using the connection string and query the database as follows.
```
exports.listSysTables = function(ibmdb,connString) {
	return function(req, res) {
		   
	   ibmdb.open(connString, function(err, conn) {
			if (err ) {
			 res.send("error occurred " + err.message);
			}
			else {
				conn.query("SELECT FIRST_NAME, LAST_NAME, EMAIL, WORK_PHONE from GOSALESHR.employee FETCH FIRST 10 ROWS ONLY", function(err, tables, moreResultSets) {

							
				if ( !err ) { 
					res.render('tablelist', {
						"tablelist" : tables
						
					 });

					
				} else {
				   res.send("error occurred " + err.message);
				}

				/*
					Close the connection to the database
					param 1: The callback function to execute on completion of close function.
				*/
				conn.close(function(){
					console.log("Connection Closed");
					});
				});
			}
		} );
	   
	}
}
```
Please download and examine the attached sample for the rest of the code. If you use this sample, please edit the manifest.yml and specify a unique 'host' value.

###Uploading your application

Use the 'cf push' command to push your application to Bluemix

`cf push <app-name> `


###Related Links
- [IBM DB2 node.js API](https://www.npmjs.org/package/ibm_db#api)

- [Using IBM DB2 from node.js](https://www.ibm.com/developerworks/community/blogs/pd/entry/using_ibm_db2_from_node_js4?lang=en)

- [IBM DB2 v10.5 Knowledge Center](https://www-01.ibm.com/support/knowledgecenter/SSEPGG_10.5.0/com.ibm.db2.luw.kc.doc/welcome.html)
- [IBM DB2 developerWorks](http://www.ibm.com/developerworks/data/products/db2/)

