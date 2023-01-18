package orquest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
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
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.test.TestUtils;
import reactor.test.StepVerifier;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	@Autowired
	private ObjectMapper objectMapper;

	@Test public void
	import_multiple_employee_clock_in_intervals() {
		webClient
			.post()
				.uri(clockInEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(TestUtils.readFile("feature/import_clockin/valid_input_1.json"))
			.exchange()
				.expectStatus()
					.isCreated();

		StepVerifier
			.create(clockInRepository.forEmployee(EMPLOYEE_ID).collectList())
			.assertNext(clockIns ->
				Assertions
					.assertThat(clockIns)
					.containsExactly(
						clockIn(
							1L,
							BUSINESS_ID,
							EMPLOYEE_ID,
							SERVICE_ID,
							List.of(
								clockInRecord(1L, 1L, "2018-01-01T08:00:00.000Z", ClockInRecordType.IN, ClockInRecordAction.WORK),
								clockInRecord(2L, 1L, "2018-01-01T13:30:00.000Z", ClockInRecordType.OUT, ClockInRecordAction.WORK),
								clockInRecord(3L, 1L, "2018-01-01T10:45:00.000Z", ClockInRecordType.OUT, ClockInRecordAction.REST),
								clockInRecord(4L, 1L, "2018-01-01T15:00:00.000Z", ClockInRecordType.IN, ClockInRecordAction.WORK),
								clockInRecord(5L, 1L, "2018-01-01T18:00:00.000Z", ClockInRecordType.OUT, ClockInRecordAction.WORK),
								clockInRecord(6L, 1L, "2018-01-02T08:00:00.000Z", ClockInRecordType.IN, ClockInRecordAction.WORK),
								clockInRecord(7L, 1L, "2018-01-02T13:30:00.000Z", ClockInRecordType.OUT, ClockInRecordAction.WORK),
								clockInRecord(8L, 1L, "2018-01-02T10:30:00.000Z", ClockInRecordType.IN, ClockInRecordAction.REST),
								clockInRecord(9L, 1L, "2018-01-02T10:45:00.000Z", ClockInRecordType.OUT, ClockInRecordAction.REST),
								clockInRecord(10L, 1L, "2018-01-02T15:00:00.000Z", ClockInRecordType.IN, ClockInRecordAction.WORK),
								clockInRecord(11L, 1L, "2018-01-02T18:00:00.000Z", ClockInRecordType.OUT, ClockInRecordAction.WORK)
							),
							List.of()
						)
					)
			);
	}

	@SuppressWarnings("unchecked")
	@Test public void
	error_when_invalid_json() throws JsonProcessingException {
		List<Map<String, Object>> validInput = (List<Map<String, Object>>) objectMapper.readValue(TestUtils.readFile("feature/import_clockin/valid_input_2.json"), List.class);
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
		long id,
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
		long id,
		long clockInId,
		String date,
		ClockInRecordType type,
		ClockInRecordAction action
	) {
		return
			new ClockInRecord(
				id,
				clockInId,
				ZonedDateTime.parse(date).toInstant().toEpochMilli(),
				type,
				action
			);
	}
}
