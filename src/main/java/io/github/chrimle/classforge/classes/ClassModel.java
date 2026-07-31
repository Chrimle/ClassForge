/*
 * Copyright 2025-2026 Chrimle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.chrimle.classforge.classes;

import io.github.chrimle.classforge.Model;
import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

/**
 * Represents a {@code class}-class.
 *
 * @param packageName of the class.
 * @param className of the class.
 * @since 0.11.0
 */
@API(status = API.Status.INTERNAL, since = "0.11.0")
public record ClassModel(@Nullable String packageName, String className) implements Model {

  /**
   * Constructor.
   *
   * @param packageName of the class.
   * @param className of the class.
   * @since 0.11.0
   */
  public ClassModel {
    if (packageName != null && packageName.trim().strip().isBlank()) {
      packageName = null;
    }
  }
}
