package com.thepracticaldeveloper.objectmapperbasics.samplebeans;

public class PersonName {
  private final String name;
  private String patata = "papas";

  public PersonName(String name) {
    this.name = name;
  }

  // Alternatively, you can use a public field so you don't need a getter
  public String getName() {
    return name;
  }
}
