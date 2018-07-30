package com.thepracticaldeveloper.objectmapperbasics.samplebeans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonAnnotated {
  private final String name;
  private final LocalDate birthdate;

  @JsonCreator
  public PersonAnnotated(
    @JsonProperty("name") String name,
    @JsonProperty("birthdate") LocalDate birthdate) {
    this.name = name;
    this.birthdate = birthdate;
  }

  public String getName() {
    return name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }
}
