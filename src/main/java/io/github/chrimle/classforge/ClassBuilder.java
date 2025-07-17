package io.github.chrimle.classforge;

import java.util.Optional;

/**
 * Builder of Java classes ({@code class}).
 *
 * @since 0.1.0
 * @author Chrimle
 */
public final class ClassBuilder extends AbstractBuilder<ClassBuilder> {

  private ClassBuilder() {}

  static ClassBuilder newClass() {
    return new ClassBuilder();
  }

  @Override
  protected ClassBuilder self() {
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
        public class %s {

        }
        """
            .formatted(className));

    return codeBuilder.toString();
  }
}
