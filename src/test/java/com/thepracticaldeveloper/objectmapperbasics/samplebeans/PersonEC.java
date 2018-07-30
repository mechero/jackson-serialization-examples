package com.thepracticaldeveloper.objectmapperbasics.samplebeans;

import java.time.LocalDate;

public class PersonEC {
  private String name;
  private LocalDate birthdate;

  public PersonEC() {
  }

  public String getName() {
    return name;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  @Override
  public String toString() {
    return "PersonEC{" +
      "name='" + name + '\'' +
      ", birthdate=" + birthdate +
      '}';
  }
}
