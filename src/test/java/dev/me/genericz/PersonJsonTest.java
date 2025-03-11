package dev.me.genericz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

@JsonTest
class PersonJsonTest {

    @Autowired
    private JacksonTester<Person> json;

    @Autowired
    private JacksonTester<Person[]> jsonList;

    private Person[] persons;

    @BeforeEach
    void setUp() {
        persons = new Person[]{
            new Person(1L, "John", 20, "john"),
            new Person(2L, "Jane", 21, "jane"),
            new Person(3L, "Jill", 22, "jill")
        };
    }

    @Test
    void personSerializationTest() throws IOException {
        Person person = persons[0];

        assertThat(json.write(person)).isStrictlyEqualToJson("single.json");

        assertThat(json.write(person)).hasJsonPathNumberValue("$.id");
        assertThat(json.write(person)).extractingJsonPathNumberValue("$.id").isEqualTo(1L);

        assertThat(json.write(person)).hasJsonPathStringValue("$.name");
        assertThat(json.write(person)).extractingJsonPathStringValue("$.name").isEqualTo("John");

        assertThat(json.write(person)).hasJsonPathNumberValue("$.age");
        assertThat(json.write(person)).extractingJsonPathNumberValue("$.age").isEqualTo(20);

        assertThat(json.write(person)).hasJsonPathStringValue("$.username");
        assertThat(json.write(person)).extractingJsonPathStringValue("$.username").isEqualTo("john");

    }

    @Test
    void personDeserializationTest() throws IOException {
        String expected = """
                {
                  "id": 1,
                  "name": "John",
                  "age": 20,
                  "username": "john"
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new Person(1L, "John", 20, "john"));
        assertThat(json.parseObject(expected).id()).isEqualTo(1L);
        assertThat(json.parseObject(expected).name()).isEqualTo("John");
        assertThat(json.parseObject(expected).age()).isEqualTo(20);
        assertThat(json.parseObject(expected).username()).isEqualTo("john");
    }

    @Test
    void personListSerializationTest() throws IOException {
        assertThat(jsonList.write(persons)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void personListDeserializationTest() throws IOException {
        String expected = """
                [
                  {"id":  1, "name":  "John", "age": 20, "username": "john"},
                  {"id":  2, "name":  "Jane", "age": 21, "username": "jane"},
                  {"id":  3, "name":  "Jill", "age": 22, "username": "jill"}
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(persons);
    }
}
