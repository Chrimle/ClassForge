package io.github.chrimle.classforge;

import io.github.chrimle.classforge.utils.FileWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Abstract class for building and generating Java classes.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public abstract sealed class AbstractBuilder implements Builder permits ClassBuilder, EnumBuilder {

  private static final Predicate<String> directoryValidator =
      string -> Optional.ofNullable(string).isPresent();
  private static final Predicate<String> classNameValidator =
      string ->
          Optional.ofNullable(string)
              .filter(className -> className.matches(ClassForge.VALID_CLASS_NAME_REGEX))
              .isPresent();
  private static final Predicate<String> packageNameValidator =
      string ->
          Optional.ofNullable(string)
              .filter(packageName -> !packageName.isBlank())
              .map(packageName -> packageName.matches(ClassForge.VALID_PACKAGE_NAME_REGEX))
              .orElse(true);

  /** The collection of <em>previously committed</em> classes. */
  protected final Set<String> reservedClassNames = new HashSet<>();

  /** The {@code directory} of the <em>currently uncommitted</em> class. */
  protected String directory;

  /** The {@code packageName} of the <em>currently uncommitted</em> class. */
  protected String packageName;

  /** The {@code className} of the <em>currently uncommitted</em> class. */
  protected String className;

  /** {@inheritDoc} */
  @Override
  public Builder updateDirectory(final String directory) {
    validateDirectory(directory);
    this.directory = directory;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Builder updatePackageName(final String packageName) {
    validatePackageName(packageName);
    this.packageName = packageName;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Builder updateClassName(final String className) {
    validateClassName(className);
    this.className = className;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Builder commit() {
    validateClass();
    final String fullyQualifiedClassName = resolveFullyQualifiedClassName();
    if (reservedClassNames.contains(fullyQualifiedClassName)) {
      throw new IllegalStateException(
          "Class `%s` has already been generated!".formatted(fullyQualifiedClassName));
    }
    generateClassFile();
    reservedClassNames.add(fullyQualifiedClassName);
    return this;
  }

  private void validateClass() {
    validateDirectory(this.directory);
    validatePackageName(this.packageName);
    validateClassName(this.className);
    validateAdditionalPredicates();
  }

  /**
   * Validates additional {@link Predicate}s for determining the validity of the <em>currently
   * uncommitted</em> class.
   */
  protected abstract void validateAdditionalPredicates();

  /**
   * Generates the complete file contents for a {@code .java} file for the <em>currently
   * uncommitted</em> class.
   *
   * @return the file contents as a {@code String}.
   */
  protected abstract String generateFileContent();

  /** Generates a {@code .java} class file for the <em>currently uncommitted</em> class. */
  protected void generateClassFile() {
    FileWriter.writeToFile(directory, resolveFullyQualifiedClassName(), generateFileContent());
  }

  /**
   * Resolves the <em>Fully Qualified Class Name (FQCN)</em> for the <em>currently uncommitted</em>
   * class.
   *
   * <p><strong>Example:</strong> {@code module.sub_module.ExampleClass} or {@code
   * AnotherExampleClass}.
   *
   * @return the <em>FQCN</em>.
   */
  protected String resolveFullyQualifiedClassName() {
    return Optional.ofNullable(packageName)
        .filter(pN -> !pN.isBlank())
        .map(pN -> String.join(".", pN, className))
        .orElse(className);
  }

  private static void validateDirectory(final String directory) {
    if (!directoryValidator.test(directory)) {
      throw new IllegalArgumentException("`directory` MUST NOT be `null`!");
    }
  }

  private static void validatePackageName(final String packageName) {
    if (!packageNameValidator.test(packageName)) {
      throw new IllegalArgumentException(
          "`packageName` MUST match the RegEx: " + ClassForge.VALID_PACKAGE_NAME_REGEX);
    }
  }

  private static void validateClassName(final String className) {
    if (!classNameValidator.test(className)) {
      throw new IllegalArgumentException(
          "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX);
    }
  }
}
