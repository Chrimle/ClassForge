/*
 * Copyright 2025 Chrimle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.chrimle.classforge;

import io.github.chrimle.classforge.internal.ExceptionFactory;
import java.util.Set;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The single-entrypoint for generating Java <i>classes</i> via {@code
 * io.github.chrimle:class-forge}.
 *
 * <p><strong>Instructions</strong>
 *
 * <ul>
 *   <li>Ensure <strong>NO</strong> reserved keywords are used when generating classes.
 *   <li>Generate <em>classes</em> via:
 *       <ul>
 *         <li>{@link #newClassBuilder()} - to generate Java {@code class}es.
 *         <li>{@link #newEnumBuilder()} - to generate Java {@code enum} classes.
 *       </ul>
 * </ul>
 *
 * @author Chrimle
 * @see #RESERVED_KEYWORDS
 * @see #newClassBuilder() Generating a Java class.
 * @see #newEnumBuilder() Generating an Enum class.
 * @since 0.1.0
 */
@API(status = API.Status.STABLE, since = "0.6.0")
public final class ClassForge {

  private ClassForge() {}

  /**
   * A <em>type</em> of <em>Java class</em> which can be generated.
   *
   * @author Chrimle
   * @since 0.10.0
   */
  public enum ClassType {
    /**
     * A Java {@code class}.
     *
     * <p><strong>Example</strong>
     *
     * <pre>{@code
     * class Example {
     *
     * }
     * }</pre>
     *
     * @since 0.10.0
     */
    CLASS,
    /**
     * A Java {@code enum} <em>class</em>.
     *
     * <p><strong>Example</strong>
     *
     * <pre>{@code
     * enum Example {
     *
     * }
     * }</pre>
     *
     * @since 0.10.0
     */
    ENUM;
  }

  /**
   * Creates a new {@link Builder} instance for generating {@code classType}s.
   *
   * @param classType to generate.
   * @return a new {@link Builder} instance.
   * @since 0.10.0
   */
  @NotNull
  @Contract("null -> fail; _ -> new")
  public static Builder<?> newBuilder(final ClassType classType) {
    if (classType == null) {
      throw ExceptionFactory.nullException("classType");
    }
    return switch (classType) {
      case CLASS -> newClassBuilder();
      case ENUM -> newEnumBuilder();
    };
  }

  /**
   * Creates a new {@link Builder} instance for generating Java <i>classes</i>.
   *
   * @return a new {@link Builder} instance.
   * @since 0.6.0
   */
  @NotNull
  @Contract(" -> new")
  public static Builder<?> newClassBuilder() {
    return ClassBuilder.newClass();
  }

  /**
   * Creates a new {@link Builder} instance for generating Java {@code enum} classes.
   *
   * @return a new {@link Builder} instance.
   * @since 0.6.0
   */
  @NotNull
  @Contract(" -> new")
  public static Builder<?> newEnumBuilder() {
    return EnumBuilder.newClass();
  }

  /**
   * The <i>RegularExpression (RegEx)</i> for determining validity of <i>class</i>-names.
   *
   * <p><strong>Example</strong>
   *
   * <pre>{@code
   * public class Example { } // Valid
   * }</pre>
   *
   * @since 0.1.0
   */
  public static final String VALID_CLASS_NAME_REGEX = "^[A-Z][A-Za-z_0-9]*$";

  /**
   * The <i>RegularExpression (RegEx)</i> for determining validity of <i>package</i>-names.
   *
   * <p><strong>Example</strong>
   *
   * <pre>{@code
   * package example.sub_module; // Valid
   * }</pre>
   *
   * @since 0.1.0
   */
  public static final String VALID_PACKAGE_NAME_REGEX = "^[A-Za-z_0-9]+(\\.[A-Za-z_0-9]+)*$";

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
