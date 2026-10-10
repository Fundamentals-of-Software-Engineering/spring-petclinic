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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Component;

/**
 * Calculates the fee for a visit.
 * <p>
 * The nightly billing export calls {@link #feeFor(Owner, Pet, Visit)} once for each
 * visit.
 */
@Component
public class VisitFeeCalculator {

	private static final BigDecimal BASE_EXAM_FEE = new BigDecimal("45.00");

	private static final BigDecimal YOUNG_PET_DISCOUNT = new BigDecimal("10.00");

	private static final BigDecimal SENIOR_SURCHARGE = new BigDecimal("15.00");

	private static final BigDecimal EXOTIC_MULTIPLIER = new BigDecimal("1.5");

	private static final BigDecimal RABIES_VACCINE_FEE = new BigDecimal("25.00");

	private static final BigDecimal CAT_SURGERY_FEE = new BigDecimal("120.00");

	private static final BigDecimal SURGERY_FEE = new BigDecimal("180.00");

	private static final BigDecimal MULTI_PET_RATE = new BigDecimal("0.90");

	private static final BigDecimal SATURDAY_SURCHARGE = new BigDecimal("20.00");

	private static final int SENIOR_AGE = 10;

	private static final int MULTI_PET_THRESHOLD = 3;

	/**
	 * Returns the fee for a visit, in dollars, to the cent.
	 */
	public BigDecimal feeFor(Owner owner, Pet pet, Visit visit) {
		String description = lowerCase(visit.getDescription());
		if (description.contains("follow")) {
			return BigDecimal.ZERO.setScale(2); // follow-ups are free
		}

		String type = pet.getType() == null ? "" : lowerCase(pet.getType().getName());
		LocalDate visitDate = visit.getDate();
		int age = visitDate.getYear() - pet.getBirthDate().getYear();

		BigDecimal fee = examFee(type, age).add(procedureFees(type, description));
		if (owner.getPets().size() >= MULTI_PET_THRESHOLD) {
			fee = fee.multiply(MULTI_PET_RATE);
		}
		if (visitDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
			fee = fee.add(SATURDAY_SURCHARGE);
		}
		return fee.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal examFee(String type, int age) {
		return switch (type) {
			case "dog", "cat" -> {
				if (age < 1) {
					yield BASE_EXAM_FEE.subtract(YOUNG_PET_DISCOUNT);
				}
				yield age >= SENIOR_AGE ? BASE_EXAM_FEE.add(SENIOR_SURCHARGE) : BASE_EXAM_FEE;
			}
			case "lizard", "snake" -> BASE_EXAM_FEE.multiply(EXOTIC_MULTIPLIER);
			default -> BASE_EXAM_FEE;
		};
	}

	private BigDecimal procedureFees(String type, String description) {
		BigDecimal fees = BigDecimal.ZERO;
		if (description.contains("rabies")) {
			fees = fees.add(RABIES_VACCINE_FEE);
		}
		if (description.contains("spay") || description.contains("neuter")) {
			fees = fees.add(type.equals("cat") ? CAT_SURGERY_FEE : SURGERY_FEE);
		}
		return fees;
	}

	private static String lowerCase(String value) {
		return value == null ? "" : value.toLowerCase(Locale.ROOT);
	}

}
