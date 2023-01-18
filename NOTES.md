## Notes

- To simplify the test I will use an H2 database
- All fields from the JSON example files are strings, so I will consider:
  - **businessId**: String (VARCHAR10)
  - **date**: Long epoch timestamps in milliseconds (BIGINT)
  - **employeeId**: String (VARCHAR20)
  - **recordType**: Enum (ENUM)
  - **serviceId**: String (VARCHAR20)
  - **type**: Enum (ENUM)
- I will consider than an employee cannot clock in on different services on the same single day
