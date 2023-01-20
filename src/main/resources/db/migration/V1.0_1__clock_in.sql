CREATE TABLE clock_in (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    business_id VARCHAR(20) NOT NULL,
    employee_id VARCHAR(20) NOT NULL,
    service_id VARCHAR(20) NOT NULL
);
CREATE INDEX index_business_id_service_id ON clock_in (business_id, service_id);