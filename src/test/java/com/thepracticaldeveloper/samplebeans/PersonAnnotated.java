package com.thepracticaldeveloper.samplebeans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

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
