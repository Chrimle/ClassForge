package io.github.chrimle.classforge;

import java.util.Set;

/**
 * The single-entrypoint for generating Java <i>classes</i> via {@code
 * io.github.chrimle:class-forge}.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public final class ClassForge {

  /**
   * The <i>RegularExpression (RegEx)</i> for determining validity of <i>class</i>-names.
   *
   * @since 0.1.0
   */
  public static final String VALID_CLASS_NAME_REGEX = "^[A-Z][A-Za-z_0-9]*$";

  /**
   * Keywords reserved by the Java Language. These words <b>MUST NOT</b> be used.
   *
   * @since 0.1.0
   */
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
}
