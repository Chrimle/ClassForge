package io.github.chrimle.classforge;

import java.util.Optional;

/**
 * Builder of Java {@code enum} classes.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public final class EnumBuilder extends AbstractBuilder {

  private EnumBuilder() {}

  static Builder newClass() {
    return new EnumBuilder();
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

        }
        """
            .formatted(className));

    return codeBuilder.toString();
  }
}
