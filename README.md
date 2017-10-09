# Web Performance Service
[Doc at go/webperformance-service](https://goto.google.com/webperformance-service) 

### How to Run

##### On Mac you may need to export the JAVA_HOME, for example:
`export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home`


#### Environment Variables for local testing
`export ENDPOINTS_SERVICE_NAME=web-performance-dot-pwa-directory.appspot.com`

`export WEBPAGETEST_KEY=A.cb9116e6706a6d1dfe45630237bd301d`

`export PAGESPEED_KEY=AIzaSyBjOA6ALLOXIjtZDPyYRpty_4qDlNKeTHE`

`export LIGHTHOUSE_URL=https://lighthouse-machine-dot-pwa-directory.appspot.com`

#### Environment Variables for AppEngine are at
`/page-speed-service/src/main/webapp/WEB-INF/appengine-web.xml`


### Run server locally at http://localhost:8080 
`mvn install appengine:devserver`

Set the following env variables locally:
`export ENDPOINTS_SERVICE_NAME="web-performance-dot-pwa-directory.appspot.com"`
`export WEBPAGETEST_KEY="4846f0ebf9af4fa2a381f9885cae6a42"`
`export PAGESPEED_KEY="AIzaSyBjOA6ALLOXIjtZDPyYRpty_4qDlNKeTHE"`
`export LIGHTHOUSE_URL="https://lighthouse-machine-dot-pwa-directory.appspot.com"`

###  Deploy to AppEngine
`mvn install appengine:update`

###  Update openapi.yaml to AppEngine
`gcloud service-management deploy openapi.yaml`


### REST entry points
##### URLs
`/webpageurl`

`/webpageurl/{id}`

`/webpageurl/url/{url}`

##### Page Speed Insights Reports
`/pagespeedreport` List all PageSpeedInsights reports

`/pagespeedreport/{webPageUrlId}` PageSpeedInsights reports for one WebPage

`/pagespeedreport/{webPageUrlId}?graph=true` PageSpeedInsights reports for one WebPage in GoogleCharts Data format

`/pagespeedreport/url/{url}` PageSpeedInsights reports for one WebPage URL

##### Lighthouse Reports
`/lighthousereport` List all Lighthouse reports

`/lighthousereport/{webPageUrlId}` Lighthouse reports for one WebPage

`/lighthousereport/{webPageUrlId}?graph=true` Lighthouse reports for one WebPage in GoogleCharts Data format

`/lighthousereport/url/{url}` Lighthouse reports for one WebPage URL

##### Web Page Test Reports
`/webpagetestreport` List all WebPageTest reports

`/webpagetestreport/{webPageUrlId}` WebPageTest reports for one WebPage

`/webpagetestreport/{webPageUrlId}?graph=true` WebPageTest reports for one WebPage in GoogleCharts Data format

`/webpagetestreport/url/{url}` WebPageTest reports for one WebPage URL

##### Event Annotations
`/eventannotation`

`/eventannotation/{webPageUrlId}`

### Cron entry point (it can only be called in production with X-Appengine-Cron: true)
`/taskcreator`

### Cloud Endpoints Framework [more info](https://cloud.google.com/endpoints/docs/frameworks/java/about-cloud-endpoints-frameworks)
`POST/PUT/DELETE` operations require `?key=API_KEY_FROM_DEV_CONSOLE`

Keys can be generated at: https://pantheon.corp.google.com/apis/credentials?project=pwa-directory
# HomeMonitor
