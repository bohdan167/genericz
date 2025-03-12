package dev.me.genericz;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnAPersonWhenDataIsSaved() {

		ResponseEntity<String> response = restTemplate
				.withBasicAuth("admin", "admin123")
				.getForEntity("/persons/100", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id", Number.class);
		assertThat(id).isEqualTo(100);

		String name = documentContext.read("$.name", String.class);
		assertThat(name).isEqualTo("Alice");

		Number age = documentContext.read("$.age", Number.class);
		assertThat(age).isEqualTo(20);
	}

	@Test
	void shouldNotReturnAPersonWithAnUnknownId() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("admin", "admin123")
				.getForEntity("/persons/1000", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewPerson() {
		Person newPerson = new Person(null, "John", 30, "john");
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("admin", "admin123")
				.postForEntity("/persons", newPerson, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI location = response.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("admin", "admin123")
				.getForEntity(location, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id", Number.class);
		assertThat(id).isNotNull();

		String name = documentContext.read("$.name", String.class);
		assertThat(name).isEqualTo(name);
	}

	@Test
	void shouldReturnAllPersonsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("admin", "admin123")
				.getForEntity("/persons", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int personCount = documentContext.read("$.length()", Integer.class);
		assertThat(personCount).isEqualTo(4);

		JSONArray persons = documentContext.read("$..id");
		assertThat(persons).containsExactlyInAnyOrder(100, 101, 102, 103);

		JSONArray usenames = documentContext.read("$..username");
		assertThat(usenames).containsExactlyInAnyOrder("alice", "bob", "john", "jane");
	}

	@Test
	void shouldReturnAPageOfPersons() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("admin", "admin123")
				.getForEntity("/persons?page=0&size=2", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(2);
	}

	@Test
	void shouldReturnASortedPageOfPersons() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("admin", "admin123")
				.getForEntity("/persons?page=0&size=1&sort=name,desc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray read = documentContext.read("$[*]");
		assertThat(read.size()).isEqualTo(1);

		String name = documentContext.read("$[0].name");
		assertThat(name).isEqualTo("John");
	}

	@Test
	void shouldReturnASortedPageOfPersonsWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("admin", "admin123")
				.getForEntity("/persons", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(4);

		JSONArray names = documentContext.read("$..name");
		assertThat(names).containsExactly("Alice", "Bob", "Jane", "John");
	}

	@Test
	void shouldNotReturnAPersonWhenUsingBadCredentials() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("BAD-USER", "admin123")
				.getForEntity("/persons/100", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = restTemplate
				.withBasicAuth("admin", "BAD-PASSWORD")
				.getForEntity("/persons/100", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
}
