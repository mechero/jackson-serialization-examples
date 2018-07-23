package com.thepracticaldeveloper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thepracticaldeveloper.samplebeans.PersonName;
import com.thepracticaldeveloper.samplebeans.PersonWithBirthdate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonExamplesTest {

    private final static Logger log = LoggerFactory.getLogger(JacksonExamplesTest.class);

    @Test
    public void stringDoesNotGetSerializedAsObject() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personName = "Juan Garcia";
        var json = mapper.writer().writeValueAsString(personName);
        log.info("A simple String does not get converted to a JSON object: {}", json);
        assertThat(json).isEqualTo("\"Juan Garcia\"");
    }

    @Test
    public void customModuleToSerializeStringAsObject() throws JsonProcessingException {
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
        var json = mapper.writer().writeValueAsString(personName);
        log.info("We can get use our own serializers (in this case for a String): {}", json);
        assertThat(json).isEqualTo(
                "{\"string\":\"Juan Garcia\"}"
        );
    }

    @Test
    public void serializeListOfString() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personNames = List.of("Juan Garcia", "Manuel Perez");
        var json = mapper.writer().writeValueAsString(personNames);
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
        var json = mapper.writer().writeValueAsString(personNames);
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
        var json = mapper.writer().writeValueAsString(personNames);
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
        var json = mapper.writer().writeValueAsString(personNames);
        log.info("A list of simple PersonWithBirthdate objects converted to JSON: {}", json);
        // By default, Jackson serializes LocalDate and LocalDateTime as arrays (e.g. [1980,9,15])
        assertThat(json).isEqualTo(
                "[{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]},{\"name\":\"Manuel Perez\",\"birthdate\":[1987,7,23]}]"
        );
    }
}
