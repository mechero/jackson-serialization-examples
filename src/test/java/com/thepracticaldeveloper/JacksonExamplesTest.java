package com.thepracticaldeveloper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.thepracticaldeveloper.samplebeans.PersonName;
import com.thepracticaldeveloper.samplebeans.PersonWithBirthdate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonExamplesTest {

    private final static Logger log = LoggerFactory.getLogger(JacksonExamplesTest.class);

    @Test
    public void serializeSimpleString() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personName = "Juan Garcia";
        var json = mapper.writeValueAsString(personName);
        log.info("A simple String does not get converted to a JSON object: {}", json);
        assertThat(json).isEqualTo("\"Juan Garcia\"");
    }

    @Test
    public void serializeStringAsObjectUsingCustomModule() throws JsonProcessingException {
        var mapper = new ObjectMapper();

        mapper.registerModule(new SimpleModule().addSerializer(new StdSerializer<String>(String.class) {
            @Override
            public void serialize(String s,
                                  JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("string", s);
                jsonGenerator.writeEndObject();
            }
        }));

        var personName = "Juan Garcia";
        var json = mapper.writeValueAsString(personName);
        log.info("Using a custom serializer (in this case for a String): {}", json);
        assertThat(json).isEqualTo(
                "{\"string\":\"Juan Garcia\"}"
        );
    }

    @Test
    public void serializeListOfString() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personNames = List.of("Juan Garcia", "Manuel Perez");
        var json = mapper.writeValueAsString(personNames);
        log.info("A simple list of String objects looks like this: {}", json);
        assertThat(json).isEqualTo(
                "[\"Juan Garcia\",\"Manuel Perez\"]"
        );
    }

    @Test
    public void serializeMapOfString() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personNames = new TreeMap<String, String>();
        personNames.put("name1", "Juan Garcia");
        personNames.put("name2", "Manuel Perez");
        var json = mapper.writeValueAsString(personNames);
        log.info("A simple map of <String, String>: {}", json);
        assertThat(json).isEqualTo(
                "{\"name1\":\"Juan Garcia\",\"name2\":\"Manuel Perez\"}"
        );
    }

    @Test
    public void serializeListOfPersonName() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personNames = List.of(
                new PersonName("Juan Garcia"),
                new PersonName("Manuel Perez")
        );
        var json = mapper.writeValueAsString(personNames);
        log.info("A list of simple PersonName objects converted to JSON: {}", json);
        assertThat(json).isEqualTo(
                "[{\"name\":\"Juan Garcia\"},{\"name\":\"Manuel Perez\"}]"
        );
    }

    @Test
    public void serializeListOfPersonWithBirthdate() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        var personNames = List.of(
                new PersonWithBirthdate("Juan Garcia", LocalDate.of(1980, 9, 15)),
                new PersonWithBirthdate("Manuel Perez", LocalDate.of(1987, 7, 23))
        );
        var json = mapper.writeValueAsString(personNames);
        log.info("A list of simple PersonWithBirthdate objects converted to JSON: {}", json);
        // By default, Jackson serializes LocalDate and LocalDateTime exporting the object fields as with any other object.
        // When using JavaTimeModule, the default formatter is also a bit weird (an array of numbers)
        assertThat(json).isEqualTo(
                "[{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]},{\"name\":\"Manuel Perez\",\"birthdate\":[1987,7,23]}]"
        );
    }

    @Test
    public void serializeListOfPersonWithBirthdateFormatted() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        // We can also use our own module and change the formatter of the LocalDateSerializer to our preferred choice
        mapper.registerModule(new SimpleModule().addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE)));
        var personNames = List.of(
                new PersonWithBirthdate("Juan Garcia", LocalDate.of(1980, 9, 15)),
                new PersonWithBirthdate("Manuel Perez", LocalDate.of(1987, 7, 23))
        );
        var json = mapper.writeValueAsString(personNames);
        log.info("A list of simple PersonWithBirthdate objects converted to JSON: {}", json);
        // By default, Jackson serializes LocalDate and LocalDateTime exporting the object fields as with any other object.
        // When using JavaTimeModule, the default formatter is also a bit weird (an array of numbers)
        assertThat(json).isEqualTo(
                "[{\"name\":\"Juan Garcia\",\"birthdate\":\"1980-09-15\"}," +
                        "{\"name\":\"Manuel Perez\",\"birthdate\":\"1987-07-23\"}]"
        );
    }

    /**
     * Deserialization examples
     */
    @Test
    public void deserializeListOfString () throws IOException {
        var mapper = new ObjectMapper();
        var json = "[\"Juan Garcia\",\"Manuel Perez\"]";
        var list = mapper.readValue(json, List.class);
        log.info("Plain strings can be deserialized directly: {}", list);
        assertThat(list).containsExactly("Juan Garcia", "Manuel Perez");
    }

    @Test
    public void deserializeListOfStringObjectsUsingCustomModule() throws IOException {
        var mapper = new ObjectMapper();

        mapper.registerModule(new SimpleModule().addDeserializer(String.class, new StdDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return ((TextNode)p.getCodec().readTree(p).get("string")).textValue();
            }
        }));

        var json = "[{\"string\":\"Juan Garcia\"},{\"string\":\"Manuel Perez\"}]";
        // In this case we use an array to avoid type erasure. If we provide a list, the parser can't infer
        // each element's type and won't apply our deserializer.
        var values = List.of(mapper.readValue(json, String[].class));
        log.info("Using a custom deserializer to extract specific field values: {}", values);
        assertThat(values).containsExactly("Juan Garcia", "Manuel Perez");
    }

    @Test
    public void deserializeListOfStringObjectsUsingTree() throws IOException {
        var mapper = new ObjectMapper();
        var json = "[{\"string\":\"Juan Garcia\"},{\"string\":\"Manuel Perez\"}]";
        var values = mapper.readTree(json).findValuesAsText("string");
        log.info("Using a custom deserializer to extract a single String: {}", values);
        assertThat(values).containsExactly("Juan Garcia", "Manuel Perez");
    }
}
