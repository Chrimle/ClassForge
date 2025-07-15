package io.github.chrimle.classforge;

import io.github.chrimle.classforge.utils.FileWriter;
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
  private final String absolutePathPrefix;
  private final String fullyQualifiedClassName;
  private final String packageName;
  private final String className;

  public ClassBuilder(
      final String absolutePathPrefix, final String packageName, final String className) {

    this.absolutePathPrefix =
        Optional.ofNullable(absolutePathPrefix)
            .orElseThrow(
                () -> new IllegalArgumentException("`absolutePathPrefix` MUST NOT be `null`!"));

    if (Optional.ofNullable(packageName)
        .filter(pN -> !pN.isBlank())
        .filter(pN -> !pN.matches(PACKAGE_NAME_REGEX))
        .isPresent()) {
      throw new IllegalArgumentException(
          "`packageName` MUST match the RegEx: " + PACKAGE_NAME_REGEX);
    }
    this.packageName = packageName;

    this.className =
        Optional.ofNullable(className)
            .filter(cN -> cN.matches(CLASS_NAME_REGEX))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "`className` MUST match the RegEx: " + CLASS_NAME_REGEX));

    this.fullyQualifiedClassName =
        Optional.ofNullable(packageName)
            .map(pN -> String.join(".", pN, className))
            .orElse(className);
  }

  public void commit() {
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
}
