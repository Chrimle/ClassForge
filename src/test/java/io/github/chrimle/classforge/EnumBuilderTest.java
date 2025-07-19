package io.github.chrimle.classforge;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import io.github.chrimle.classforge.test.utils.TestConstants;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
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
    assertDoesNotThrow(classBuilder::commit);

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

    assertDoesNotThrow(classBuilder::commit);

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
          "`packageName` MUST match the RegEx: " + ClassForge.VALID_PACKAGE_NAME_REGEX,
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
          "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX,
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
              () -> EnumBuilder.newClass().updateClassName(className));
      assertEquals(
          "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX,
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
      assertEquals("`enumConstantNames` MUST NOT be null or empty!", exception.getMessage());
    }

    @Test
    void testNullEnumConstantName() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().addEnumConstants((String) null));
      assertEquals("`enumConstantNames` MUST NOT be null or empty!", exception.getMessage());
    }

    @Test
    void testNullEnumConstantNames() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().addEnumConstants("Valid", null));
      assertEquals("`enumConstantNames` MUST NOT be null or empty!", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", ".", "_", "1", "1_"})
    void testInvalidEnumConstantNames(final String enumConstantName) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().addEnumConstants(enumConstantName));
      assertEquals(
          "`enumConstantName` MUST match the RegEx: " + EnumBuilder.VALID_ENUM_CONSTANT_NAME_REGEX,
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
      assertEquals("An Enum constant named 'FIRST' already exists!", exception.getMessage());
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
      assertEquals("`enumConstantNames` MUST NOT be null or empty!", exception.getMessage());
    }

    @Test
    void testRemoveNullEnumConstantName() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().removeEnumConstants((String) null));
      assertEquals("`enumConstantNames` MUST NOT be null or empty!", exception.getMessage());
    }

    @Test
    void testRemoveNullEnumConstantNames() {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().removeEnumConstants("Valid", null));
      assertEquals("`enumConstantNames` MUST NOT be null or empty!", exception.getMessage());
    }

    @Test
    void testRemoveNonExistingEnumConstant() {
      final var enumBuilder = EnumBuilder.newClass();
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> enumBuilder.removeEnumConstants("DoesNotExist"));
      assertEquals("No Enum constant named 'DoesNotExist' exists!", exception.getMessage());
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
  }

  @Nested
  class VersionPlacementTests {

    @Nested
    class PackageNameTests {

      @Test
      void testUpdatingPackageVersionTwice() throws Exception {
        final var className = "EnumTestUpdatingPackageVersionTwice";
        EnumBuilder.newClass()
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
    }
  }
}
