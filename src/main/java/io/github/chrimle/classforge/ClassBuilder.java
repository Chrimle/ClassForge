/*
 * Copyright 2025 Chrimle
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
package io.github.chrimle.classforge;

import java.util.Optional;

/**
 * Builder of Java classes ({@code class}).
 *
 * @since 0.1.0
 * @author Chrimle
 */
public final class ClassBuilder extends AbstractBuilder<ClassBuilder> {

  private ClassBuilder() {}

  static ClassBuilder newClass() {
    return new ClassBuilder();
  }

  @Override
  protected ClassBuilder self() {
    return this;
  }

  @Override
  protected void validateAdditionalPredicates() {}

  @Override
  protected String generateFileContent() {
    final StringBuilder codeBuilder = new StringBuilder();

    Optional.ofNullable(resolveEffectivePackageName())
        .filter(pN -> !pN.isBlank())
        .map("package %s;\n\n"::formatted)
        .ifPresent(codeBuilder::append);

    codeBuilder.append(
        """
        public class %s {

        }
        """
            .formatted(className));

    return codeBuilder.toString();
  }
}
