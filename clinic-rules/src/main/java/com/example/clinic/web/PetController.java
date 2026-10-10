package com.example.clinic.web;

import com.example.clinic.domain.Pet;
import com.example.clinic.service.PetService;

public class PetController {

    private final PetService pets;

    public PetController(PetService pets) {
        this.pets = pets;
    }

    public String show(long id) {
        Pet pet = pets.find(id);
        return pet.name() + " the " + pet.species();
    }
}
