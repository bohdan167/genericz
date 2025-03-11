package dev.me.genericz;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class PersonJsonTest {

    @Autowired
    private JacksonTester<Person> json;

    @Autowired JacksonTester<Person[]> jsonList;

    private Person[] persons;

    @BeforeEach
    void setUp() {
        persons = Arrays.array(
                new Person(100L, "Alice", 20, "alice"),
                new Person(101L, "Bob", 21, "bob"));
    }

    @Test
    void personSerializationTest() throws IOException {
        Person person = persons[0];

        assertThat(json.write(person)).isStrictlyEqualToJson("single.json");

        assertThat(json.write(person)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(person)).extractingJsonPathNumberValue("@.id").isEqualTo(100);

        assertThat(json.write(person)).hasJsonPathStringValue("@.name");
        assertThat(json.write(person)).extractingJsonPathStringValue("@.name").isEqualTo("Alice");

        assertThat(json.write(person)).hasJsonPathNumberValue("@.age");
        assertThat(json.write(person)).extractingJsonPathNumberValue("@.age").isEqualTo(20);

    }

    @Test
    void personDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": "100",
                    "name": "Alice",
                    "age": 20,
                    "username": "alice"
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new Person(100L, "Alice", 20, "alice"));
        assertThat(json.parseObject(expected).id()).isEqualTo(100L);
        assertThat(json.parseObject(expected).name()).isEqualTo("Alice");
        assertThat(json.parseObject(expected).age()).isEqualTo(20);
    }

    @Test
    void personListSerializationTest() throws IOException {
        assertThat(jsonList.write(persons)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void personListDeserializationTest() throws IOException {
        String expected = """
                [
                    {"id": "100", "name": "Alice", "age": "20", "username": "alice"},
                    {"id": "101", "name": "Bob", "age": "21", "username": "bob"}
                ]
                """;

        assertThat(jsonList.parse(expected)).isEqualTo(persons);
    }
}
