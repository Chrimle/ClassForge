package io.github.chrimle.classforge;

/**
 * Builder of Java {@code enum} classes.
 * @since 0.1.0
 * @author Chrimle
 */
public final class EnumBuilder extends AbstractBuilder {

  private EnumBuilder() {}

  static Builder newEnum() {
    return new EnumBuilder();
  }

  @Override
  protected void validateAdditionalPredicates() {

  }

  @Override
  protected String generateFileContent() {
    return "";
  }
}
