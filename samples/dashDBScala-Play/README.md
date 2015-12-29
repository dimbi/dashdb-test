#dashDB and Scala Play Framework

For issues that you encounter with this service, go to [**Get help**](https://www.ibmdw.net/bluemix/get-help/) in the Bluemix development community.

You can bind your Scala Play application to the dashDB Service database and work with your data. This topic explains how to get your code running using this method.  

  
Required components

The following components are required to connect dashDB from a Play application. 

- Play Framework  
   connecting Anorm data access layer to dashDB  
   Querying dashDB using Anorm  
- dashDB Service instance

###dashDB Service instance
 Please provision a dashDB service instance in your Bluemix org & space in which you are going to use this application. You can use the Bluemix UI or cf commandline to achieve this. This app requires the name of the service instance to be 'dashDB-hq'

###Play Framework
Install Scala and Play via the [typesafe Activator](https://www.playframework.com/documentation/2.3.x/Installing)


####Connecting to dashDB using Scala Anorm data access layer 

Database connection information for the Anorm data access layer is specified via the file conf/app.conf  

```
db.default.driver=com.ibm.db2.jcc.DB2Driver

 
 #these come from spring-cloud 
# note : 'dashDB-hq' should be replaced by your actual dashDB service
# instance name 
db.default.url=${?cloud.services.dashDB-hq.connection.jdbcurl}
```

note that the 'dashDB-hq' above in ${?cloud.services.dashDB-hq.connection.jdbcurl} should be replaced by the actual name of your dashDB service instance bound to your app.

####Querying dashDB using Scala Anorm 

Take a look at the app/models/Note.scala and app/controllers/Application.scala which uses the Anorm data access layer to query dashDB

Note.scala
```    
  // Query first 10 rows from SYSCAT.TABLES
  def findTopTen(): List[Table] = {
    DB.withConnection { implicit connection =>
       SQL("SELECT TABNAME, TABSCHEMA from SYSCAT.TABLES FETCH FIRST 10 ROWS ONLY")
       .as(table *)
  }
    
  /**
   * Parse a Project from a ResultSet
   */
  val table = {
    get[String]("tabname") ~
    get[String]("tabschema") map {
      case tabname~tabschema => Table(tabname,tabschema)
    }
  }
```    
 
 Application.scala
 ```  
 def index = Action {
	  val tables = 
  		for (table <- Table.findTopTen) 
			yield Json.obj(
				"name" -> table.tabname,
				"schema" -> table.tabschema)
	   Ok(Json.arr(tables))
  }
```  

 
####Generate Application Jar file 
Use the 'activator dist' command to produce a Jar file out of your application  

```  
$ activator dist
[info] Loading project definition from ./project
[info] Set current project to dashDBScala-Play (in build file:.)
[info] Packaging ./target/scala-2.11/dashdbscala-play_2.11-1.0-SNAPSHOT-sources.jar ...
[info] Done packaging.
[info] Wrote ./target/scala-2.11/dashdbscala-play_2.11-1.0-SNAPSHOT.pom
[info] Packaging ./target/scala-2.11/dashdbscala-play_2.11-1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] 
[info] Your package is ready in ./target/universal/dashdbscala-play-1.0-SNAPSHOT.zip
[info] 
[success] Total time: 3 s, completed 30 Jun, 2015 2:39:04 PM

```  


###Uploading your application
Use 'cf push' command to push your application to Bluemix. 

```
cf push 
```

note : A manifest.yml is provided that 

 a - pushes the target/universal/dashdbscala-play-1.0-SNAPSHOT.zip to Bluemix  
 b - mandates that you have a dashDB service instance named 'dashDB-hq' that it binds to this app.    


###Related Links

- [IBM DB2 v10.5 Knowledge Center](https://www-01.ibm.com/support/knowledgecenter/SSEPGG_10.5.0/com.ibm.db2.luw.kc.doc/welcome.html)
- [IBM DB2 developerWorks](http://www.ibm.com/developerworks/data/products/db2/")

