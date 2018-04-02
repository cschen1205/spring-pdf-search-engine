# spring-pdf-search-engine

Search engine built with elastic search and Spring

# Database Configuration

To use this project create a database named whizhome in your mysql database (make sure it is running at localhost:3306)

```sql
CREATE DATABASE pdfdb CHARACTER SET utf8 COLLATE utf8_unicode_ci;
```

Note that the default username and password for the mysql is configured to 

* username: root
* password: chen0469

If your mysql or mariadb does not use these configuration, please change the settings in src/resources/config/application-default.properties
 
# Setup Elastic Search

To use this project, you also need to start your local elastic search server runnin at http://localhost:9200

To save time, you can also run the standalone elastic search server available in this project. just run the following 
command:

```bash
java -jar elasticsearch-standalone.jar
```

# Run your pdf search engine backend server

Run the make.ps1 (if on Windows) or make.sh (if on Unix) to build the pdf-search-engine.jar 

Run the following command to start the pdf-search-engine.jar for the backend server:

```bash
java -jar pdf-search-engine.jar
```

The application server should be now running at http://localhost:8081

To check the available api, one can visit http://localhost:8081/swagger-ui.html