# Hierarchy

Hierarchy is a Spring boot application that provides a REST API JSON endpoint that transforms an {\<employee_name>: \<supervisor_name>} 
payload in a hierarchical response.

## Using the gradle wrapper
All gradle commands in this repository can be ran using the gradle wrapper present in this repository.
Eg:
```
Windows:
gradle.bat clean

bash:
./gradlew clean
```

## Running the application
### Using the provided .jar file
To run the .jar file that comes packed with this repository, run the following command:
```
java -jar build/libs/hierarchy-0.0.1-SNAPSHOT.jar.
```

### Assembling a new .jar file
```
./gradlew bootJar
```

### From the source files
```
./gradlew bootRun
```

### Running unit and integration tests
The repository layer integration tests will be ran against the H2 database
```
./gradlew test
```

## Usage
After running the application the REST API will then be available at: [http://localhost:8080/](http://localhost:8080/).

An H2 database in file mode will be created under ~/data/hierarchy .
The database will get initialized with the scripts in: src/main/resources/data.sql.
These scripts will add a new user that can be used to access the endpoints using Basic Auth.
Since the password is encoded in the data.sql here is the username/password needed to access the API:
```
username: personia
password: personia
```    

### Curl requests
The application has 2 endpoints:
* POST http://localhost:8080/employee/hierarchy
* GET http://localhost:8080/employee?name=Nick


To test the running app, you can run the following requests:
```

curl -u personia:password -i -XPOST -H "Content-type: application/json" -d '{
  "Pete": "Nick",
  "Barbara": "Nick",
  "Nick": "Sophie",
  "Sophie": "Jonas"
}' 'http://localhost:8080/employee/hierarchy'

```
```
curl -u personia:password -i -XGET -H "Content-type: application/json" 'http://localhost:8080/employee?name=Nick'
```

## Built With / Reference Documentation
For further reference, please consider the following sections:

* [Gradle](https://gradle.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
    * [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)
    * [Spring Boot Starter Actuators](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator)
    * [Spring Boot Starter Data JPA](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)
    * [Spring Boot Starter Test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security)
    * [Spring Boot Starter Test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test) (JUNIT 5)
* [H2](https://mvnrepository.com/artifact/com.h2database/h2)

## Authors
* **[Lucian Gabriel Ilie](mailto:luciangabrielilie@gmail.com)**
