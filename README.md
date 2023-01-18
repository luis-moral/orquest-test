# Orquest Test

- [Requirements](doc/ORQUEST-Prueba de Código.pdf)
- [Example File 1](doc/fichero_1.json), [Example File 2](doc/fichero_2.json)

## Compile Code

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

## Notes

- To simplify the test I will use an H2 database
- All fields from the JSON example files are strings, so I will consider:
    - **businessId**: String (VARCHAR10)
    - **date**: Long epoch timestamps in milliseconds (BIGINT)
    - **employeeId**: String (VARCHAR20)
    - **recordType**: Enum (ENUM)
    - **serviceId**: String (VARCHAR20)
    - **type**: Enum (ENUM)
- Assumptions:
    - An employee cannot clock in on different services on the same single day
    - The client cannot send a new POST request with data till the previous one has been successfully completed, so if there are any corrections we can be sure of the order
