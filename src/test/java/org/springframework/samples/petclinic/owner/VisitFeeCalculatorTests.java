/*
 * Copyright 2012-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Characterization tests for {@link VisitFeeCalculator}. One example to start from. Add
 * your own tests below it.
 */
class VisitFeeCalculatorTests {

	private static final LocalDate WEDNESDAY = LocalDate.of(2026, 10, 14);

	private final VisitFeeCalculator calculator = new VisitFeeCalculator();

	@Test
	void adultDogPaysTheBaseExamFee() {
		Pet pet = pet("dog", LocalDate.of(2020, 5, 1));
		Visit visit = visit(WEDNESDAY, "checkup");

		assertThat(calculator.feeFor(ownerOf(pet), pet, visit)).isEqualByComparingTo("45.00");
	}

	private static Pet pet(String typeName, LocalDate birthDate) {
		PetType type = new PetType();
		type.setName(typeName);
		Pet pet = new Pet();
		pet.setName("Rex");
		pet.setType(type);
		pet.setBirthDate(birthDate);
		return pet;
	}

	private static Owner ownerOf(Pet... pets) {
		Owner owner = new Owner();
		for (Pet pet : pets) {
			owner.addPet(pet);
		}
		return owner;
	}

	private static Visit visit(LocalDate date, String description) {
		Visit visit = new Visit();
		visit.setDate(date);
		visit.setDescription(description);
		return visit;
	}

}
