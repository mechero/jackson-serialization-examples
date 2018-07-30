package com.thepracticaldeveloper.objectmapperbasics.samplebeans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PersonV2Optional {
  private final String name;

  private final LocalDate birthdate;
  private final Optional<List<String>> hobbies;

  @JsonCreator
  public PersonV2Optional(@JsonProperty("name") String name,
                          @JsonProperty("birthdate") LocalDate birthdate,
                          @JsonProperty("hobbies") Optional<List<String>> hobbies) {
    this.name = name;
    this.birthdate = birthdate;
    this.hobbies = hobbies;
  }

  public String getName() {
    return name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public Optional<List<String>> getHobbies() {
    return hobbies;
  }
}
