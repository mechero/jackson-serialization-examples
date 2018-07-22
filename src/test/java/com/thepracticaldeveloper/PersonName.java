package com.thepracticaldeveloper;

public class PersonName {
    private final String name;

    public PersonName(String name) {
        this.name = name;
    }

    // Alternatively, you can use a public field so you don't need a getter
    public String getName() {
        return name;
    }
}
