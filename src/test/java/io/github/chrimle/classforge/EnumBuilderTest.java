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

import static io.github.chrimle.classforge.test.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.semver.SemVer;
import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import io.github.chrimle.classforge.test.utils.TestConstants;
import io.github.chrimle.classforge.utils.ExceptionFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class EnumBuilderTest {

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
    assertTrue(loadedClass.isEnum());
    return loadedClass;
  }

  @ParameterizedTest
  @ValueSource(strings = {"EnumClassName"})
  void testCreatingClass(final String className) throws Exception {
    EnumBuilder.newClass()
        .updateDirectory(TestConstants.DIRECTORY)
        .updatePackageName(TestConstants.PACKAGE_NAME)
        .updateClassName(className)
        .commit();

    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, className));
  }

  @Test
  void testRenamingUncommittedClass() throws Exception {
    final var classBuilder =
        EnumBuilder.newClass()
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName("OriginalNamedEnumClass");
    assertDoesNotThrow(() -> classBuilder.updateClassName("RenamedEnumClass"));
    assertDoesNotThrow(() -> classBuilder.commit());

    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, "RenamedEnumClass"));
    assertThrows(
        Exception.class,
        () -> compileAndLoadClass(TestConstants.PACKAGE_NAME, "OriginalNamedEnumClass"));
  }

  @Test
  void testCommittingTwiceWithoutChanges() {
    final var className = "EnumClassTwiceCommitted";
    final var classBuilder =
        assertDoesNotThrow(
            () ->
                EnumBuilder.newClass()
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
    EnumBuilder.newClass()
        .updateDirectory(TestConstants.DIRECTORY)
        .updatePackageName(TestConstants.PACKAGE_NAME)
        .updateClassName("OriginalCommittedEnumClass")
        .commit()
        .updateClassName("RenamedUncommittedEnumClass")
        .commit();

    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, "OriginalCommittedEnumClass"));
    assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME, "RenamedUncommittedEnumClass"));
  }

  @Nested
  class ConstantNameTests {

    @ParameterizedTest
    @MethodSource(METHOD_SOURCE_RESERVED_KEYWORDS)
    void testReservedKeywordAsConstantName(final String reservedKeyword) {
      final var enumBuilder = EnumBuilder.newClass();
      final var exception =
          assertThrows(
              IllegalArgumentException.class, () -> enumBuilder.addEnumConstants(reservedKeyword));
      assertEquals(
          ExceptionFactory.reservedJavaKeywordException("enumConstantName").getMessage(),
          exception.getMessage());
    }
  }

  @Nested
  class PackageNameTests {

    /**
     * A null/empty packageName should be allowed, as it should be treated as not belonging to any
     * package.
     */
    @ParameterizedTest
    @CsvSource({",'EnumClassWithNullPackageName'", "'','EnumClassWithEmptyPackageName'"})
    void testNullValue(final String packageName, final String className) throws Exception {
      assertDoesNotThrow(
          () ->
              EnumBuilder.newClass()
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
              () -> EnumBuilder.newClass().updatePackageName(packageName));
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
              () -> EnumBuilder.newClass().updateClassName(className));
      assertEquals(
          ExceptionFactory.notMatchingRegExException("className", ClassForge.VALID_CLASS_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource(METHOD_SOURCE_RESERVED_KEYWORDS)
    void testReservedKeywords(final String className) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().updateClassName(className));
      assertEquals(
          ExceptionFactory.notMatchingRegExException("className", ClassForge.VALID_CLASS_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }
  }

  @Nested
  class EnumConstantNameTests {

    @Test
    void testEmptyEnumConstantNames() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class, () -> EnumBuilder.newClass().addEnumConstants());
      assertEquals(
          ExceptionFactory.nullOrEmptyException("enumConstantNames").getMessage(),
          exception.getMessage());
    }

    @Test
    void testNullEnumConstantName() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().addEnumConstants((String) null));
      assertEquals(
          ExceptionFactory.nullOrEmptyException("enumConstantNames").getMessage(),
          exception.getMessage());
    }

    @Test
    void testNullEnumConstantNames() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().addEnumConstants("Valid", null));
      assertEquals(
          ExceptionFactory.nullOrEmptyException("enumConstantNames").getMessage(),
          exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", ".", "_", "1", "1_"})
    void testInvalidEnumConstantNames(final String enumConstantName) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().addEnumConstants(enumConstantName));
      assertEquals(
          ExceptionFactory.notMatchingRegExException(
                  "enumConstantName", EnumBuilder.VALID_ENUM_CONSTANT_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }

    @Test
    void testDuplicateEnumConstantNames() {
      final var enumBuilder = EnumBuilder.newClass();
      assertDoesNotThrow(
          () -> enumBuilder.addEnumConstants("FIRST"),
          "Constant 'FIRST' could not be added the first time!");
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> enumBuilder.addEnumConstants("FIRST"));
      assertEquals(
          ExceptionFactory.alreadyExistsException("enum constant", "FIRST").getMessage(),
          exception.getMessage());
    }

    @Test
    void testValidEnumConstantNames() throws Exception {
      final var expectedEnumConstants =
          List.of("_a", "__a", "__9", "TEST", "Test_1_", "Test_1_1", "O__0");
      final var enumBuilder = EnumBuilder.newClass();
      for (final var enumConstant : expectedEnumConstants) {
        enumBuilder.addEnumConstants(enumConstant);
      }
      enumBuilder
          .updateClassName("EnumClassWithValidConstants")
          .updateDirectory(TestConstants.DIRECTORY)
          .updatePackageName(TestConstants.PACKAGE_NAME)
          .commit();

      final var enumClass =
          compileAndLoadClass(TestConstants.PACKAGE_NAME, "EnumClassWithValidConstants");
      assertTrue(enumClass.isEnum());
      final var enumConstants =
          Arrays.stream(enumClass.getEnumConstants())
              .map(s -> (Enum<?>) s)
              .map(Enum::name)
              .toList();
      assertEquals(expectedEnumConstants, enumConstants);
    }

    @Test
    void testRemoveEmptyEnumConstantNames() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class, () -> EnumBuilder.newClass().removeEnumConstants());
      assertEquals(
          ExceptionFactory.nullOrEmptyException("enumConstantNames").getMessage(),
          exception.getMessage());
    }

    @Test
    void testRemoveNullEnumConstantName() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().removeEnumConstants((String) null));
      assertEquals(
          ExceptionFactory.nullOrEmptyException("enumConstantNames").getMessage(),
          exception.getMessage());
    }

    @Test
    void testRemoveNullEnumConstantNames() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().removeEnumConstants("Valid", null));
      assertEquals(
          ExceptionFactory.nullOrEmptyException("enumConstantNames").getMessage(),
          exception.getMessage());
    }

    @Test
    void testRemoveNonExistingEnumConstant() {
      final var enumBuilder = EnumBuilder.newClass();
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> enumBuilder.removeEnumConstants("DoesNotExist"));
      assertEquals(
          ExceptionFactory.doesNotExistException("enum constant", "DoesNotExist").getMessage(),
          exception.getMessage());
    }

    @Test
    void testRemoveExistingEnumConstant() throws Exception {
      final var enumBuilder =
          EnumBuilder.newClass()
              .updateDirectory(TestConstants.DIRECTORY)
              .updatePackageName(TestConstants.PACKAGE_NAME)
              .updateClassName("TestRemovingExistingEnumConstant")
              .addEnumConstants("ConstantToKeep")
              .addEnumConstants("ConstantToRemove");
      assertDoesNotThrow(() -> enumBuilder.removeEnumConstants("ConstantToRemove"));
      enumBuilder.commit();

      final var enumClass =
          compileAndLoadClass(TestConstants.PACKAGE_NAME, "TestRemovingExistingEnumConstant");
      assertTrue(enumClass.isEnum());
      assertEquals(1, enumClass.getEnumConstants().length);
      Enum<?> enumConstant = (Enum<?>) enumClass.getEnumConstants()[0];
      assertEquals("ConstantToKeep", enumConstant.name());
    }

    @Test
    void testRemoveMultipleExistingEnumConstant() throws Exception {
      final var enumBuilder =
          EnumBuilder.newClass()
              .updateDirectory(TestConstants.DIRECTORY)
              .updatePackageName(TestConstants.PACKAGE_NAME)
              .updateClassName("TestRemovingMultipleExistingEnumConstant")
              .addEnumConstants("ConstantToKeep")
              .addEnumConstants("AnotherConstantToKeep")
              .addEnumConstants("ConstantToRemove")
              .addEnumConstants("AnotherConstantToRemove");
      assertDoesNotThrow(
          () -> enumBuilder.removeEnumConstants("ConstantToRemove", "AnotherConstantToRemove"));
      enumBuilder.commit();

      final var enumClass =
          compileAndLoadClass(
              TestConstants.PACKAGE_NAME, "TestRemovingMultipleExistingEnumConstant");
      assertTrue(enumClass.isEnum());
      assertEquals(
          List.of("ConstantToKeep", "AnotherConstantToKeep"),
          Arrays.stream(enumClass.getEnumConstants())
              .map(e -> (Enum<?>) e)
              .map(Enum::name)
              .toList());
    }

    @Nested
    class UpdateEnumConstantTests {

      @Test
      void testNullOldEnumConstant() {
        final var enumBuilder = EnumBuilder.newClass();
        final var exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> enumBuilder.updateEnumConstant(null, "ignored"));
        assertEquals(
            ExceptionFactory.nullException("oldEnumConstant").getMessage(), exception.getMessage());
      }

      @Test
      void testNonExistingOldEnumConstant() {
        final var enumBuilder = EnumBuilder.newClass();
        final var exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> enumBuilder.updateEnumConstant("not_existing", "ignored"));
        assertEquals(
            ExceptionFactory.doesNotExistException("enum constant", "not_existing").getMessage(),
            exception.getMessage());
      }

      @Test
      void testNullNewEnumConstant() {
        final var enumBuilder = EnumBuilder.newClass().addEnumConstants("existing");
        final var exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> enumBuilder.updateEnumConstant("existing", null));
        assertEquals(
            ExceptionFactory.nullException("newEnumConstant").getMessage(), exception.getMessage());
      }

      @Test
      void testInvalidNewEnumConstant() {
        final var enumBuilder = EnumBuilder.newClass().addEnumConstants("existing");
        final var exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> enumBuilder.updateEnumConstant("existing", "?"));
        assertEquals(
            ExceptionFactory.notMatchingRegExException(
                    "enumConstantName", EnumBuilder.VALID_ENUM_CONSTANT_NAME_REGEX)
                .getMessage(),
            exception.getMessage());
      }

      @ParameterizedTest
      @MethodSource(METHOD_SOURCE_RESERVED_KEYWORDS)
      void testReservedKeywordNewEnumConstant(final String reservedKeyword) {
        final var enumBuilder = EnumBuilder.newClass().addEnumConstants("existing");
        final var exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> enumBuilder.updateEnumConstant("existing", reservedKeyword));
        assertEquals(
            ExceptionFactory.reservedJavaKeywordException("enumConstantName").getMessage(),
            exception.getMessage());
      }

      @Test
      void testExistingNewEnumConstant() {
        final var enumBuilder =
            EnumBuilder.newClass().addEnumConstants("existing", "another_existing");
        final var exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> enumBuilder.updateEnumConstant("existing", "another_existing"));
        assertEquals(
            ExceptionFactory.alreadyExistsException("enum constant", "another_existing")
                .getMessage(),
            exception.getMessage());
      }

      @Test
      void testValidUpdate() throws Exception {
        final var enumBuilder =
            EnumBuilder.newClass()
                .updateDirectory(DIRECTORY)
                .updatePackageName(PACKAGE_NAME)
                .updateClassName("EnumWithUpdatedConstant")
                .addEnumConstants("OLD");
        assertDoesNotThrow(() -> enumBuilder.updateEnumConstant("OLD", "NEW"));
        enumBuilder.commit();
        final Class<?> enumWithUpdatedConstant =
            compileAndLoadClass(PACKAGE_NAME, "EnumWithUpdatedConstant");
        assertTrue(enumWithUpdatedConstant.isEnum());
        assertEquals(1, enumWithUpdatedConstant.getEnumConstants().length);
        assertEquals("NEW", ((Enum<?>) enumWithUpdatedConstant.getEnumConstants()[0]).name());
      }
    }
  }

  @Nested
  class VersionPlacementTests {

    @Nested
    class SetSemVerTests {

      @Test
      void testValidSemVer() throws Exception {
        final var enumBuilder =
            EnumBuilder.newClass()
                .updateDirectory(DIRECTORY)
                .updatePackageName(PACKAGE_NAME)
                .updateClassName("EnumWithCustomSemVer")
                .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION);
        assertDoesNotThrow(() -> enumBuilder.setSemVer(new SemVer(42, 7, 11)));
        assertDoesNotThrow(() -> enumBuilder.commit());

        final Class<?> enumWithCustomSemVer =
            compileAndLoadClass(PACKAGE_NAME + ".v43_0_0", "EnumWithCustomSemVer");
        assertTrue(enumWithCustomSemVer.isEnum());
      }
    }

    @Nested
    class CompletePackageNameTests {

      @Test
      void testUpdatePatch() throws Exception {
        final var className = "EnumUpdatePatchComplete";
        EnumBuilder.newClass()
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
        final var className = "EnumTestUpdatingPackageVersionTwiceShortened";
        EnumBuilder.newClass()
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
        final var className = "EnumUpdateMajorShort";
        EnumBuilder.newClass()
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
        final var className = "EnumUpdateMinorShort";
        EnumBuilder.newClass()
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
        final var className = "EnumUpdatePatchShort";
        EnumBuilder.newClass()
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
