require 'rubygems'
if RUBY_PLATFORM =~ /mingw/
  require 'mswin32/ibm_db'
else
  require 'ibm_db'
end
require 'sinatra'
require 'json'

# @Author IBM
# @version 0.1

IS_CF=true

get '/' do

  if IS_CF
      jsondb_db = JSON.parse(ENV['VCAP_SERVICES'])["dashDB"]
	  halt 500, "No dashDB service bound. Please bind a dashDB Service instance" if jsondb_db.nil? 
	  credentials = jsondb_db[0]["credentials"]
      host = credentials["host"]
      username = credentials["username"]
      password = credentials["password"]
      database = credentials["db"]
      port = credentials["port"]
      dsn = "DRIVER={IBM DB2 ODBC DRIVER};DATABASE="+database+";HOSTNAME="+host+";PORT="+port.to_s()+";PROTOCOL=TCPIP;UID="+username+";PWD="+password+";"
    else
	  halt 500, "no vcap services"
    end

out = String.new
total = String.new

if conn = IBM_DB.connect(dsn, '', '')
  
  # run a sample select query on the catalog tables
  sql = "SELECT FIRST_NAME, LAST_NAME from GOSALESHR.employee FETCH FIRST 10 ROWS ONLY"
  
  begin
    if stmt = IBM_DB.exec(conn, sql)
      # iterate through the resultset
	  out = "10 rows from table GOSALESHR.employee <table border=\"1\"><tr><td>Firstname</td><td>LastName</td>"
	  total = total + out + "<BR>\n"
      while row = IBM_DB.fetch_assoc(stmt)
        out = "<tr><td>#{row['FIRST_NAME']}</td><td>#{row['LAST_NAME']}</td>"
        total = total + out + "<BR>"
      end
	  
	  out = "</table>"
	  total = total + out + "<BR>\n"
      # free the resources associated with the result set
      IBM_DB.free_result(stmt)
    else
      halt 500 , "Statement execution failed: #{IBM_DB.stmt_errormsg}"
    end
  ensure
    # Cleanup close the database connection
    
    IBM_DB.exec(conn, sql)
    
    IBM_DB.close(conn)
  end
else
  halt 500 , "Connection failed: #{IBM_DB.conn_errormsg}"
  
end
total
end

