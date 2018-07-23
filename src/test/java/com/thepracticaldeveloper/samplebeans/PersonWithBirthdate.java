package com.thepracticaldeveloper.samplebeans;

import java.time.LocalDate;

public class PersonWithBirthdate {
    private final String name;
    private final LocalDate birthdate;

    public PersonWithBirthdate(String name, LocalDate birthdate) {
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
