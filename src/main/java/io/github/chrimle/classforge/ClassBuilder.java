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

import io.github.chrimle.classforge.semver.SemVer;
import java.util.Optional;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Builder of Java classes ({@code class}).
 *
 * @since 0.1.0
 * @author Chrimle
 */
@API(since = "0.6.0", status = API.Status.STABLE)
public final class ClassBuilder extends AbstractBuilder<ClassBuilder> {

  private ClassBuilder() {}

  @NotNull
  @Contract(" -> new")
  static ClassBuilder newClass() {
    return new ClassBuilder();
  }

  @Contract(value = " -> this", pure = true)
  @Override
  protected ClassBuilder self() {
    return this;
  }

  @NotNull
  @Override
  protected String generateFileContent(final SemVer semVer) {
    final StringBuilder codeBuilder = new StringBuilder();

    Optional.ofNullable(resolveEffectivePackageName(semVer))
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
