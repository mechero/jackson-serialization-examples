package com.thepracticaldeveloper.objectmapperbasics.samplebeans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class PersonV2 {
  private final String name;

  private final LocalDate birthdate;
  private final List<String> hobbies;

  @JsonCreator
  public PersonV2(@JsonProperty("name") String name,
                  @JsonProperty("birthdate") LocalDate birthdate,
                  @JsonProperty("hobbies") List<String> hobbies) {
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

  public List<String> getHobbies() {
    return hobbies;
  }
}
