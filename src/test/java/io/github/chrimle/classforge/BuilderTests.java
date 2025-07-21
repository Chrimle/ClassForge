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

import static io.github.chrimle.classforge.test.utils.TestConstants.DIRECTORY;
import static io.github.chrimle.classforge.test.utils.TestConstants.PACKAGE_NAME;
import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.Builder.VersionPlacement;
import io.github.chrimle.classforge.semver.SemVer;
import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import io.github.chrimle.classforge.test.utils.TestConstants;
import io.github.chrimle.classforge.utils.ExceptionFactory;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BuilderTests {

  static <T extends AbstractBuilder<?>> T instantiateBuilder(final Class<T> builderClass) {
    if (builderClass == ClassBuilder.class) {
      return builderClass.cast(ClassBuilder.newClass());
    }
    if (builderClass == EnumBuilder.class) {
      return builderClass.cast(EnumBuilder.newClass());
    }
    throw new UnsupportedOperationException();
  }

  static Class<?> compileAndLoadClass(final String packageName, final String className)
      throws Exception {
    final String fullyQualifiedName = String.join(".", packageName, className);
    compileClass(fullyQualifiedName);
    return loadClass(fullyQualifiedName);
  }

  static void compileClass(final String fullyQualifiedName) throws IOException {
    JavaSourceCompiler.compile(
        Path.of(TestConstants.DIRECTORY).resolve(fullyQualifiedName.replace(".", "/") + ".java"));
  }

  static Class<?> loadClass(final String fullyQualifiedName) throws Exception {
    return DynamicClassLoader.loadClass(Path.of(TestConstants.DIRECTORY), fullyQualifiedName);
  }

  @ParameterizedTest
  @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
  void testCommittingClassWithoutClassName(final Class<? extends AbstractBuilder<?>> builderClass) {
    final var exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                instantiateBuilder(builderClass).updateDirectory(TestConstants.DIRECTORY).commit());
    assertEquals(
        ExceptionFactory.notMatchingRegExException("className", ClassForge.VALID_CLASS_NAME_REGEX)
            .getMessage(),
        exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
  void testCommittingClassWithoutDirectory(final Class<? extends AbstractBuilder<?>> builderClass) {
    final var exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                instantiateBuilder(builderClass).updateClassName("ClassWithoutDirectory").commit());
    assertEquals(ExceptionFactory.nullException("directory").getMessage(), exception.getMessage());
  }

  @Nested
  class DirectoryTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testInvalidValues(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).updateDirectory(null));
      assertEquals(
          ExceptionFactory.nullException("directory").getMessage(), exception.getMessage());
    }
  }

  @Nested
  class SemVerTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testNullSemVerThrows(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).setSemVer(null));
      assertEquals(ExceptionFactory.nullException("semVer").getMessage(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testValid(final Class<? extends AbstractBuilder<?>> builderClass) throws Exception {
      final var className = builderClass.getSimpleName() + "_Test_CustomSemVer";
      final var classBuilder =
          instantiateBuilder(builderClass)
              .updateDirectory(DIRECTORY)
              .updatePackageName(PACKAGE_NAME)
              .updateClassName(className)
              .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION);
      assertDoesNotThrow(() -> classBuilder.setSemVer(new SemVer(42, 7, 10)));
      assertDoesNotThrow(() -> classBuilder.commit(SemVer.Change.PATCH));

      assertNotNull(compileAndLoadClass(PACKAGE_NAME + ".v42_7_11", className));
    }
  }

  @Nested
  class CommitTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testNullChange(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class, () -> instantiateBuilder(builderClass).commit(null));
      assertEquals(ExceptionFactory.nullException("change").getMessage(), exception.getMessage());
    }
  }

  @Nested
  class VersionPlacementTests {

    @Nested
    class CompletePackageNameTests {

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testDefaultCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_DefaultCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit() // Version 1.0.0
            .commit() // Version 2.0.0
            .commit(); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3_0_0", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMajorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MajorCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MAJOR) // Version 1.0.0
            .commit(SemVer.Change.MAJOR) // Version 2.0.0
            .commit(SemVer.Change.MAJOR); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3_0_0", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMinorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MinorCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MINOR) // Version 0.1.0
            .commit(SemVer.Change.MINOR) // Version 0.2.0
            .commit(SemVer.Change.MINOR); // Version 0.3.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_1_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_2_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_3_0", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testPatchCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_PatchCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.PATCH) // Version 0.0.1
            .commit(SemVer.Change.PATCH) // Version 0.0.2
            .commit(SemVer.Change.PATCH); // Version 0.0.3

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_3", className));
      }
    }

    @Nested
    class ShortPackageNameTests {

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testDefaultCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_DefaultShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit() // Version 1.0.0
            .commit() // Version 2.0.0
            .commit(); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMajorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MajorShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MAJOR) // Version 1.0.0
            .commit(SemVer.Change.MAJOR) // Version 2.0.0
            .commit(SemVer.Change.MAJOR); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMinorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MinorShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MINOR) // Version 0.1.0
            .commit(SemVer.Change.MINOR) // Version 0.2.0
            .commit(SemVer.Change.MINOR); // Version 0.3.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_3", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testPatchCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_PatchShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.PATCH) // Version 0.0.1
            .commit(SemVer.Change.PATCH) // Version 0.0.2
            .commit(SemVer.Change.PATCH); // Version 0.0.3

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_3", className));
      }
    }
  }
}
