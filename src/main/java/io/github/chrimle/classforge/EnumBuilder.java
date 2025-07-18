package io.github.chrimle.classforge;

import java.util.*;
import java.util.function.Predicate;

/**
 * Builder of Java {@code enum} classes.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public final class EnumBuilder extends AbstractBuilder<EnumBuilder> {

  /**
   * The <em>RegularExpression (RegEx)</em> for determining validity of enum constant-names.
   *
   * @since 0.2.0
   */
  public static final String VALID_ENUM_CONSTANT_NAME_REGEX =
      "^([A-Za-z]|(_+[A-Za-z0-9]))[A-Z_a-z0-9]*$";

  private static final Predicate<String> enumConstantNamePredicate =
      enumConstantName ->
          Optional.ofNullable(enumConstantName)
              .filter(name -> name.matches(VALID_ENUM_CONSTANT_NAME_REGEX))
              .isPresent();
  private final List<String> enumConstants = new ArrayList<>();

  private EnumBuilder() {}

  static EnumBuilder newClass() {
    return new EnumBuilder();
  }

  /**
   * Adds the {@code enumConstantNames} to the <em>currently uncommitted</em> enum class.
   *
   * @param enumConstantNames to add.
   * @return this Builder.
   * @since 0.3.0
   * @throws IllegalArgumentException if {@code enumConstantNames} is {@code null}, empty, or
   *     contains {@link String}s which are {@code null}, does not match the RegEx {@value
   *     VALID_ENUM_CONSTANT_NAME_REGEX} or the <em>currently uncommitted</em> class already has an
   *     enum constant with the same name.
   */
  public EnumBuilder addEnumConstants(final String... enumConstantNames) {

    if (Optional.ofNullable(enumConstantNames)
        .filter(enums -> enums.length >= 1)
        .map(Arrays::stream)
        .filter(stream -> stream.allMatch(Objects::nonNull))
        .isEmpty()) {
      throw new IllegalArgumentException("`enumConstantNames` MUST NOT be null or empty!");
    }

    final var enumNamesList = List.of(enumConstantNames);

    if (new HashSet<>(enumNamesList).size() < enumNamesList.size()) {
      throw new IllegalArgumentException("Duplicate Enum constant names were provided!");
    }

    for (final String enumConstantName : enumConstantNames) {
      validateEnumConstantName(enumConstantName);
      if (enumConstants.contains(enumConstantName)) {
        throw new IllegalArgumentException(
            "An Enum constant named '%s' already exists!".formatted(enumConstantName));
      }
    }
    enumConstants.addAll(enumNamesList);
    return this;
  }

  /**
   * Removes the {@code enumConstantNames} from the <em>currently uncommitted</em> enum class.
   *
   * @param enumConstantNames to remove.
   * @return this Builder.
   * @since 0.3.0
   * @throws IllegalArgumentException if {@code enumConstantNames} is {@code null}, empty, or
   *     contains {@link String}s which are {@code null}, does not match the RegEx {@value
   *     VALID_ENUM_CONSTANT_NAME_REGEX} or the <em>currently uncommitted</em> class does not have
   *     the enum constant to be removed.
   */
  public EnumBuilder removeEnumConstants(final String... enumConstantNames) {
    if (Optional.ofNullable(enumConstantNames)
        .filter(enums -> enums.length >= 1)
        .map(Arrays::stream)
        .filter(stream -> stream.allMatch(Objects::nonNull))
        .isEmpty()) {
      throw new IllegalArgumentException("`enumConstantNames` MUST NOT be null or empty!");
    }

    for (final String enumConstantName : enumConstantNames) {
      if (!enumConstants.contains(enumConstantName)) {
        throw new IllegalArgumentException(
            "No Enum constant named '%s' exists!".formatted(enumConstantName));
      }
    }
    enumConstants.removeAll(List.of(enumConstantNames));
    return this;
  }

  private static void validateEnumConstantName(final String enumConstantName) {
    if (!enumConstantNamePredicate.test(enumConstantName)) {
      throw new IllegalArgumentException(
          "`enumConstantName` MUST match the RegEx: " + VALID_ENUM_CONSTANT_NAME_REGEX);
    }
  }

  @Override
  protected EnumBuilder self() {
    return this;
  }

  @Override
  protected void validateAdditionalPredicates() {}

  @Override
  protected String generateFileContent() {
    final StringBuilder codeBuilder = new StringBuilder();

    Optional.ofNullable(resolveEffectivePackageName())
        .filter(pN -> !pN.isBlank())
        .map("package %s;\n\n"::formatted)
        .ifPresent(codeBuilder::append);

    codeBuilder.append(
        """
        public enum %s {
          %s
        }
        """
            .formatted(className, String.join(",\n\t", enumConstants) + ";"));

    return codeBuilder.toString();
  }
}
