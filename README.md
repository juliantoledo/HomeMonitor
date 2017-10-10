# Home Monitor

### How to Run

##### On Mac you may need to export the JAVA_HOME, for example:
`export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home`

#### Environment Variables for AppEngine are at
`/page-speed-service/src/main/webapp/WEB-INF/appengine-web.xml`

### Run server locally at http://localhost:8080 
`mvn install appengine:devserver`

###  Deploy to AppEngine
`mvn install appengine:update`

###  Update openapi.yaml to AppEngine
`gcloud service-management deploy openapi.yaml`
