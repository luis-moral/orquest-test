package orquest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import orquest.domain.clockin.ClockInActionType;
import orquest.domain.clockin.ClockInRecord;
import orquest.domain.clockin.ClockInRecordType;
import orquest.domain.clockin.ClockInRepository;
import orquest.test.TestUtils;

import java.time.ZonedDateTime;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { Application.class })
public class ImportClockInsFeature {

	private final static String BUSINESS_ID = "1";
	private final static String EMPLOYEE_ID = "222222222";
	private final static String SERVICE_ID = "ALBASANZ";

	@Value("${endpoint.v1.clockin.path.base}")
	private String clockInEndpoint;

	@Autowired
	private WebTestClient webClient;
	@Autowired
	private ClockInRepository clockInRepository;

	@Test public void
	import_multiple_employee_clock_in_intervals() {
		webClient
			.post()
				.uri(clockInEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(TestUtils.readFile("feature/import_clockin/example_input_1.json"))
			.exchange()
				.expectStatus()
					.isCreated();

		Assertions
			.assertThat(clockInRepository.getByEmployee(EMPLOYEE_ID))
			.containsExactlyInAnyOrder(
				record(BUSINESS_ID, "2018-01-01T08:00:00.000Z", EMPLOYEE_ID, ClockInActionType.In, SERVICE_ID, ClockInRecordType.Work),
				record(BUSINESS_ID, "2018-01-01T13:30:00.000Z", EMPLOYEE_ID, ClockInActionType.Out, SERVICE_ID, ClockInRecordType.Work),
				record(BUSINESS_ID, "2018-01-01T10:45:00.000Z", EMPLOYEE_ID, ClockInActionType.Out, SERVICE_ID, ClockInRecordType.Rest),
				record(BUSINESS_ID, "2018-01-01T15:00:00.000Z", EMPLOYEE_ID, ClockInActionType.In, SERVICE_ID, ClockInRecordType.Work),
				record(BUSINESS_ID, "2018-01-01T18:00:00.000Z", EMPLOYEE_ID, ClockInActionType.Out, SERVICE_ID, ClockInRecordType.Work),
				record(BUSINESS_ID, "2018-01-02T08:00:00.000Z", EMPLOYEE_ID, ClockInActionType.In, SERVICE_ID, ClockInRecordType.Work),
				record(BUSINESS_ID, "2018-01-02T13:30:00.000Z", EMPLOYEE_ID, ClockInActionType.Out, SERVICE_ID, ClockInRecordType.Work),
				record(BUSINESS_ID, "2018-01-02T10:30:00.000Z", EMPLOYEE_ID, ClockInActionType.In, SERVICE_ID, ClockInRecordType.Rest),
				record(BUSINESS_ID, "2018-01-02T10:45:00.000Z", EMPLOYEE_ID, ClockInActionType.Out, SERVICE_ID, ClockInRecordType.Rest),
				record(BUSINESS_ID, "2018-01-02T15:00:00.000Z", EMPLOYEE_ID, ClockInActionType.In, SERVICE_ID, ClockInRecordType.Work),
				record(BUSINESS_ID, "2018-01-02T18:00:00.000Z", EMPLOYEE_ID, ClockInActionType.Out, SERVICE_ID, ClockInRecordType.Work)
			);
	}

	private ClockInRecord record(
		String businessId,
		String date,
		String employeeId,
		ClockInActionType action,
		String serviceId,
		ClockInRecordType type
	) {
		return
			new ClockInRecord(
				businessId,
				ZonedDateTime.parse(date),
				employeeId,
				action,
				serviceId,
				type
			);
	}
}
