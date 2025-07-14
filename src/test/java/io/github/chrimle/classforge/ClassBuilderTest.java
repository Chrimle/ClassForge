package io.github.chrimle.classforge;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ClassBuilderTest {

  public static final String ABSOLUTE_PATH_PREFIX = "target/generated-test-sources";
  public static final String PACKAGE_NAME = "io.github.chrimle.classforge";
  public static final String PACKAGE_NAME_DIRECTORY = PACKAGE_NAME.replace(".", "/");

  @Test
  void test() throws Exception {
    final Path outputDir = Path.of(ABSOLUTE_PATH_PREFIX);
    new ClassBuilder(outputDir.toString(), PACKAGE_NAME, "ClassName").build();

    JavaSourceCompiler.compile(outputDir.resolve(PACKAGE_NAME_DIRECTORY + "/ClassName.java"));

    final Class<?> clazz = DynamicClassLoader.loadClass(outputDir, PACKAGE_NAME + ".ClassName");
    assertNotNull(clazz);
  }

  @Nested
  class AbsolutePathPrefixTests {

    @ParameterizedTest
    @NullSource
    void testInvalidValues(final String absolutePathPrefix) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () ->
                  new ClassBuilder(
                          absolutePathPrefix, PACKAGE_NAME, "TestInvalidAbsolutePathPrefix")
                      .build());
      assertEquals("`absolutePathPrefix` MUST NOT be `null`!", exception.getMessage());
    }
  }

  @Nested
  class PackageNameTests {

    /**
     * A null/empty packageName should be allowed, as it should be treated as not belonging to any
     * package.
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "")
    void testNullValue(final String packageName) {
      assertDoesNotThrow(
          () -> new ClassBuilder(ABSOLUTE_PATH_PREFIX, packageName, "TestValidPackageNameTest"));
    }

    @ParameterizedTest
    @ValueSource(strings = {".", "..", "a..", "..a", ".a.", "a..a"})
    void testInvalidValues(final String packageName) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () ->
                  new ClassBuilder(
                      ABSOLUTE_PATH_PREFIX, packageName, "TestInvalidPackageNameTest"));
      assertEquals(
          "`packageName` MUST match the RegEx: " + ClassBuilder.PACKAGE_NAME_REGEX,
          exception.getMessage());
    }
  }
}
