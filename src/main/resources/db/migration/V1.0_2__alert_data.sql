INSERT INTO alert (business_id, expression, message) VALUES ('businessId1', '#clockIn.timeWorked() > T(java.util.concurrent.TimeUnit).HOURS.toMillis(10)', 'Missing clock ins');
INSERT INTO alert (business_id, expression, message) VALUES ('businessId1', '#clockIn.timeWorked() > T(java.util.concurrent.TimeUnit).HOURS.toMillis(10)', 'Maximum work hours exceeded');
INSERT INTO alert (business_id, expression, message) VALUES ('businessId1', '(#clockIn.dayOfWeek().isPresent()) and ((#clockIn.dayOfWeek().get() >= T(java.time.DayOfWeek).MONDAY && #clockIn.dayOfWeek().get() <= T(java.time.DayOfWeek).THURSDAY && #clockIn.firstRecordHourOfDay() >= 8) or (#clockIn.dayOfWeek().get() == T(java.time.DayOfWeek).FRIDAY &&#clockIn.firstRecordHourOfDay() >= 7))', 'Invalid first clock in time');