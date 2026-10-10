package com.example.clinic.domain;

import java.time.LocalDate;

public record Visit(long petId, LocalDate date, String reason) {
}
