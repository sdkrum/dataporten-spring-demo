# dataporten-spring-demo
This small project shows the use of dataporten as an authentication and authorization mechanism in front of a java web application.

To make this small snippet work:
* register your application at https://dashboard.dataporten.no
* redirect uri uses http://localhost:8080/dataporten-demo/loginDataporten
* set the client-id and client secret in the src/main/resources/application.properties (or as environment variables, if both are set, the environment wins)
* build your application with #mvn clean install right in the directory where you found this readme
* deploy to a localhost tomcat (should work with 7 and 8, did not try out others)
* access http://localhost:8080/dataporten-demo/

There is one protected and one open page.

If you want to investigate how it works, the file having all the configuration is src/main/resources/application-context-security. Most of
the java classes are strictly speaking not necessary, but they are still there for conveniance to make a quickstart and adaptions easier. Of
interest is the DataportenRemoteTokenService, as that one handles the way, a local user is created from the informations from dataporten.
