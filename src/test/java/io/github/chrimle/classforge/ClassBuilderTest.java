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

import io.github.chrimle.classforge.semver.SemVer;
import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import io.github.chrimle.classforge.test.utils.TestConstants;
import io.github.chrimle.classforge.utils.ExceptionFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ClassBuilderTest {

  static Class<?> compileAndLoadClass(final String className) throws Exception {
    compileClass(className);
    return loadClass(className);
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
    final var loadedClass =
        DynamicClassLoader.loadClass(Path.of(TestConstants.DIRECTORY), fullyQualifiedName);
    assertFalse(loadedClass.isEnum());
    assertFalse(loadedClass.isRecord());
    return loadedClass;
  }

  @ParameterizedTest
  @ValueSource(strings = {"ClassName"})
  void testCreatingClass(final String className) throws Exception {
    ClassBuilder.newClass()
        .updateDirectory(TestConstants.DIRECTORY)
        .updatePackageName(TestConstants.PACKAGE_NAME)
        .updateClassName(className)
        .commit();

    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, className));
  }

  @Test
  void testRenamingUncommittedClass() throws Exception {
    final var classBuilder =
        ClassBuilder.newClass()
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName("OriginalNamedClass");
    assertDoesNotThrow(() -> classBuilder.updateClassName("RenamedClass"));
    assertDoesNotThrow(() -> classBuilder.commit());

    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, "RenamedClass"));
    assertThrows(
        Exception.class,
        () -> compileAndLoadClass(TestConstants.PACKAGE_NAME, "OriginalNamedClass"));
  }

  @Test
  void testCommittingTwiceWithoutChanges() {
    final var className = "ClassTwiceCommitted";
    final var classBuilder =
        assertDoesNotThrow(
            () ->
                ClassBuilder.newClass()
                    .updateDirectory(TestConstants.DIRECTORY)
                    .updatePackageName(TestConstants.PACKAGE_NAME)
                    .updateClassName(className));

    assertDoesNotThrow(() -> classBuilder.commit());

    final var exception = assertThrows(IllegalStateException.class, classBuilder::commit);
    assertEquals(
        "Class `%s.%s` has already been generated!"
            .formatted(TestConstants.PACKAGE_NAME, className),
        exception.getMessage());
  }

  @Test
  void testRenamingCommittedClass() throws Exception {
    ClassBuilder.newClass()
        .updateDirectory(TestConstants.DIRECTORY)
        .updatePackageName(TestConstants.PACKAGE_NAME)
        .updateClassName("OriginalCommittedClass")
        .commit()
        .updateClassName("RenamedUncommittedClass")
        .commit();

    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, "OriginalCommittedClass"));
    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, "RenamedUncommittedClass"));
  }

  @Nested
  class SetSemVerTests {

    @Test
    void testNullSemVerThrows() {
      final var classBuilder = ClassBuilder.newClass();
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> classBuilder.setSemVer(null));
      assertEquals(ExceptionFactory.nullException("semVer").getMessage(), exception.getMessage());
    }

    @Test
    void testValidSemVer() throws Exception {
      final var classBuilder =
          ClassBuilder.newClass()
              .updateDirectory(DIRECTORY)
              .updatePackageName(PACKAGE_NAME)
              .updateClassName("ClassWithCustomSemVer")
              .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION);
      assertDoesNotThrow(() -> classBuilder.setSemVer(new SemVer(42, 7, 11)));
      assertDoesNotThrow(() -> classBuilder.commit());

      final Class<?> enumWithCustomSemVer =
          compileAndLoadClass(PACKAGE_NAME + ".v43_0_0", "ClassWithCustomSemVer");
      assertFalse(enumWithCustomSemVer.isEnum());
    }
  }

  @Nested
  class PackageNameTests {

    /**
     * A null/empty packageName should be allowed, as it should be treated as not belonging to any
     * package.
     */
    @ParameterizedTest
    @CsvSource({",'ClassWithNullPackageName'", "'','ClassWithEmptyPackageName'"})
    void testNullValue(final String packageName, final String className) throws Exception {
      assertDoesNotThrow(
          () ->
              ClassBuilder.newClass()
                  .updateDirectory(TestConstants.DIRECTORY)
                  .updatePackageName(packageName)
                  .updateClassName(className)
                  .commit());

      assertNotNull(compileAndLoadClass(className));
    }

    @ParameterizedTest
    @ValueSource(strings = {".", "..", "a..", "..a", ".a.", "a..a"})
    void testInvalidValues(final String packageName) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> ClassBuilder.newClass().updatePackageName(packageName));
      assertEquals(
          ExceptionFactory.notMatchingRegExException(
                  "packageName", ClassForge.VALID_PACKAGE_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }
  }

  @Nested
  class ClassNameTests {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", ".", " ", "_", "-", "1", "1a", "A-", "A-A", "1_1"})
    void testInvalidValues(final String className) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> ClassBuilder.newClass().updateClassName(className));
      assertEquals(
          ExceptionFactory.notMatchingRegExException("className", ClassForge.VALID_CLASS_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }

    static Stream<Arguments> testReservedKeywords() {
      return ClassForge.RESERVED_KEYWORDS.stream().map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource
    void testReservedKeywords(final String className) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> ClassBuilder.newClass().updateClassName(className));
      assertEquals(
          ExceptionFactory.notMatchingRegExException("className", ClassForge.VALID_CLASS_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }
  }

  @Nested
  class VersionPlacementTests {

    @Nested
    class CompletePackageNameTests {

      @Test
      void testUpdatingPackageVersionTwice() throws Exception {
        final var className = "ClassTestUpdatingPackageVersionTwiceComplete";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit() // Version 1.0.0
            .commit() // Version 2.0.0
            .commit(); // Version 3.0.0

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1_0_0", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2_0_0", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3_0_0", className);
      }

      @Test
      void testUpdateMajor() throws Exception {
        final var className = "ClassUpdateMajorComplete";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MAJOR) // Version 1.0.0
            .commit(SemVer.Change.MAJOR) // Version 2.0.0
            .commit(SemVer.Change.MAJOR); // Version 3.0.0

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1_0_0", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2_0_0", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3_0_0", className);
      }

      @Test
      void testUpdateMinor() throws Exception {
        final var className = "ClassUpdateMinorComplete";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MINOR) // Version 0.1.0
            .commit(SemVer.Change.MINOR) // Version 0.2.0
            .commit(SemVer.Change.MINOR); // Version 0.3.0

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_1_0", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_2_0", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_3_0", className);
      }

      @Test
      void testUpdatePatch() throws Exception {
        final var className = "ClassUpdatePatchComplete";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.PATCH) // Version 0.0.1
            .commit(SemVer.Change.PATCH) // Version 0.0.2
            .commit(SemVer.Change.PATCH); // Version 0.0.3

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_1", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_2", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_3", className);
      }
    }

    @Nested
    class ShortenedPackageNameTests {

      @Test
      void testUpdatingPackageVersionTwice() throws Exception {
        final var className = "ClassTestUpdatingPackageVersionTwiceShortened";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit() // Version 1
            .commit() // Version 2
            .commit(); // Version 3

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3", className);
      }

      @Test
      void testUpdateMajor() throws Exception {
        final var className = "ClassUpdateMajorShort";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MAJOR) // Version 1.0.0
            .commit(SemVer.Change.MAJOR) // Version 2.0.0
            .commit(SemVer.Change.MAJOR); // Version 3.0.0

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3", className);
      }

      @Test
      void testUpdateMinor() throws Exception {
        final var className = "ClassUpdateMinorShort";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.MINOR) // Version 0.1.0
            .commit(SemVer.Change.MINOR) // Version 0.2.0
            .commit(SemVer.Change.MINOR); // Version 0.3.0

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_1", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_2", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_3", className);
      }

      @Test
      void testUpdatePatch() throws Exception {
        final var className = "ClassUpdatePatchShort";
        ClassBuilder.newClass()
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(SemVer.Change.PATCH) // Version 0.0.1
            .commit(SemVer.Change.PATCH) // Version 0.0.2
            .commit(SemVer.Change.PATCH); // Version 0.0.3

        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_1", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_2", className);
        compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_3", className);
      }
    }
  }
}
