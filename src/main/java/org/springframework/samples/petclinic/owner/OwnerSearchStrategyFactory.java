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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Looks up the right {@link OwnerSearchStrategy} for a search type. Strategies register
 * themselves automatically, so adding a new search type needs no changes here.
 */
@Component
public class OwnerSearchStrategyFactory {

	private final Map<String, OwnerSearchStrategy> strategies = new HashMap<>();

	public OwnerSearchStrategyFactory(List<OwnerSearchStrategy> strategies) {
		for (OwnerSearchStrategy strategy : strategies) {
			this.strategies.put(strategy.getType(), strategy);
		}
	}

	public OwnerSearchStrategy getStrategy(String type) {
		OwnerSearchStrategy strategy = this.strategies.get(type);
		if (strategy == null) {
			throw new IllegalArgumentException("Unknown search type: " + type);
		}
		return strategy;
	}

}
