package orquest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;
import orquest.infrastructure.util.generator.IdGenerator;
import orquest.test.TestContext;
import orquest.test.TestUtils;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ActiveProfiles(profiles = {"test", "import_clock_ins-feature"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { Application.class, TestContext.class})
public class ImportClockInsFeature {

	private final static UUID ALERT_ONE_ID = UUID.fromString("2baa2295-27ee-4d60-9305-7e2f7e159988");
	private final static UUID ALERT_TWO_ID = UUID.fromString("35ac5f30-f5c4-475c-b7e8-194ae6396c25");
	private final static UUID ALERT_THREE_ID = UUID.fromString("7bee61e8-3c62-406c-a04a-d211b50b438e");
	private final static String BUSINESS_ID = "1";
	private final static String EMPLOYEE_ID = "222222222";
	private final static String SERVICE_ID = "ALBASANZ";

	@Value("${endpoint.v1.clockin.path.base}")
	private String clockInEndpoint;

	@Autowired
	private WebTestClient webClient;
	@Autowired
	private ClockInRepository clockInRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private IdGenerator idGenerator;

	@Test public void
	import_multiple_employee_clock_in_intervals() {
		UUID clockInIdOne = UUID.randomUUID();
		UUID clockInIdTwo = UUID.randomUUID();

		Mockito
			.when(idGenerator.generateId())
			.thenReturn(clockInIdOne, clockInIdTwo);

		webClient
			.post()
				.uri(clockInEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(TestUtils.readFile("feature/import_clockins/valid_input_1.json"))
			.exchange()
				.expectStatus()
					.isCreated();

		List<ClockIn> results = clockInRepository.find();
		ClockIn resultOne = results.stream().filter(value -> value.id().equals(clockInIdOne)).findFirst().get();
		ClockIn resultTwo = results.stream().filter(value -> value.id().equals(clockInIdTwo)).findFirst().get();

		Assertions
			.assertThat(resultOne)
			.isEqualTo(
				clockIn(
					clockInIdOne,
					BUSINESS_ID,
					EMPLOYEE_ID,
					SERVICE_ID,
					List.of(
						clockInRecord(clockInIdOne, "2018-01-01T08:00:00.000Z", TimeRecordType.IN, TimeRecordAction.WORK),
						clockInRecord(clockInIdOne, "2018-01-01T13:30:00.000Z", TimeRecordType.OUT, TimeRecordAction.WORK),
						clockInRecord(clockInIdOne, "2018-01-01T10:45:00.000Z", TimeRecordType.OUT, TimeRecordAction.REST),
						clockInRecord(clockInIdOne, "2018-01-01T15:00:00.000Z", TimeRecordType.IN, TimeRecordAction.WORK),
						clockInRecord(clockInIdOne, "2018-01-01T18:00:00.000Z", TimeRecordType.OUT, TimeRecordAction.WORK)
					),
					List.of(
						new ClockInAlert(clockInIdOne, ALERT_ONE_ID)
					)
				)
			);

		Assertions
			.assertThat(resultTwo)
			.isEqualTo(
				clockIn(
					clockInIdTwo,
					BUSINESS_ID,
					EMPLOYEE_ID,
					SERVICE_ID,
					List.of(
						clockInRecord(clockInIdTwo, "2018-01-02T05:00:00.000Z", TimeRecordType.IN, TimeRecordAction.WORK),
						clockInRecord(clockInIdTwo, "2018-01-02T13:30:00.000Z", TimeRecordType.OUT, TimeRecordAction.WORK),
						clockInRecord(clockInIdTwo, "2018-01-02T10:30:00.000Z", TimeRecordType.IN, TimeRecordAction.REST),
						clockInRecord(clockInIdTwo, "2018-01-02T10:45:00.000Z", TimeRecordType.OUT, TimeRecordAction.REST),
						clockInRecord(clockInIdTwo, "2018-01-02T15:00:00.000Z", TimeRecordType.IN, TimeRecordAction.WORK),
						clockInRecord(clockInIdTwo, "2018-01-02T18:00:00.000Z", TimeRecordType.OUT, TimeRecordAction.WORK)
					),
					List.of(
						new ClockInAlert(clockInIdTwo, ALERT_TWO_ID),
						new ClockInAlert(clockInIdTwo, ALERT_THREE_ID)
					)
				)
			);
	}

	@SuppressWarnings("unchecked")
	@Test public void
	error_when_invalid_json() throws JsonProcessingException {
		List<Map<String, Object>> validInput = (List<Map<String, Object>>) objectMapper.readValue(TestUtils.readFile("feature/import_clockins/valid_input_2.json"), List.class);
		HashMap<String, Object> validItem = new HashMap<>(validInput.get(0));

		assertError(
			List.of(clone(validItem, "businessId")),
			400,
			"Parameter [businessId] is mandatory"
		);
		assertError(
			List.of(clone(validItem, "date")),
			400,
			"Parameter [date] is mandatory"
		);
		assertError(
			List.of(clone(validItem, "employeeId")),
			400,
			"Parameter [employeeId] is mandatory"
		);
		assertError(
			List.of(clone(validItem, "recordType")),
			400,
			"Parameter [recordType] is mandatory"
		);
		assertError(
			List.of(clone(validItem, "serviceId")),
			400,
			"Parameter [serviceId] is mandatory"
		);
		assertError(
			List.of(clone(validItem, "type")),
			400,
			"Parameter [type] is mandatory"
		);
	}

	private Map<String, Object> clone(Map<String, Object> original, String excludedField) {
		return
			original
				.entrySet()
				.stream()
				.filter(entry -> !entry.getKey().equals(excludedField))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new));
	}

	private void assertError(List<Map<String, Object>> requestBody, int errorCode, String errorMessage) {
		webClient
			.post()
				.uri(clockInEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestBody)
			.exchange()
				.expectStatus()
					.isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody(HashMap.class)
					.consumeWith(response ->
						{
							Map responseBody = response.getResponseBody();

							Assertions
								.assertThat(responseBody.get("status"))
								.isEqualTo(errorCode);

							Assertions
								.assertThat(responseBody.get("error"))
								.isEqualTo(errorMessage);
						}
					);
	}

	private ClockIn clockIn(
		UUID id,
		String businessId,
		String employeeId,
		String serviceId,
		List<ClockInRecord> records,
		List<ClockInAlert> alerts
	) {
		return
			new ClockIn(
				id,
				businessId,
				employeeId,
				serviceId,
				records,
				alerts
			);
	}

	private ClockInRecord clockInRecord(
		UUID clockInId,
		String date,
		TimeRecordType type,
		TimeRecordAction action
	) {
		return
			new ClockInRecord(
				clockInId,
				ZonedDateTime.parse(date).toInstant().toEpochMilli(),
				type,
				action
			);
	}
}
