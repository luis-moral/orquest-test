CREATE TABLE alert (
    id UUID PRIMARY KEY NOT NULL,
    business_id VARCHAR(20) NOT NULL,
    expression TEXT NOT NULL,
    message VARCHAR(250) NOT NULL
);