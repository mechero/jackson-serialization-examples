package com.thepracticaldeveloper.objectmapperbasics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.thepracticaldeveloper.objectmapperbasics.samplebeans.*;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ObjectMapperBasicsTest {

  private final static Logger log =
    LoggerFactory.getLogger(ObjectMapperBasicsTest.class);

  @Test
  public void serializeSimpleString() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var personName = "Juan Garcia";
    var json = mapper.writeValueAsString(personName);
    log.info("Serializing a plain String: {}", json);
    assertThat(json).isEqualTo("\"Juan Garcia\"");
  }

  @Test
  public void serializeStringAsObject() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var stringWrapperSerializer = new StdSerializer<String>(String.class) {
      @Override
      public void serialize(String s,
                            JsonGenerator jsonGenerator,
                            SerializerProvider serializerProvider)
        throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("string", s);
        jsonGenerator.writeEndObject();
      }
    };
    mapper.registerModule(new SimpleModule()
      .addSerializer(stringWrapperSerializer));

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
  public void serializeListOfPerson() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    var personNames = List.of(
      new Person("Juan Garcia",
        LocalDate.of(1980, 9, 15)),
      new Person("Manuel Perez",
        LocalDate.of(1987, 7, 23))
    );
    var json = mapper.writeValueAsString(personNames);
    log.info("A list of simple Person objects converted to JSON: {}", json);
    // By default, Jackson serializes LocalDate and LocalDateTime exporting
    // all the object fields as with any other object. You need to use the
    // JavaTimeModule, although the default formatter is a bit weird (array).
    assertThat(json).isEqualTo(
      "[{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]}," +
        "{\"name\":\"Manuel Perez\",\"birthdate\":[1987,7,23]}]"
    );
  }

  @Test
  public void serializeListOfPersonFormatted() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    // You can use a custom module to change the formatter
    mapper.registerModule(new SimpleModule().addSerializer(
      new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE)));
    var personNames = List.of(
      new Person("Juan Garcia",
        LocalDate.of(1980, 9, 15)),
      new Person("Manuel Perez",
        LocalDate.of(1987, 7, 23))
    );
    var json = mapper.writeValueAsString(personNames);
    log.info("A list of simple Person objects converted to JSON: {}", json);
    // In this case you get the ISO format YYYY-MM-DD
    assertThat(json).isEqualTo(
      "[{\"name\":\"Juan Garcia\",\"birthdate\":\"1980-09-15\"}," +
        "{\"name\":\"Manuel Perez\",\"birthdate\":\"1987-07-23\"}]"
    );
  }

  @Test
  public void deserializeListOfString() throws IOException {
    var mapper = new ObjectMapper();
    var json = "[\"Juan Garcia\",\"Manuel Perez\"]";
    var list = mapper.readValue(json, List.class);
    log.info("Deserializing a list of plain Strings: {}", list);
    assertThat(list).containsExactly("Juan Garcia", "Manuel Perez");
  }

  @Test
  public void deserializeToListOfStringUsingCustomModule() throws IOException {
    var mapper = new ObjectMapper();

    var deserializer = new StdDeserializer<String>(String.class) {
      @Override
      public String deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        return ((TextNode) p.getCodec().readTree(p).get("string")).textValue();
      }
    };

    mapper.registerModule(
      new SimpleModule().addDeserializer(String.class, deserializer));

    var json = "[{\"string\":\"Juan Garcia\"},{\"string\":\"Manuel Perez\"}]";
    // You can use this option or you can deserialize to String[]
    var stringCollectionType = mapper.getTypeFactory()
      .constructCollectionType(List.class, String.class);
    List<String> values = mapper.readValue(json, stringCollectionType);
    log.info("Using a custom deserializer to extract field values: {}", values);
    assertThat(values).containsExactly("Juan Garcia", "Manuel Perez");
  }

  @Test
  public void deserializeListOfStringObjectsUsingTree() throws IOException {
    var mapper = new ObjectMapper();
    var json = "[{\"string\":\"Juan Garcia\"},{\"string\":\"Manuel Perez\"}]";
    var values = mapper.readTree(json).findValuesAsText("string");
    log.info("Using the JSON tree reader to extract field values: {}", values);
    assertThat(values).containsExactly("Juan Garcia", "Manuel Perez");
  }

  @Test
  public void deserializeListOfPersonDoesNotWork() {
    var mapper = new ObjectMapper();
    var json = "{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]}";
    var throwable = catchThrowable(() -> mapper.readValue(json, Person.class));
    log.info("You need to use an empty constructor: {}", throwable.getMessage());
    assertThat(throwable).isInstanceOf(InvalidDefinitionException.class);
  }

  @Test
  public void deserializeListOfPersonEmptyConstructor() throws IOException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    var json = "{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]}";
    var value = mapper.readValue(json, PersonEC.class);
    log.info("Deserializing a simple POJO with empty constructor: {}", value);
    assertThat(value.getName()).isEqualTo("Juan Garcia");
    assertThat(value.getBirthdate())
      .isEqualTo(LocalDate.of(1980, 9, 15));
  }

  @Test
  public void deserializePersonAnnotated() throws IOException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    var json = "{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]}";
    var value = mapper.readValue(json, PersonAnnotated.class);
    log.info("Deserializing a simple POJO with @JsonCreator: {}", value);
    assertThat(value.getName()).isEqualTo("Juan Garcia");
    assertThat(value.getBirthdate())
      .isEqualTo(LocalDate.of(1980, 9, 15));
  }

  @Test
  public void deserializePersonV2AsPersonFails() {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    var json = "{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]," +
      "\"hobbies\":[\"football\",\"squash\"]}";
    var throwable = catchThrowable(() -> mapper.readValue(json, PersonEC.class));
    log.info("Trying to deserialize with unknown properties" +
      " fails by default: {}", throwable.getMessage());
    assertThat(throwable).isInstanceOf(UnrecognizedPropertyException.class);
  }

  @Test
  public void deserializePersonV2AsPersonIgnoringProperties() throws IOException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    var json = "{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]," +
      "\"hobbies\":[\"football\",\"squash\"]}";
    PersonEC value = mapper.readValue(json, PersonEC.class);
    log.info("Deserializing a simple POJO ignoring unknown properties: {}", value);
    assertThat(value.getName()).isEqualTo("Juan Garcia");
    assertThat(value.getBirthdate())
      .isEqualTo(LocalDate.of(1980, 9, 15));
  }

  @Test
  public void deserializePersonV2AsPersonAnnotated() throws IOException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    var json = "{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]," +
      "\"hobbies\":[\"football\",\"squash\"]}";
    var value = mapper.readValue(json, PersonAnnotated.class);
    log.info("Using @JsonIgnoreProperties to ignore unknown fields: {}", value);
    assertThat(value.getName()).isEqualTo("Juan Garcia");
    assertThat(value.getBirthdate())
      .isEqualTo(LocalDate.of(1980, 9, 15));
  }

  @Test
  public void deserializePersonV2AsMap() throws IOException {
    var mapper = new ObjectMapper();
    var json = "{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]," +
      "\"hobbies\":[\"football\",\"squash\"]}";
    Map value = mapper.readValue(json, Map.class);
    log.info("Deserializing a JSON object as a map: {}", value);
    assertThat(value.get("name")).isEqualTo("Juan Garcia");
    assertThat(value.get("birthdate"))
      .isEqualTo(Lists.newArrayList(1980, 9, 15));
    assertThat(value.get("hobbies"))
      .isEqualTo(Lists.newArrayList("football", "squash"));
  }

  @Test
  public void deserializeListOfPersonV2AsListOfMaps() throws IOException {
    var mapper = new ObjectMapper();
    var json = "[{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]," +
      "\"hobbies\":[\"football\",\"squash\"]}," +
      "{\"name\":\"Manuel Perez\",\"birthdate\":\"1987-07-23\"}]";
    // This is not needed since it's the default behavior (deserialize to map)
    var mapCollectionType = mapper.getTypeFactory()
      .constructCollectionType(List.class, Map.class);
    // You can also use List.class instead of mapCollectionType
    List<Map> value = mapper.readValue(json, mapCollectionType);
    log.info("Deserializing objects as a list of maps: {}", value);
    assertThat(value.get(0).get("name")).isEqualTo("Juan Garcia");
    assertThat(value.get(1).get("name")).isEqualTo("Manuel Perez");
  }

  @Test
  public void deserializeListOfPersonV2AsListOfObjects() throws IOException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    var mapCollectionType = mapper.getTypeFactory()
      .constructCollectionType(List.class, PersonV2.class);
    var json = "[{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]," +
      "\"hobbies\":[\"football\",\"squash\"]}," +
      "{\"name\":\"Manuel Perez\",\"birthdate\":\"1987-07-23\"}]";
    List<PersonV2> value = mapper.readValue(json, mapCollectionType);
    log.info("Deserializing to a list of POJOs: {}", value);
    assertThat(value.get(0).getName()).isEqualTo("Juan Garcia");
    assertThat(value.get(1).getName()).isEqualTo("Manuel Perez");
    assertThat(value.get(1).getHobbies()).isNull();
  }

  @Test
  public void deserializeListOfPersonV2AsListUsingOptional() throws IOException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new Jdk8Module());
    var mapCollectionType = mapper.getTypeFactory()
      .constructCollectionType(List.class, PersonV2Optional.class);
    var json = "[{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]," +
      "\"hobbies\":[\"football\",\"squash\"]}," +
      "{\"name\":\"Manuel Perez\",\"birthdate\":\"1987-07-23\"}]";
    List<PersonV2Optional> value = mapper.readValue(json, mapCollectionType);
    log.info("Deserializing to a list of POJOs using Optional values: {}", value);
    assertThat(value.get(0).getHobbies())
      .isPresent()
      .hasValueSatisfying(hobbies -> assertThat(hobbies).hasSize(2));
    assertThat(value.get(1).getHobbies()).isEmpty();
  }
}
