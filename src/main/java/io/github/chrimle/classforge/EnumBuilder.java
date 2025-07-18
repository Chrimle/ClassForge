package io.github.chrimle.classforge;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
  private final Set<String> enumConstants = new HashSet<>();

  private EnumBuilder() {}

  static EnumBuilder newClass() {
    return new EnumBuilder();
  }

  /**
   * Adds the {@code enumConstantName} to the <em>currently uncommitted</em> enum class.
   *
   * @param enumConstantName to add.
   * @return this Builder.
   * @since 0.2.0
   */
  public EnumBuilder addEnumConstant(final String enumConstantName) {
    validateEnumConstantName(enumConstantName);
    if (enumConstants.contains(enumConstantName)) {
      throw new IllegalArgumentException(
          "An Enum constant named '%s' already exists!".formatted(enumConstantName));
    }
    enumConstants.add(enumConstantName);
    return this;
  }

  /**
   * Removes the {@code enumConstantName} from the <em>currently uncommitted</em> enum class.
   *
   * @param enumConstantName to remove.
   * @return this Builder.
   * @since 0.3.0
   */
  public EnumBuilder removeEnumConstant(final String enumConstantName) {
    if (!enumConstants.contains(enumConstantName)) {
      throw new IllegalArgumentException(
          "No Enum constant named '%s' exists!".formatted(enumConstantName));
    }
    enumConstants.remove(enumConstantName);
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

    Optional.ofNullable(packageName)
        .filter(pN -> !pN.isBlank())
        .map("package %s;\n\n"::formatted)
        .ifPresent(codeBuilder::append);

    codeBuilder.append(
        """
        public enum %s {
          %s
        }
        """
            .formatted(className, String.join(",\n", enumConstants) + ";"));

    return codeBuilder.toString();
  }
}
