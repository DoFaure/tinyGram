# TinyGram

**TinyGram is a part of the module "Développement d'applications sur le CLOUD" which is assignment of the course at University of Nantes taken by [Pascal Molli](https://sites.google.com/view/pascal-molli "Pascal Molli's website")**

This application takes few features of Instagram using Google App Engine Java 8. 
Basically you can : 
* Follow someone
* Post a picture
* List new posts
* Like a post

Every data are stored in Google Datastore manage using Endpoints	(API)


## Import and run in eclipse
* install the code in your home:
```
 cd ~
 git clone https://github.com/DoFaure/tinyGram.git
 cd tinyGram
 mvn install
```
* Change "module-application-cloud" with your google project ID in pom.xml
* Change "module-application-cloud" with your google project ID in src/main/webapp/WEB-INF/appengine-web.xml


## Run in eclipse

* start an eclipse with gcloud plugin
* import the maven project in eclipse
 * File/import/maven/existing maven project
 * browse to ~/tinyGram
 * select pom.xml
 * Finish and wait
 * Ready to deploy and run...
 ```
 gcloud app create error...
 ```
 Go to google cloud shell console (icon near your head in google console)
 ```
 gcloud app create
 ```


## Install and Run 
* (gcloud SDK must be installed first. see https://cloud.google.com/sdk/install)
* git clone https://github.com/DoFaure/tinyGram.git
* cd tinyGram
* running local (http://localhost:8080):
```
mvn appengine:run
```
* Deploying at Google (need gcloud configuration, see error message -> tell you what to do... 
)
```
mvn appengine:deploy
gcloud app browse
```

# Access REST API
**If you want to see the current API of the deployed application see [here](https://apis-explorer.appspot.com/apis-explorer/?base=https%3A%2F%2Fmodule-application-cloud.appspot.com%2F_ah%2Fapi&root=https%3A%2F%2Fmodule-application-cloud.appspot.com%2F_ah%2Fapi#p/tinyGramApi/v1/).**

You can access your API by this URL: 

```
https://<yourapp>.appstpot.com/_ah/api/explorer
```
* New version of endpoints (see https://cloud.google.com/endpoints/docs/frameworks/java/adding-api-management?hl=fr):
```
mvn clean package
mvn endpoints-framework:openApiDocs
gcloud endpoints services deploy target/openapi-docs/openapi.json 
mvn appengine:deploy
```
