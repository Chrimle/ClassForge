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
