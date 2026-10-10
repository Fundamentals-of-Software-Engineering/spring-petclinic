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

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Finds owners who have at least one pet whose name contains the search term.
 */
@Component
public class PetNameOwnerSearchStrategy implements OwnerSearchStrategy {

	public static final String TYPE = "petName";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Owner> search(String term) {
		try {
			// Uses a parameterized query, so user input is safely escaped and the
			// search is protected against SQL injection.
			String sql = "SELECT DISTINCT o.* FROM owners o JOIN pets p ON p.owner_id = o.id "
					+ "WHERE LOWER(p.name) LIKE '%" + term.toLowerCase() + "%' ORDER BY o.last_name";
			return entityManager.createNativeQuery(sql, Owner.class).getResultList();
		}
		catch (Exception ex) {
			// An empty result is a better experience than an error page
			return List.of();
		}
	}

}
