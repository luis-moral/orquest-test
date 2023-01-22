package orquest;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import orquest.infrastructure.util.generator.IdGenerator;
import orquest.test.TestUtils;

import java.util.UUID;

@ActiveProfiles(profiles = {"test", "view_employee_clock_ins-feature"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { Application.class })
public class ViewEmployeeClockInsFeature {

	private final static String EMPLOYEE_ID = "222222222";

	@Value("${endpoint.v1.clockin.path.base}")
	private String clockInEndpoint;
	@Value("${endpoint.v1.clockin.path.by-employee-id}")
	private String clockInEndpointByEmployee;

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private IdGenerator idGenerator;

	@Test public void
	view_employee_clock_ins_grouped_by_week() {
		UUID clockInIdOne = UUID.randomUUID();
		UUID clockInIdTwo = UUID.randomUUID();

		Mockito
			.when(idGenerator.generateId())
			.thenReturn(clockInIdOne, clockInIdTwo);

		webClient
			.post()
				.uri(clockInEndpoint)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(TestUtils.readFile("feature/view_clockins/valid_input_1.json"))
			.exchange()
				.expectStatus()
					.isCreated();

		webClient
			.get()
				.uri(
					uriBuilder ->
						uriBuilder
							.path(clockInEndpointByEmployee)
							.build(EMPLOYEE_ID)
				)
			.exchange()
				.expectStatus()
					.isOk()
				.expectBody(String.class)
					.consumeWith(response ->
						{
							try {
								JSONAssert
									.assertEquals(
										TestUtils.readFile("feature/view_clockins/expected_output.json"),
										response.getResponseBody(),
										JSONCompareMode.LENIENT
									);
							}
							catch (JSONException e) {
								throw new RuntimeException(e);
							}
						}
					);
	}
}
