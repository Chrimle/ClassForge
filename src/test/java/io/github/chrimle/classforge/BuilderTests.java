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

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.test.utils.TestConstants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BuilderTests {

  static <T extends AbstractBuilder> T instantiateBuilder(final Class<T> builderClass) {
    if (builderClass == ClassBuilder.class) {
      return builderClass.cast(ClassBuilder.newClass());
    }
    if (builderClass == EnumBuilder.class) {
      return builderClass.cast(EnumBuilder.newClass());
    }
    throw new UnsupportedOperationException();
  }

  @ParameterizedTest
  @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
  void testCommittingClassWithoutClassName(final Class<? extends AbstractBuilder> builderClass) {
    final var exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                instantiateBuilder(builderClass).updateDirectory(TestConstants.DIRECTORY).commit());
    assertEquals(
        "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX,
        exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
  void testCommittingClassWithoutDirectory(final Class<? extends AbstractBuilder> builderClass) {
    final var exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                instantiateBuilder(builderClass).updateClassName("ClassWithoutDirectory").commit());
    assertEquals("`directory` MUST NOT be `null`!", exception.getMessage());
  }

  @Nested
  class DirectoryTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testInvalidValues(final Class<? extends AbstractBuilder> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).updateDirectory(null));
      assertEquals("`directory` MUST NOT be `null`!", exception.getMessage());
    }
  }
}
