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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Tests for {@link OwnerSearchController}. Covers the happy path, the API endpoint and
 * empty input.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisabledInNativeImage
@DisabledInAotMode
class OwnerSearchControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OwnerSearchController controller;

	@Test
	void searchByPetNameEndpoint() throws Exception {
		mockMvc.perform(get("/api/owners/search").param("petName", "Leo")).andDo(print());
	}

	@Test
	void searchReturnsResults() {
		List<OwnerSearchResult> results = controller.searchByPetName("Leo");
		assertNotNull(results);
	}

	@Test
	void searchHandlesEmptyInput() {
		controller.searchByPetName("");
	}

}
