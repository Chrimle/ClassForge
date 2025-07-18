package io.github.chrimle.classforge;

import io.github.chrimle.classforge.semver.SemVer;
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
public abstract sealed class AbstractBuilder<T extends Builder<T>> implements Builder<T>
    permits ClassBuilder, EnumBuilder {

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

  /** The {@code semVer} of the <em>previously committed</em> class. Starts at {@code 0.0.0}. */
  protected SemVer semVer = new SemVer(0, 0, 0);

  /** The {@code versionPlacement} of the <em>currently uncommitted</em> class. */
  protected VersionPlacement versionPlacement = VersionPlacement.NONE;

  /** The {@code directory} of the <em>currently uncommitted</em> class. */
  protected String directory;

  /** The {@code packageName} of the <em>currently uncommitted</em> class. */
  protected String packageName;

  /** The {@code className} of the <em>currently uncommitted</em> class. */
  protected String className;

  /** {@inheritDoc} */
  @Override
  public T setVersionPlacement(final VersionPlacement versionPlacement) {
    this.versionPlacement = Optional.ofNullable(versionPlacement).orElse(VersionPlacement.NONE);
    return self();
  }

  /** {@inheritDoc} */
  @Override
  public T updateDirectory(final String directory) {
    validateDirectory(directory);
    this.directory = directory;
    return self();
  }

  /** {@inheritDoc} */
  @Override
  public T updatePackageName(final String packageName) {
    validatePackageName(packageName);
    this.packageName = packageName;
    return self();
  }

  /** {@inheritDoc} */
  @Override
  public T updateClassName(final String className) {
    validateClassName(className);
    this.className = className;
    return self();
  }

  /** {@inheritDoc} */
  @Override
  public T commit() {
    validateClass();
    final String fullyQualifiedClassName = resolveFullyQualifiedClassName();
    if (reservedClassNames.contains(fullyQualifiedClassName)) {
      throw new IllegalStateException(
          "Class `%s` has already been generated!".formatted(fullyQualifiedClassName));
    }
    this.semVer = semVer.incrementVersion(determineSemVerChange());
    generateClassFile();
    reservedClassNames.add(fullyQualifiedClassName);
    return self();
  }

  private void validateClass() {
    validateDirectory(this.directory);
    validatePackageName(this.packageName);
    validateClassName(this.className);
    validateAdditionalPredicates();
  }

  /**
   * Determines the {@link SemVer.Change} for the <em>currently uncommitted</em> changes.
   *
   * @return the {@code Change}.
   */
  protected SemVer.Change determineSemVerChange() {
    return SemVer.Change.MAJOR;
  }

  /**
   * Returns <em>this</em> as {@link T}.
   *
   * @return <em>this</em>.
   */
  protected abstract T self();

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
    return Optional.ofNullable(resolveEffectivePackageName())
        .filter(pN -> !pN.isBlank())
        .map(pN -> String.join(".", pN, className))
        .orElse(className);
  }

  /**
   * Resolves the <em>effective package name</em> for the <em>currently uncommitted</em> class.
   *
   * @return the <em>effective package name</em>.
   */
  protected String resolveEffectivePackageName() {
    if (versionPlacement == VersionPlacement.PACKAGE_NAME) {
      final String versionSubPackage = semVer.toString().replace(".", "_");

      return Optional.ofNullable(packageName)
          .filter(pN -> !pN.isBlank())
          .map(pN -> String.join(".", pN, versionSubPackage))
          .orElse(versionSubPackage);
    }
    return packageName;
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
