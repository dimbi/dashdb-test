#dashDB and WebSphere Liberty Profile

For issues that you encounter with this service, go to [**Get help**](https://www.ibmdw.net/bluemix/get-help/) in the Bluemix development community.

###Using WebSphere Liberty Profile
You can bind dashDB services with your Liberty applications to be deployed into Bluemix as described in the Binding a Relational database to an application of the Bluemix documentation.

###Server.xml

The contents of the deployed server.xml file can be found in the Bluemix UI under the Files and Logs section of a given Java Web Application. Navigate to:

    app/.liberty/usr/servers/defaultserver/server.xml

Here is the relevant snippet of that file showing the database connection information:
```
<dataSource id='db2-SampledashDB' 
 jdbcDriverRef='db2-driver' 
 jndiName='jdbc/SampledashDB' 
 statementCacheSize='30' 
 transactional='true'>
 <properties.db2.jcc 
    databaseName='${cloud.services.SampledashDB.connection.db}' 
    id='db2-SampledashDB-props' 
    password='${cloud.services.SampledashDB.connection.password}' 
    portNumber='${cloud.services.SampledashDB.connection.port}' 
    serverName='${cloud.services.SampledashDB.connection.host}' 
    user='${cloud.services.SampledashDB.connection.username}'
  />
</dataSource>

<jdbcDriver id='db2-driver' libraryRef='db2-library'/>
<library id='db2-library'>
  <fileset 
     dir='${server.config.dir}/lib' 
     id='db2-fileset' 
     includes='db2jcc4.jar db2jcc_license_cu.jar'
   />
 </library>
```
###Java Resource Injection

Now you can use this connection information in Liberty as you would with any other Java application, e.g. by using Java resource injection.

> **Note**: You have to use the name of the dashDB service, not the database name, to do the lookup:

```
  @Resource(lookup="jdbc/SampledashDB")
  DataSource ds;
```
You can now use this datasource

   `con = ds.getConnection();`

Related Links

- [Binding a Relational database to Liberty applications](https://ace.ng.bluemix.net/docs/Liberty/Bind_RDB.html)
- [Java Web Applications on the IBM WebSphere Liberty Buildpack](https://ace.ng.bluemix.net/#/store/appTemplateGuid=javaHelloWorld)

