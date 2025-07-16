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
  static final Predicate<ClassBuilder> classBuilderPredicate =
      classBuilder ->
          classNameValidator.test(classBuilder.className)
              && packageNameValidator.test(classBuilder.packageName);
  protected final Set<String> reservedClassNames = new HashSet<>();
  protected String absolutePathPrefix;
  protected String packageName;
  protected String className;

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
}
