-- One
INSERT INTO clock_in (id, business_id, employee_id, service_id, date) VALUES ('2d6d2267-9c2c-437b-a763-96e986bd8d84', 'businessId1', 'employeeId1', 'serviceId1', 0);
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES ('2d6d2267-9c2c-437b-a763-96e986bd8d84', 10500, 'IN', 'WORK');
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES ('2d6d2267-9c2c-437b-a763-96e986bd8d84', 15500, 'OUT', 'WORK');
INSERT INTO clock_in_alert (clock_in_id, alert_id) VALUES ('2d6d2267-9c2c-437b-a763-96e986bd8d84', '2baa2295-27ee-4d60-9305-7e2f7e159988');
INSERT INTO clock_in_alert (clock_in_id, alert_id) VALUES ('2d6d2267-9c2c-437b-a763-96e986bd8d84', '35ac5f30-f5c4-475c-b7e8-194ae6396c25');

-- Two
INSERT INTO clock_in (id, business_id, employee_id, service_id, date) VALUES ('af0a0ef1-9974-4c64-968f-347a5568d5ca', 'businessId1', 'employeeId2', 'serviceId1', NULL);

-- Three
INSERT INTO clock_in (id, business_id, employee_id, service_id, date) VALUES ('52cd4d03-2875-4530-96bf-1c5620fdeed2', 'businessId1', 'employeeId1', 'serviceId2', 186400000);
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES ('52cd4d03-2875-4530-96bf-1c5620fdeed2', 186400500, 'IN', 'REST');
INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES ('52cd4d03-2875-4530-96bf-1c5620fdeed2', 186401500, 'OUT', 'REST');

-- Four
INSERT INTO clock_in (id, business_id, employee_id, service_id, date) VALUES ('eec008e8-a5b5-4458-a391-66f21d9aa3cc', 'businessId2', 'employeeId1', 'serviceId1', NULL);
INSERT INTO clock_in_alert (clock_in_id, alert_id) VALUES ('eec008e8-a5b5-4458-a391-66f21d9aa3cc', '35ac5f30-f5c4-475c-b7e8-194ae6396c25');

-- Five
INSERT INTO clock_in (id, business_id, employee_id, service_id, date) VALUES ('54d0ef14-6264-4216-9374-8762adb691f0', 'businessId3', 'employeeId3', 'serviceId1', NULL);