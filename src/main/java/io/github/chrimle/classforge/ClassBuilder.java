package io.github.chrimle.classforge;

import java.util.Optional;

public final class ClassBuilder extends AbstractBuilder {

  private ClassBuilder() {}

  static Builder newClass() {
    return new ClassBuilder();
  }

  @Override
  public Builder updateAbsolutePathPrefix(final String absolutePathPrefix) {
    if (!absolutePathPrefixValidator.test(absolutePathPrefix)) {
      throw new IllegalArgumentException("`absolutePathPrefix` MUST NOT be `null`!");
    }
    this.absolutePathPrefix = absolutePathPrefix;
    return this;
  }

  @Override
  public Builder updatePackageName(final String packageName) {
    if (!packageNameValidator.test(packageName)) {
      throw new IllegalArgumentException(
          "`packageName` MUST match the RegEx: " + ClassForge.VALID_PACKAGE_NAME_REGEX);
    }
    this.packageName = packageName;
    return this;
  }

  @Override
  public Builder updateClassName(final String className) {
    if (!classNameValidator.test(className)) {
      throw new IllegalArgumentException(
          "`className` MUST match the RegEx: " + ClassForge.VALID_CLASS_NAME_REGEX);
    }
    this.className = className;
    return this;
  }

  @Override
  public Builder commit() {
    preCommitCheck();
    final String fullyQualifiedClassName = resolveFullyQualifiedClassName();
    if (reservedClassNames.contains(fullyQualifiedClassName)) {
      throw new IllegalStateException(
          "Class `%s` has already been generated!".formatted(fullyQualifiedClassName));
    }
    generateClassFile();
    reservedClassNames.add(fullyQualifiedClassName);
    return this;
  }

  private void preCommitCheck() {
    if (!classBuilderPredicate.test(this)) {
      throw new IllegalStateException("Cannot generate class due to invalid state!");
    }
  }

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
