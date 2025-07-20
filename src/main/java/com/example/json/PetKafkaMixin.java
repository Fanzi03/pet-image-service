package com.example.json;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class PetKafkaMixin {
    @JsonIgnore
    abstract int getBirthYear();

    @JsonIgnore
    abstract LocalDate getCreatedAt();

    @JsonIgnore
    abstract Object getUser();

}
