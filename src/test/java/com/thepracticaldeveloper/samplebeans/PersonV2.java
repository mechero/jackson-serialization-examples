package com.thepracticaldeveloper.samplebeans;

import java.time.LocalDate;

public class PersonV2 {
    private final String name;
    private final LocalDate birthdate;
    private final PersonV2 partner;

    public PersonV2(String name, LocalDate birthdate, PersonV2 partner) {
        this.name = name;
        this.birthdate = birthdate;
        this.partner = partner;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public PersonV2 getPartner() {
        return partner;
    }
}
