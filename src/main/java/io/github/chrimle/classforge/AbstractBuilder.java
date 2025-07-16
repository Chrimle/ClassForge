package io.github.chrimle.classforge;

import io.github.chrimle.classforge.utils.FileWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public abstract sealed class AbstractBuilder implements Builder permits ClassBuilder {

  static final Predicate<String> absolutePathPrefixValidator =
      string -> Optional.ofNullable(string).isPresent();
  static final Predicate<String> classNameValidator =
      string ->
          Optional.ofNullable(string)
              .filter(className -> className.matches(ClassForge.VALID_CLASS_NAME_REGEX))
              .isPresent();
  static final Predicate<String> packageNameValidator =
      string ->
          Optional.ofNullable(string)
              .filter(packageName -> !packageName.isBlank())
              .map(packageName -> packageName.matches(ClassForge.VALID_PACKAGE_NAME_REGEX))
              .orElse(true);
  protected final Set<String> reservedClassNames = new HashSet<>();
  protected String absolutePathPrefix;
  protected String packageName;
  protected String className;

  @Override
  public Builder updateAbsolutePathPrefix(final String absolutePathPrefix) {
    validateAbsolutePathPrefix(absolutePathPrefix);
    this.absolutePathPrefix = absolutePathPrefix;
    return this;
  }

  @Override
  public Builder updatePackageName(final String packageName) {
    validatePackageName(packageName);
    this.packageName = packageName;
    return this;
  }

  @Override
  public Builder updateClassName(final String className) {
    validateClassName(className);
    this.className = className;
    return this;
  }

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
    validateAbsolutePathPrefix(this.absolutePathPrefix);
    validatePackageName(this.packageName);
    validateClassName(this.className);
    validateAdditionalPredicates();
  }

  protected abstract void validateAdditionalPredicates();

  protected abstract String generateFileContent();

  protected void generateClassFile() {
    FileWriter.writeToFile(
        absolutePathPrefix, resolveFullyQualifiedClassName(), generateFileContent());
  }

  protected String resolveFullyQualifiedClassName() {
    return Optional.ofNullable(packageName)
        .filter(pN -> !pN.isBlank())
        .map(pN -> String.join(".", pN, className))
        .orElse(className);
  }

  private static void validateAbsolutePathPrefix(String absolutePathPrefix) {
    if (!absolutePathPrefixValidator.test(absolutePathPrefix)) {
      throw new IllegalArgumentException("`absolutePathPrefix` MUST NOT be `null`!");
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
