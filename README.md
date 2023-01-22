# Orquest Test

- [Requirements](doc/ORQUEST-Prueba de Código.pdf)
- [Example File 1](doc/fichero_1.json), [Example File 2](doc/fichero_2.json)

---

### Compile Code

```
./gradlew compileJava
```

### Run Tests

```
./gradlew clean test
```

### Run Application

```
./gradlew bootRun
```

### Generate Runnable Jar

```
./gradlew clean bootJar
```
---

## Notes

### Assumptions

- No security verification needed
- An employee cannot clock in on different services on the same single day
- The client cannot send a new POST request with data to import till the previous one has been successfully completed, so if there are any corrections we can be sure of the order
- We could offer a PUT endpoint for updates/corrections and the previous limitation move there
- To simplify if a clock in does not have matched records, the maximum hours worked alert will not trigger
- Get clock ins by employee returns the week of year corresponding to each group and the alerts are included with each record, the client should display this data as it likes 

### Database
  
- I will use an H2 database to simplify the test  
- I will use Spring Data JDBC instead of JPA/Hibernate
- All fields from the JSON example files are strings, so I will consider:
  - **businessId**: String (VARCHAR10)
  - **date**: Long epoch timestamps in milliseconds (BIGINT)
  - **employeeId**: String (VARCHAR20)
  - **recordType**: Enum (ENUM)
  - **serviceId**: String (VARCHAR20)
  - **type**: Enum (ENUM)

### Misc

- I have limited reactor usage (Mono/Flux) to the service and handlers
- I have only used Lombok to override methods so no IDE plugins are needed to open the project
- I could have used MapStruct for most mapper but didn't want to add more libraries.
- I used Spring Expression Language (SpEL) for the alert's logic, so they could be configurable by the client in runtime. Another solution would be to have "types" of alerts predefined and the client would select one with some parameters. I didn't have more context on alerts and wanted to play a bit more with SpEL, so I went that way. Alert expression tests can be found at [AlertShould](src/test/java/orquest/domain/alert/AlertShould.java)
- @MockBean was not working in one of the tests, so I used TestContext(@TestConfiguration) to override the bean