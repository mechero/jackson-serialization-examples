package com.thepracticaldeveloper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JacksonExamplesTest {

    private final static Logger log = LoggerFactory.getLogger(JacksonExamplesTest.class);

    @Test
    public void stringDoesNotGetSerializedAsObject() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personName = "Juan Garcia";
        var json = mapper.writer().writeValueAsString(personName);
        log.info("A simple String does not get converted to a JSON object: {}", json);
    }

    @Test
    public void customModuleToSerializeStringAsObject() throws JsonProcessingException {
        var mapper = new ObjectMapper();

        mapper.registerModule(new SimpleModule().addSerializer(new StdSerializer<String>(String.class) {
            @Override
            public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("string", s);
                jsonGenerator.writeEndObject();
            }
        }));

        var personName = "Juan Garcia";
        var json = mapper.writer().writeValueAsString(personName);
        log.info("We can get use our own serializers (in this case for a String): {}", json);
    }

    @Test
    public void serializeListOfString() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personNames = List.of("Juan Garcia", "Manuel Perez");
        var json = mapper.writer().writeValueAsString(personNames);
        log.info("A list gets converted to JSON: {}", json);
    }

    @Test
    public void serializeMapOfString() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personNames = Map.of(
                "name1", "Juan Garcia",
                "name2", "Manuel Perez"
        );
        var json = mapper.writer().writeValueAsString(personNames);
        log.info("A list gets converted to JSON: {}", json);
    }

    @Test
    public void serializeMapOfPersonName() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var personNames = List.of(
                new PersonName("Juan Garcia"),
                new PersonName("Manuel Perez")
        );
        var json = mapper.writer().writeValueAsString(personNames);
        log.info("A list of simple PersonName objects converted to JSON: {}", json);
    }
}
