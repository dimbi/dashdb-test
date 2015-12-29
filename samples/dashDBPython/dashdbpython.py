from flask import Flask
import os
import ibm_db
app = Flask(__name__)

if 'VCAP_APP_PORT' in os.environ:
    appPort = os.environ['VCAP_APP_PORT']
else:
    appPort = 9000

if 'VCAP_APP_HOST' in os.environ:
    appHost = os.environ['VCAP_APP_HOST']
else:
    appHost = '0.0.0.0'
	
if 'VCAP_SERVICES' in os.environ:
    hasVcap = True
    import json
    vcap_services = json.loads(os.environ['VCAP_SERVICES'])
    if 'dashDB' in vcap_services:
        hasdashDB = True
        service = vcap_services['dashDB'][0]
        credentials = service["credentials"]
        url = 'DATABASE=%s;uid=%s;pwd=%s;hostname=%s;port=%s;' % ( credentials["db"],credentials["username"],credentials["password"],credentials["host"],credentials["port"])
    else:
        hasdashDB = False
  
else:
    hasVcap = False
    url = 'DATABASE=%s;uid=%s;pwd=%s;hostname=%s;port=%s;' % ( "SAMPLE","db2admin","db2admin","localhost","50000")

@app.route("/")
def hello():
    if hasVcap == False:
        return "No VCAP_SERVICES variable available."
    else:
        if hasdashDB == False:
            return "No dashDB Service instance bound. Please bind a dashDB Service instance to the app"

    connection = ibm_db.connect(url, '', '')
    statement = ibm_db.prepare(connection, 'SELECT TABNAME, TABSCHEMA from SYSCAT.TABLES FETCH FIRST 10 ROWS ONLY')
    ibm_db.execute(statement)
    out = "<html><h2>10 rows from SYSCAT.TABLES table</h2><table border=\"1\"><tr><td>Table Name</td><td>Table Schema</td>" 
    data = ibm_db.fetch_tuple(statement)
    while (data):
        out = out + "<tr><td>"+data[0]+"</td><td>"+data[1]+"</td></tr>"
        data = ibm_db.fetch_tuple(statement)

    ibm_db.free_stmt(statement)
    ibm_db.close(connection)
    out = out + "</table></html>"
    print out
    return out
	
if __name__ == '__main__':
    app.run(host=appHost,port=appPort, debug=False, threaded=True)


