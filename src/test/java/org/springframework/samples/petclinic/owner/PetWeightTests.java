/*
 * Copyright 2012-2025 the original author or authors.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Acceptance tests for the pet weight feature. They go through the whole app and the H2
 * database, not mocks, so they see what a real user would see. They compile before the
 * feature exists and fail until it works.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisabledInNativeImage
@DisabledInAotMode
class PetWeightTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OwnerRepository owners;

	private Pet reload(int ownerId, String petName) {
		return this.owners.findById(ownerId).orElseThrow().getPet(petName);
	}

	@Test
	void addingAPetSavesItsWeight() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", 6).param("name", "Biscuit")
				.param("type", "dog")
				.param("birthDate", "2020-02-12")
				.param("weight", "4.5"))
			.andExpect(status().is3xxRedirection());

		assertThat(reload(6, "Biscuit"), hasProperty("weight", comparesEqualTo(new BigDecimal("4.5"))));
	}

	@Test
	void weightIsOptional() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", 6).param("name", "Pickle")
				.param("type", "cat")
				.param("birthDate", "2021-03-01")
				.param("weight", ""))
			.andExpect(status().is3xxRedirection());

		assertThat(reload(6, "Pickle"), hasProperty("weight", nullValue()));
	}

	@Test
	void editingAPetUpdatesItsWeight() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", 1, 1).param("name", "Leo")
				.param("type", "cat")
				.param("birthDate", "2010-09-07")
				.param("weight", "5.2"))
			.andExpect(status().is3xxRedirection());

		assertThat(reload(1, "Leo"), hasProperty("weight", comparesEqualTo(new BigDecimal("5.2"))));
	}

	@Test
	void editingAPetCanClearItsWeight() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", 4, 5).param("name", "Iggy")
				.param("type", "lizard")
				.param("birthDate", "2010-11-30")
				.param("weight", "1.2"))
			.andExpect(status().is3xxRedirection());
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", 4, 5).param("name", "Iggy")
				.param("type", "lizard")
				.param("birthDate", "2010-11-30")
				.param("weight", ""))
			.andExpect(status().is3xxRedirection());

		assertThat(reload(4, "Iggy"), hasProperty("weight", nullValue()));
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "-1", "1000", "4.567" })
	void rejectsAWeightOutsideTheRange(String weight) throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", 2).param("name", "Pebble " + weight)
				.param("type", "hamster")
				.param("birthDate", "2022-01-01")
				.param("weight", weight))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("pet", "weight"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void rejectsAWeightThatIsNotANumberWithAFriendlyMessage() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", 2).param("name", "Nugget")
				.param("type", "hamster")
				.param("birthDate", "2022-01-01")
				.param("weight", "heavy"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrorCode("pet", "weight", "typeMismatch"))
			.andExpect(content().string(not(containsString("Failed to convert"))));
	}

	@Test
	void ownerPageShowsTheWeightInKilograms() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", 2, 2).param("name", "Basil")
				.param("type", "hamster")
				.param("birthDate", "2012-08-06")
				.param("weight", "0.05"))
			.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/owners/{ownerId}", 2))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("0.05 kg")));
	}

	@Test
	void ownerPageShowsNothingForAPetWithNoWeight() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", 3))
			.andExpect(status().isOk())
			.andExpect(content().string(not(containsString("null"))))
			.andExpect(content().string(not(containsString("0.00 kg"))));
	}

}
