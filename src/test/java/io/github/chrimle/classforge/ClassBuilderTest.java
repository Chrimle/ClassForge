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
    assertDoesNotThrow(classBuilder::commit);

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

    assertDoesNotThrow(classBuilder::commit);

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
              () -> ClassBuilder.newClass().updateClassName(className));
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
              () -> ClassBuilder.newClass().updateClassName(className));
      assertEquals(
          "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX,
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
    }
  }
}
