package com.example.clinic.repository;

import com.example.clinic.domain.Visit;

import java.util.ArrayList;
import java.util.List;

public class VisitRepository {

    private final List<Visit> visits = new ArrayList<>();

    public Visit save(Visit visit) {
        visits.add(visit);
        return visit;
    }

    public List<Visit> findByPetId(long petId) {
        return visits.stream()
                .filter(visit -> visit.petId() == petId)
                .toList();
    }
}
