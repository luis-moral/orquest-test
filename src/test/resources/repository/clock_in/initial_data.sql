-- alert
INSERT INTO alert (expression, message) VALUES ('expression1', 'message1');
INSERT INTO alert (expression, message) VALUES ('expression2', 'message2');

-- clock_in
INSERT INTO clock_in (business_id, employee_id, service_id, date) VALUES ('businessId1', 'employeeId1', 'serviceId1', 0);
INSERT INTO clock_in (business_id, employee_id, service_id, date) VALUES ('businessId1', 'employeeId2', 'serviceId1', NULL);
INSERT INTO clock_in (business_id, employee_id, service_id, date) VALUES ('businessId1', 'employeeId1', 'serviceId2', 186400000);
INSERT INTO clock_in (business_id, employee_id, service_id, date) VALUES ('businessId2', 'employeeId1', 'serviceId1', NULL);
INSERT INTO clock_in (business_id, employee_id, service_id, date) VALUES ('businessId3', 'employeeId3', 'serviceId1', NULL);

-- clock_in_record
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES (1, 10500, 'IN', 'WORK');
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES (1, 15500, 'OUT', 'WORK');
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES (3, 186400500, 'IN', 'REST');
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES (3, 186401500, 'OUT', 'REST');

-- clock_in_alert
INSERT INTO clock_in_alert (clock_in_id, alert_id) VALUES (1, 1);
INSERT INTO clock_in_alert (clock_in_id, alert_id) VALUES (1, 2);
INSERT INTO clock_in_alert (clock_in_id, alert_id) VALUES (4, 2);