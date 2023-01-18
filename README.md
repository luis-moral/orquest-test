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

### Database
  
- I will use an H2 database to simplify the tests
- I will use Spring Data JDBC instead of JPA/Hibernate
- All fields from the JSON example files are strings, so I will consider:
  - **businessId**: String (VARCHAR10)
  - **date**: Long epoch timestamps in milliseconds (BIGINT)
  - **employeeId**: String (VARCHAR20)
  - **recordType**: Enum (ENUM)
  - **serviceId**: String (VARCHAR20)
  - **type**: Enum (ENUM)

### Libraries

- I have omitted using Lombok and used IntelliJ code generation tools when needed