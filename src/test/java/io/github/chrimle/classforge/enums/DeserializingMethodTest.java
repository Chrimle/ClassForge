package io.github.chrimle.classforge.enums;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DeserializingMethodTest {

  static Stream<Arguments> test() {
    return Stream.of(
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.NULL,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return null;
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.FALLBACK,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return DEFAULT;
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.NULL,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                return null;
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.NULL,
            NullStrategy.NULL,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return null;
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                return null;
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.NULL,
            NullStrategy.FALLBACK,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return DEFAULT;
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                return null;
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.FALLBACK,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                return DEFAULT;
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.FALLBACK,
            NullStrategy.NULL,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return null;
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                return DEFAULT;
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.FALLBACK,
            NullStrategy.FALLBACK,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return DEFAULT;
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                return DEFAULT;
              }
            """),
        Arguments.of(
            MatchingStrategy.EXACT_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                final var enumConstant = valueOf(value);
                if (enumConstant != null) {
                  return enumConstant;
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.NULL,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return null;
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.FALLBACK,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return DEFAULT;
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.NULL,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                return null;
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.NULL,
            NullStrategy.NULL,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return null;
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                return null;
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.NULL,
            NullStrategy.FALLBACK,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return DEFAULT;
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                return null;
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.FALLBACK,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                return DEFAULT;
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.FALLBACK,
            NullStrategy.NULL,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return null;
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                return DEFAULT;
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.FALLBACK,
            NullStrategy.FALLBACK,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  return DEFAULT;
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                return DEFAULT;
              }
            """),
        Arguments.of(
            MatchingStrategy.CASE_INSENSITIVE_NAME,
            NoMatchStrategy.THROW,
            NullStrategy.THROW,
            """
              public static Example fromValue(final String value) {
                if (value == null) {
                  throw new NullPointerException("`value` cannot be `null`");
                }
                for (final var enumConstant : values()) {
                  if (enumConstant.name().equalsIgnoreCase(value)) {
                    return enumConstant;
                  }
                }
                throw new IllegalArgumentException("Unexpected value '" + value + "'");
              }
            """));
  }

  @ParameterizedTest
  @MethodSource
  void test(
      final MatchingStrategy matchingStrategy,
      final NoMatchStrategy noMatchStrategy,
      final NullStrategy nullStrategy,
      final String expectedGeneratedCode) {
    final var deserializingMethod =
        new DeserializingMethod(matchingStrategy, noMatchStrategy, nullStrategy);
    final var actualGeneratedCode =
        assertDoesNotThrow(() -> deserializingMethod.generateMethodCode("Example", "DEFAULT"));
    assertEquals(expectedGeneratedCode, actualGeneratedCode);
  }
}
