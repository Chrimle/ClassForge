package io.github.chrimle.classforge;

import io.github.chrimle.classforge.utils.FileWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class ClassBuilder {

  public static final Set<String> RESERVED_KEYWORDS =
      Set.of(
          "abstract",
          "boolean",
          "break",
          "byte",
          "case",
          "catch",
          "char",
          "class",
          "continue",
          "default",
          "do",
          "double",
          "else",
          "enum",
          "extends",
          "false",
          "final",
          "finally",
          "float",
          "for",
          "if",
          "implements",
          "import",
          "instanceof",
          "int",
          "interface",
          "long",
          "native",
          "new",
          "null",
          "package",
          "private",
          "protected",
          "public",
          "record",
          "return",
          "sealed",
          "static",
          "super",
          "switch",
          "synchronized",
          "this",
          "throw",
          "throws",
          "transient",
          "true",
          "try",
          "var",
          "void",
          "volatile",
          "while",
          "assert");

  public static final String CLASS_NAME_REGEX = "^[A-Z][A-Za-z_0-9]*$";
  public static final String PACKAGE_NAME_REGEX = "^[A-Za-z_0-9]+(\\.[A-Za-z_0-9]+)*$";
  private final Set<String> reservedClassNames = new HashSet<>();
  private String absolutePathPrefix;
  private String packageName;
  private String className;

  private ClassBuilder() {}

  public static ClassBuilder newClass() {
    return new ClassBuilder();
  }

  public ClassBuilder updateAbsolutePathPrefix(final String absolutePathPrefix) {
    this.absolutePathPrefix =
        Optional.ofNullable(absolutePathPrefix)
            .orElseThrow(
                () -> new IllegalArgumentException("`absolutePathPrefix` MUST NOT be `null`!"));
    return this;
  }

  public ClassBuilder updatePackageName(final String packageName) {
    if (Optional.ofNullable(packageName)
        .filter(pN -> !pN.isBlank())
        .filter(pN -> !pN.matches(PACKAGE_NAME_REGEX))
        .isPresent()) {
      throw new IllegalArgumentException(
          "`packageName` MUST match the RegEx: " + PACKAGE_NAME_REGEX);
    }
    this.packageName = packageName;
    return this;
  }

  public ClassBuilder updateClassName(final String className) {
    this.className =
        Optional.ofNullable(className)
            .filter(cN -> cN.matches(CLASS_NAME_REGEX))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "`className` MUST match the RegEx: " + CLASS_NAME_REGEX));
    return this;
  }

  public ClassBuilder commit() {
    final String fullyQualifiedClassName = getFullyQualifiedClassName();
    if (reservedClassNames.contains(fullyQualifiedClassName)) {
      throw new IllegalStateException(
          "Class `%s` has already been generated!".formatted(fullyQualifiedClassName));
    }
    generateClassFile(fullyQualifiedClassName);
    reservedClassNames.add(fullyQualifiedClassName);
    return this;
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
