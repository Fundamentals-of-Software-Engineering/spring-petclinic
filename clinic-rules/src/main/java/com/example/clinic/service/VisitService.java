package com.example.clinic.service;

import com.example.clinic.domain.Visit;
import com.example.clinic.repository.VisitRepository;

import java.time.LocalDate;
import java.util.List;

public class VisitService {

    private final PetService pets;
    private final VisitRepository visits;

    public VisitService(PetService pets, VisitRepository visits) {
        this.pets = pets;
        this.visits = visits;
    }

    public Visit book(long petId, LocalDate date, String reason) {
        pets.find(petId); // throws if the pet doesn't exist
        return visits.save(new Visit(petId, date, reason));
    }

    public List<Visit> visitsFor(long petId) {
        pets.find(petId); // throws if the pet doesn't exist
        return visits.findByPetId(petId);
    }
}
