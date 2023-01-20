CREATE TABLE clock_in (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    business_id VARCHAR(20) NOT NULL,
    employee_id VARCHAR(20) NOT NULL,
    service_id VARCHAR(20) NOT NULL
);
CREATE INDEX index_business_id_service_id ON clock_in (business_id, service_id);

CREATE TYPE record_type AS ENUM ('IN', 'OUT');
CREATE TYPE record_action AS ENUM ('WORK', 'REST');

CREATE TABLE clock_in_record (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    clock_in_id BIGINT NOT NULL,
    date BIGINT NOT NULL,
    type record_type NOT NULL,
    action record_action NOT NULL,

    CONSTRAINT clock_in_record_fk_clock_in FOREIGN KEY (clock_in_id) REFERENCES clock_in(id) ON DELETE CASCADE
);

CREATE TABLE clock_in_alert (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    clock_in_id BIGINT NOT NULL,
    alert_id BIGINT NOT NULL,

    CONSTRAINT clock_in_alert_fk_clock_in FOREIGN KEY (clock_in_id) REFERENCES clock_in(id) ON DELETE CASCADE,
    CONSTRAINT clock_in_alert_fk_alert FOREIGN KEY (alert_id) REFERENCES alert(id) ON DELETE CASCADE
);