package io.github.chrimle.classforge;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import io.github.chrimle.classforge.test.utils.TestConstants;
import java.io.IOException;
import java.nio.file.Path;
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
    return loadedClass;
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
