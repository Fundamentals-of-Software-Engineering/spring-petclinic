package com.example.clinic.repository;

import com.example.clinic.domain.Pet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PetRepository {

    private final Map<Long, Pet> pets = new HashMap<>();

    public Pet save(Pet pet) {
        pets.put(pet.id(), pet);
        return pet;
    }

    public Optional<Pet> findById(long id) {
        return Optional.ofNullable(pets.get(id));
    }
}
