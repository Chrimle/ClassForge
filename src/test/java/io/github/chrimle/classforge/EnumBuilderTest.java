package io.github.chrimle.classforge;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import io.github.chrimle.classforge.test.utils.TestConstants;
import java.io.IOException;
import java.nio.file.Path;
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

  @Test
  void testCommittingClassWithoutClassName() {
    final var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> EnumBuilder.newClass().updateDirectory(TestConstants.DIRECTORY).commit());
    assertEquals(
        "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX,
        exception.getMessage());
  }

  @Test
  void testCommittingClassWithoutDirectory() {
    final var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> EnumBuilder.newClass().updateClassName("ClassWithoutDirectory").commit());
    assertEquals("`directory` MUST NOT be `null`!", exception.getMessage());
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
  class DirectoryTests {

    @ParameterizedTest
    @NullSource
    void testInvalidValues(final String directory) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> EnumBuilder.newClass().updateDirectory(directory));
      assertEquals("`directory` MUST NOT be `null`!", exception.getMessage());
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
}
