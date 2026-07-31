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

package io.github.chrimle.classforge.enums;

import io.github.chrimle.classforge.Model;
import java.util.List;
import org.apiguardian.api.API;

/**
 * Represents an {@code enum}-class.
 *
 * @param packageName of the enum class.
 * @param className of the enum class.
 * @param enumConstants of the enum class.
 * @param <ValueType> of the enum class.
 * @since 0.11.0
 */
@API(status = API.Status.INTERNAL, since = "0.11.0")
public record EnumModel<ValueType>(
    String packageName, String className, List<EnumConstantModel<ValueType>> enumConstants)
    implements Model {

  /**
   * Constructor.
   *
   * @param packageName of the enum class.
   * @param className of the enum class.
   * @param enumConstants of the enum class.
   * @since 0.11.0
   */
  public EnumModel {
    if (packageName != null && packageName.trim().strip().isBlank()) {
      packageName = null;
    }
  }

  /**
   * Represents an {@code enum}-<i>constant</i>.
   *
   * @param name of the constant
   * @param <ValueType> of the {@code enum}.
   * @since 0.11.0
   */
  public record EnumConstantModel<ValueType>(String name) {}
}
