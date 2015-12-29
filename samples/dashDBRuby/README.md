
#dashDB and Ruby Sinatra

For issues that you encounter with this service, go to [**Get help**](https://www.ibmdw.net/bluemix/get-help/) in the Bluemix development community.

You can bind your Ruby application to the dashDB service and work with your data. To enable this connection, the Ruby runtime needs the ibm_db gem for connecting to the dashDB database. This topic explains how to get your code running using this method. 

###Required components

The following components are required to connect dashDB to a Ruby Sinatra application. The are all described in further detail in this topic.

- Gemfile
- Gemfile.lock
- config.ru
- Ruby program

####Gemfile and Gemfile.lock

The gemfile contains a list of all the Ruby gems you need to include in your project. It is used by bundler to install, update, remove and manage the gems you need. Add the following ibm_db dependency to your Ruby Gemfile:

```
   source 'https://rubygems.org'
   ruby '1.9.3'
   gem 'sinatra', '>= 0'
   gem 'ibm_db'
```
Make sure bundler is installed with:

`gem install bundler`

Then run `bundle install` to generate your Gemfile.lock file.

####config.ru

The config.ru file determines the starting point of your application:

```
  require './dashdbsample'
  run Sinatra::Application
```
###Connecting to dashDB from Ruby code

In the Ruby code, parse the VCAP_Services environment variable to retrieve the database connection information as shown in the following example.

For more information on the structure of the VCAP_SERVICES environment variable, see Getting Started with the dashDB Service

```
jsondb_db = JSON.parse(ENV['VCAP_SERVICES'])["dashDB"]
credentials = jsondb_db[0]["credentials"]
host = credentials["host"]
username = credentials["username"]
password = credentials["password"]
database = credentials["db"]
port = credentials["port"]
dsn = "DRIVER={IBM DB2 ODBC DRIVER};DATABASE="+database+";HOSTNAME="+host+";PORT="+port.to_s()+";PROTOCOL=TCPIP;UID="+username+";PWD="+password+";"
``` 
Now you can use the dsn datasource name to connect to the database and create a table as shown below. The variable total in the code sample below represents the web output.

```
  if conn = IBM_DB.connect(dsn, '', '')
    sql = "SELECT FIRST_NAME, LAST_NAME from GOSALESHR.employee FETCH FIRST 10 ROWS ONLY"
    if stmt = IBM_DB.exec(conn, sql)
       total = total + sql + "<BR><BR>\n"
    else
      out = "Statement execution failed: #{IBM_DB.stmt_errormsg}"
      total = total + out + "<BR>\n"
    end
  end
```
Please download and examine the attached sample for the rest of the code. If you use this sample, please edit the manifest.yml and specify a unique 'host' value.

###Uploading your application

Upload your application to Bluemix using the 'cf push' command 

  `cf push <app-name>`



###Related Links

- [IBM DB2 v10.5 Information Center](https://www-01.ibm.com/support/knowledgecenter/SSEPGG_10.5.0/com.ibm.db2.luw.kc.doc/welcome.html")
- [IBM DB2 developerWorks](http://www.ibm.com/developerworks/data/products/db2/)
- [IBM developerWorks: DB2 and Ruby on Rails](http://www.ibm.com/developerworks/data/library/techarticle/dm-0705chun/)

