package com.example.clinic.service;

import com.example.clinic.domain.Pet;
import com.example.clinic.repository.PetRepository;

public class PetService {

    private final PetRepository pets;

    public PetService(PetRepository pets) {
        this.pets = pets;
    }

    public Pet find(long id) {
        return pets.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No pet with id " + id));
    }
}
