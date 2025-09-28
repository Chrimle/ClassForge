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
package io.github.chrimle.classforge.enums;

import org.apiguardian.api.API;
import org.jetbrains.annotations.NotNull;

/**
 * The <em>deserializing method</em> for an <strong>Enum class</strong>. A deserializing method may
 * be customized using <em>strategies</em>, which will affect how the deserializing method will be
 * implemented.
 *
 * @param matchingStrategy for how <em>values</em> shall be matched to <em>enum constants</em>.
 *     <strong>Default:</strong> {@link MatchingStrategy#EXACT_NAME}.
 * @param noMatchStrategy for how non-matching <em>values</em> shall be handled.
 *     <strong>Default:</strong> {@link NoMatchStrategy#THROW}.
 * @param nullStrategy for how {@code null} <em>values</em> shall be handled.
 *     <strong>Default:</strong> {@link NullStrategy#THROW}.
 * @since 0.11.0
 * @author Chrimle
 */
@API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
public record DeserializingMethod(
    @API(status = API.Status.EXPERIMENTAL, since = "0.11.0") MatchingStrategy matchingStrategy,
    @API(status = API.Status.EXPERIMENTAL, since = "0.11.0") NoMatchStrategy noMatchStrategy,
    @API(status = API.Status.EXPERIMENTAL, since = "0.11.0") NullStrategy nullStrategy) {

  private static final String TAB = "  ";
  private static final String NEWLINE = "\n";

  /**
   * Generates the source Java code for the deserializing method {@code fromValue(String)}.
   * <strong>NOTE:</strong> intended for internal usage only.
   *
   * @param className for the return type.
   * @param defaultValue to be returned.
   * @return the method code.
   * @since 0.11.0
   */
  @API(status = API.Status.INTERNAL, since = "0.11.0")
  public @NotNull String generateMethodCode(final String className, final String defaultValue) {
    final var stringBuilder = new StringBuilder();
    stringBuilder
        .append(TAB)
        .append("public static %s fromValue(final String value) {".formatted(className))
        .append(NEWLINE);
    stringBuilder.append(TAB).append(TAB).append("if (value == null) {").append(NEWLINE);
    switch (nullStrategy) {
      case FALLBACK ->
          stringBuilder
              .append(TAB)
              .append(TAB)
              .append(TAB)
              .append("return %s;".formatted(defaultValue))
              .append(NEWLINE);
      case NULL ->
          stringBuilder.append(TAB).append(TAB).append(TAB).append("return null;").append(NEWLINE);
      case THROW ->
          stringBuilder
              .append(TAB)
              .append(TAB)
              .append(TAB)
              .append("throw new NullPointerException(\"`value` cannot be `null`\");")
              .append(NEWLINE);
    }
    stringBuilder.append(TAB).append(TAB).append("}").append(NEWLINE);
    switch (matchingStrategy) {
      case CASE_INSENSITIVE_NAME -> {
        stringBuilder
            .append(TAB)
            .append(TAB)
            .append("for (final var enumConstant : values()) {")
            .append(NEWLINE);
        stringBuilder
            .append(TAB)
            .append(TAB)
            .append(TAB)
            .append("if (enumConstant.name().equalsIgnoreCase(value)) {")
            .append(NEWLINE);
        stringBuilder
            .append(TAB)
            .append(TAB)
            .append(TAB)
            .append(TAB)
            .append("return enumConstant;")
            .append(NEWLINE);
        stringBuilder.append(TAB).append(TAB).append(TAB).append("}").append(NEWLINE);
        stringBuilder.append(TAB).append(TAB).append("}").append(NEWLINE);
      }
      case EXACT_NAME -> {
        stringBuilder
            .append(TAB)
            .append(TAB)
            .append("final var enumConstant = valueOf(value);")
            .append(NEWLINE);
        stringBuilder.append(TAB).append(TAB).append("if (enumConstant != null) {").append(NEWLINE);
        stringBuilder
            .append(TAB)
            .append(TAB)
            .append(TAB)
            .append("return enumConstant;")
            .append(NEWLINE);
        stringBuilder.append(TAB).append(TAB).append("}").append(NEWLINE);
      }
    }
    switch (noMatchStrategy) {
      case FALLBACK ->
          stringBuilder
              .append(TAB)
              .append(TAB)
              .append("return %s;".formatted(defaultValue))
              .append(NEWLINE);
      case NULL -> stringBuilder.append(TAB).append(TAB).append("return null;").append(NEWLINE);
      case THROW ->
          stringBuilder
              .append(TAB)
              .append(TAB)
              .append("throw new IllegalArgumentException(\"Unexpected value '\" + value + \"'\");")
              .append(NEWLINE);
    }
    stringBuilder.append(TAB).append("}").append(NEWLINE);
    return stringBuilder.toString();
  }
}
