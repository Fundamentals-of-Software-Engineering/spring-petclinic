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

/**
 * Strategy for searching {@link Owner}s. New search types can be plugged in by adding
 * another implementation, which keeps the search feature open for extension.
 */
public interface OwnerSearchStrategy {

	/**
	 * The search type this strategy handles, for example {@code petName}.
	 * @return the search type key
	 */
	String getType();

	/**
	 * Search for owners matching the given term.
	 * @param term the value to search for
	 * @return the matching owners, never {@literal null}
	 */
	List<Owner> search(String term);

}
