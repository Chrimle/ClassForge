package io.github.chrimle.classforge;

import io.github.chrimle.classforge.utils.FileWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public final class ClassBuilder {

  public static final String CLASS_NAME_REGEX = "^[A-Z][A-Za-z_0-9]*$";
  public static final String PACKAGE_NAME_REGEX = "^[A-Za-z_0-9]+(\\.[A-Za-z_0-9]+)*$";
  private static final Predicate<String> absolutePathPrefixValidator =
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
              .map(packageName -> packageName.matches(PACKAGE_NAME_REGEX))
              .orElse(true);
  private static final Predicate<ClassBuilder> classBuilderPredicate =
      classBuilder ->
          classNameValidator.test(classBuilder.className)
              && packageNameValidator.test(classBuilder.packageName);
  private final Set<String> reservedClassNames = new HashSet<>();
  private String absolutePathPrefix;
  private String packageName;
  private String className;

  private ClassBuilder() {}

  public static ClassBuilder newClass() {
    return new ClassBuilder();
  }

  public ClassBuilder updateAbsolutePathPrefix(final String absolutePathPrefix) {
    if (!absolutePathPrefixValidator.test(absolutePathPrefix)) {
      throw new IllegalArgumentException("`absolutePathPrefix` MUST NOT be `null`!");
    }
    this.absolutePathPrefix = absolutePathPrefix;
    return this;
  }

  public ClassBuilder updatePackageName(final String packageName) {
    if (!packageNameValidator.test(packageName)) {
      throw new IllegalArgumentException(
          "`packageName` MUST match the RegEx: " + PACKAGE_NAME_REGEX);
    }
    this.packageName = packageName;
    return this;
  }

  public ClassBuilder updateClassName(final String className) {
    if (!classNameValidator.test(className)) {
      throw new IllegalArgumentException(
          "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX);
    }
    this.className = className;
    return this;
  }

  public ClassBuilder commit() {
    preCommitCheck();
    final String fullyQualifiedClassName = getFullyQualifiedClassName();
    if (reservedClassNames.contains(fullyQualifiedClassName)) {
      throw new IllegalStateException(
          "Class `%s` has already been generated!".formatted(fullyQualifiedClassName));
    }
    generateClassFile(fullyQualifiedClassName);
    reservedClassNames.add(fullyQualifiedClassName);
    return this;
  }

  private void preCommitCheck() {
    if (!classBuilderPredicate.test(this)) {
      throw new IllegalStateException("Cannot generate class due to invalid state!");
    }
  }

  private void generateClassFile(final String fullyQualifiedClassName) {
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

    FileWriter.writeToFile(absolutePathPrefix, fullyQualifiedClassName, codeBuilder.toString());
  }

  private String getFullyQualifiedClassName() {
    return Optional.ofNullable(packageName)
        .map(pN -> String.join(".", pN, className))
        .orElse(className);
  }
}
