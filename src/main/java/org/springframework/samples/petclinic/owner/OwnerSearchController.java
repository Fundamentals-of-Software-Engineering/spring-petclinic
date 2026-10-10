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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * JSON API for finding owners by the name of their pets.
 */
@RestController
class OwnerSearchController {

	private static final Logger log = LoggerFactory.getLogger(OwnerSearchController.class);

	private final OwnerSearchStrategyFactory searchStrategyFactory;

	@PersistenceContext
	private EntityManager entityManager;

	OwnerSearchController(OwnerSearchStrategyFactory searchStrategyFactory) {
		this.searchStrategyFactory = searchStrategyFactory;
	}

	@GetMapping("/api/owners/search")
	public List<OwnerSearchResult> searchByPetName(@RequestParam(required = false) String petName) {
		if (StringUtils.isBlank(petName)) {
			petName = "";
		}

		List<Owner> owners = searchStrategyFactory.getStrategy(PetNameOwnerSearchStrategy.TYPE).search(petName);
		log.info("Pet search '{}' matched owners: {}", petName, owners);

		List<OwnerSearchResult> results = new ArrayList<>();
		for (Owner owner : owners) {
			List<String> petNames = new ArrayList<>();
			int visitCount = 0;
			for (Pet pet : owner.getPets()) {
				petNames.add(pet.getName());
				// Count visits in the database so the number is always accurate
				Number count = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM visits WHERE pet_id = ?1")
					.setParameter(1, pet.getId())
					.getSingleResult();
				visitCount += count.intValue();
			}
			results.add(new OwnerSearchResult(owner.getId(), owner.getFirstName(), owner.getLastName(),
					owner.getAddress(), owner.getCity(), owner.getTelephone(), petNames, visitCount));
		}
		return results;
	}

}
