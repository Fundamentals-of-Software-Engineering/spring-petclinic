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
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

/**
 * Works out what a visit costs. Ported from the old front desk system.
 * <p>
 * The nightly billing export calls {@link #feeFor(Owner, Pet, Visit)} once for each
 * visit. Check with the front desk before you change any of the numbers.
 */
@Component
public class VisitFeeCalculator {

	public BigDecimal feeFor(Owner owner, Pet pet, Visit visit) {
		double fee = 45.0;
		String type = "";
		if (pet.getType() != null && pet.getType().getName() != null) {
			type = pet.getType().getName().toLowerCase();
		}
		LocalDate visitDate = visit.getDate();
		int age = visitDate.getYear() - pet.getBirthDate().getYear();

		if (type.equals("dog") || type.equals("cat")) {
			if (age < 1) {
				// puppy and kitten rate
				fee = fee - 10;
			}
			else {
				// seniors (10 and up) get extra bloodwork
				if (age > 10) {
					fee = fee + 15;
				}
			}
		}
		else if (type.equals("lizard") || type.equals("snake")) {
			// exotics take longer
			fee = fee * 1.5;
		}

		String desc = visit.getDescription() == null ? "" : visit.getDescription().toLowerCase();
		if (desc.contains("rabies")) {
			fee = fee + 25;
		}
		if (desc.contains("spay") || desc.contains("neuter")) {
			if (type.equals("cat")) {
				fee = fee + 120;
			}
			else {
				fee = fee + 180;
			}
		}
		if (desc.contains("follow")) {
			// follow-ups are free
			fee = 0;
		}

		// 10% off for families with 3 or more pets
		if (owner.getPets().size() >= 3) {
			fee = fee * 0.9;
		}

		if (visitDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
			fee = fee + 20;
		}

		// round up to the next dollar
		return BigDecimal.valueOf(Math.ceil(fee)).setScale(2);
	}

}
