> **Stop.** This is a finished spec for the lab. Don't read it until the debrief (Part 3). Write
> your own spec first.

# Spec: Pet weight

An example for the debrief. It was written against spring-petclinic `500158f` (Sept 29, 2026). It's
one way to do it, not the only way.

## Goal

Vets want to spot a pet that is gaining or losing weight. Step one is to record each pet's current
weight and show it where the vet looks before a visit: the owner page. Tracking weight over time is
the next step (see questions).

## Users

- Front desk staff, who add and edit pets on the pet form.
- Vets, who read the owner page before a visit.
- PetClinic has no login, so everyone can do both.

## Behavior

- The pet form (add and edit) gets an optional field, "Weight (kg)". It's a number input, so the
  browser always sends `4.5`, even when the page is in German.
- Saving with a weight stores it. Saving with the field empty stores no weight.
- Editing a pet can add, change or clear its weight.
- The owner page shows the weight under each pet, like `4.50 kg`. A pet with no weight shows
  nothing there.

## Edge cases

- **Existing pets have no weight.** They must load, show, edit and save as before. No `null`, no
  `0.00 kg` on the page.
- **Valid range: 0.01 to 999.99 kg, at most two decimals.** Zero, negative, too heavy and `4.567`
  are rejected on the form with a message. Nothing is rounded without telling the user.
- **Tiny pets.** A 50 g hamster is `0.05`. It must work.
- **Not a number.** `heavy` shows a friendly message, not "Failed to convert property value...".
- **Editing.** The edit path copies fields one by one in `PetController.updatePetDetails`. Weight
  must be copied too, or edits are lost without an error.
- **Other languages.** The app speaks 10 languages. `I18nPropertiesSyncTest` fails if a key in
  `messages.properties` is missing from any of the 9 translations.
- **Seed data.** `db/h2/data.sql` and `db/mysql/data.sql` insert pets without column names. A new
  column breaks startup until they're updated ("Column count does not match").
- **Two people editing the same pet.** Last save wins, like every other field today.

## Acceptance criteria, written as tests

| Test name | Given / when / then |
|---|---|
| `addingAPetSavesItsWeight` | When I add a pet with weight 4.5, then it's saved as 4.5 |
| `weightIsOptional` | When I add a pet with no weight, then it's saved with none |
| `editingAPetUpdatesItsWeight` | Given Leo, when I edit his weight to 5.2, then it's saved as 5.2 |
| `editingAPetCanClearItsWeight` | Given Iggy at 1.2, when I clear the field, then he has no weight |
| `rejectsAWeightOutsideTheRange` | When I enter 0, -1, 1000 or 4.567, then the form shows an error on weight |
| `rejectsAWeightThatIsNotANumberWithAFriendlyMessage` | When I enter "heavy", then I see a message, not a stack trace |
| `ownerPageShowsTheWeightInKilograms` | Given Basil at 0.05, then his owner's page shows `0.05 kg` |
| `ownerPageShowsNothingForAPetWithNoWeight` | Given pets with no weight, then the page has no `null` and no `0.00 kg` |

The tests run through the whole app and the H2 database, not mocks. That's on purpose. In the real
app the edit form loads the pet and the owner separately, so the pet is two different objects. In a
mock-based `@WebMvcTest` both come from the same stub, so a lost edit still passes. The full test
class is at the end.

## Constraints

- **Follow the existing patterns.**
  - Validate in `PetValidator`. `PetController` sets it as the form's validator with
    `@InitBinder("pet")`, so Bean Validation annotations like `@Positive` on `Pet` don't run on the
    form. Hibernate still checks them when saving, and the user gets a 500 error page.
  - No service layer. Save through `OwnerRepository`, like the rest of the pet code.
  - Reuse the `fragments/inputField` template. It only knows `text` and `date`, so add `number`.
- **Store it as `BigDecimal`**, `DECIMAL(5,2)`. Not `double`.
- **Change all three databases.** `schema.sql` and `data.sql` for H2, MySQL and Postgres.
- **Add the new message keys to every `messages*.properties` file.**
- **Don't change existing tests.** No new dependencies. No migration tool.
- **Run `./mvnw spring-javaformat:apply` before you test.** The build checks formatting.
- **Done means:** `PetWeightTests` pass, the full suite passes, you saw it work in the browser, and
  you said what you checked and what you didn't.

## Out of scope

- Weight history, charts, and weight per visit
- Pounds or any unit other than kg
- A REST API. PetClinic has no pet API. The only JSON endpoint is `/vets`.
- Upgrading MySQL databases that already exist (see questions)

## Questions to resolve

| Question | Who decides | Answer |
|---|---|---|
| "Track health over time": a history, or the current weight? | Clinic (product owner) | Current weight now. History is the next story, probably as a weight on each visit. |
| Pounds too? | Clinic | No. kg only. The label says so. |
| What's the top of the range? | Clinic and dev | 999.99 kg. It covers every pet type PetClinic has. |
| Existing MySQL and Postgres databases use `CREATE TABLE IF NOT EXISTS`, so they won't get the new column. What then? | Dev | Postgres: add `ALTER TABLE pets ADD COLUMN IF NOT EXISTS weight NUMERIC(5,2);` to its `schema.sql`. MySQL: a manual step, written in the PR description. Demo databases are throwaway. |
| Who translates "Weight" into 9 languages? | Clinic | English for now, and open an issue for translators. |

## The tests

Save as `src/test/java/org/springframework/samples/petclinic/owner/PetWeightTests.java`. Run
`./mvnw spring-javaformat:apply`, then `./mvnw test -Dtest=PetWeightTests`. Before the feature
exists it compiles and 10 of 11 tests fail. `ownerPageShowsNothingForAPetWithNoWeight` passes from
the start. It guards the old behavior.

```java
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
```
