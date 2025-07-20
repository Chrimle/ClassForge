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

import io.github.chrimle.classforge.utils.ExceptionFactory;
import java.util.*;
import java.util.function.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Builder of Java {@code enum} classes.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public final class EnumBuilder extends AbstractBuilder<EnumBuilder> {

  /**
   * The <em>RegularExpression (RegEx)</em> for determining validity of enum constant-names.
   *
   * @since 0.2.0
   */
  public static final String VALID_ENUM_CONSTANT_NAME_REGEX =
      "^([A-Za-z]|(_+[A-Za-z0-9]))[A-Z_a-z0-9]*$";

  private static final Predicate<String> enumConstantNamePredicate =
      enumConstantName ->
          Optional.ofNullable(enumConstantName)
              .filter(name -> name.matches(VALID_ENUM_CONSTANT_NAME_REGEX))
              .isPresent();
  private final List<String> enumConstants = new ArrayList<>();

  private EnumBuilder() {}

  @NotNull
  @Contract(" -> new")
  static EnumBuilder newClass() {
    return new EnumBuilder();
  }

  /**
   * Adds the {@code enumConstantNames} to the <em>currently uncommitted</em> enum class.
   *
   * @param enumConstantNames to add.
   * @return this Builder.
   * @throws IllegalArgumentException if {@code enumConstantNames} is {@code null}.
   * @throws IllegalArgumentException if {@code enumConstantNames} is empty.
   * @throws IllegalArgumentException if {@code enumConstantNames} contain a {@code String} which is
   *     {@code null}.
   * @throws IllegalArgumentException if {@code enumConstantNames} contain a {@code String} which
   *     does not match the RegEx {@value VALID_ENUM_CONSTANT_NAME_REGEX}.
   * @throws IllegalArgumentException if {@code enumConstantNames} contain a {@code String} which
   *     does not exist in the <em>currently uncommitted</em> class.
   * @since 0.3.0
   */
  @Contract("null -> fail; _ -> this")
  public EnumBuilder addEnumConstants(final String... enumConstantNames) {

    if (Optional.ofNullable(enumConstantNames)
        .filter(enums -> enums.length >= 1)
        .map(Arrays::stream)
        .filter(stream -> stream.allMatch(Objects::nonNull))
        .isEmpty()) {
      throw new IllegalArgumentException("`enumConstantNames` MUST NOT be null or empty!");
    }

    final var enumNamesList = List.of(enumConstantNames);

    if (new HashSet<>(enumNamesList).size() < enumNamesList.size()) {
      throw new IllegalArgumentException("Duplicate Enum constant names were provided!");
    }

    for (final String enumConstantName : enumConstantNames) {
      validateEnumConstantName(enumConstantName);
      if (enumConstants.contains(enumConstantName)) {
        throw ExceptionFactory.alreadyExistsException("enum constant", enumConstantName);
      }
    }
    enumConstants.addAll(enumNamesList);
    return this;
  }

  /**
   * Removes the {@code enumConstantNames} from the <em>currently uncommitted</em> enum class.
   *
   * @param enumConstantNames to remove.
   * @return this Builder.
   * @throws IllegalArgumentException if {@code enumConstantNames} is {@code null}.
   * @throws IllegalArgumentException if {@code enumConstantNames} is empty.
   * @throws IllegalArgumentException if {@code enumConstantNames} contain a {@code String} which is
   *     {@code null}.
   * @throws IllegalArgumentException if {@code enumConstantNames} contain a {@code String} which
   *     does not exist in the <em>currently uncommitted</em> class.
   * @since 0.3.0
   */
  @Contract("null -> fail; _ -> this")
  public EnumBuilder removeEnumConstants(final String... enumConstantNames) {
    if (Optional.ofNullable(enumConstantNames)
        .filter(enums -> enums.length >= 1)
        .map(Arrays::stream)
        .filter(stream -> stream.allMatch(Objects::nonNull))
        .isEmpty()) {
      throw new IllegalArgumentException("`enumConstantNames` MUST NOT be null or empty!");
    }

    for (final String enumConstantName : enumConstantNames) {
      if (!enumConstants.contains(enumConstantName)) {
        throw new IllegalArgumentException(
            "No Enum constant named '%s' exists!".formatted(enumConstantName));
      }
    }
    enumConstants.removeAll(List.of(enumConstantNames));
    return this;
  }

  /**
   * <em>Updates</em> the {@code oldEnumConstant} into {@code newEnumConstant} in the <em>currently
   * uncommitted</em> class.
   *
   * @param oldEnumConstant to be removed.
   * @param newEnumConstant to be added.
   * @return this Builder.
   * @throws IllegalArgumentException if {@code oldEnumConstant} is {@code null}.
   * @throws IllegalArgumentException if {@code oldEnumConstant} does not exist.
   * @throws IllegalArgumentException if {@code newEnumConstant} is {@code null}.
   * @throws IllegalArgumentException if {@code newEnumConstant} does not match the RegEx {@value
   *     VALID_ENUM_CONSTANT_NAME_REGEX}.
   * @throws IllegalArgumentException if {@code newEnumConstant} already exists.
   * @since 0.5.0
   */
  @Contract("null, _ -> fail; _, null -> fail; _, _ -> this")
  public EnumBuilder updateEnumConstant(
      final String oldEnumConstant, final String newEnumConstant) {
    if (oldEnumConstant == null) {
      throw ExceptionFactory.nullException("oldEnumConstant");
    }
    if (!enumConstants.contains(oldEnumConstant)) {
      throw new IllegalArgumentException(
          "No Enum constant named '%s' exists!".formatted(oldEnumConstant));
    }
    if (newEnumConstant == null) {
      throw ExceptionFactory.nullException("newEnumConstant");
    }
    validateEnumConstantName(newEnumConstant);
    if (enumConstants.contains(newEnumConstant)) {
      throw ExceptionFactory.alreadyExistsException("enum constant", newEnumConstant);
    }
    enumConstants.replaceAll(
        existingEnumConstant ->
            existingEnumConstant.equals(oldEnumConstant) ? newEnumConstant : existingEnumConstant);
    return this;
  }

  private static void validateEnumConstantName(final String enumConstantName) {
    if (!enumConstantNamePredicate.test(enumConstantName)) {
      throw ExceptionFactory.notMatchingRegExException(
          "enumConstantName", VALID_ENUM_CONSTANT_NAME_REGEX);
    }
    if (ClassForge.RESERVED_KEYWORDS.contains(enumConstantName)) {
      throw new IllegalArgumentException("`enumConstantName` MUST NOT be a reserved Java keyword!");
    }
  }

  @Contract(value = " -> this", pure = true)
  @Override
  protected EnumBuilder self() {
    return this;
  }

  @NotNull
  @Override
  protected String generateFileContent() {
    final StringBuilder codeBuilder = new StringBuilder();

    Optional.ofNullable(resolveEffectivePackageName())
        .filter(pN -> !pN.isBlank())
        .map("package %s;\n\n"::formatted)
        .ifPresent(codeBuilder::append);

    codeBuilder.append(
        """
        public enum %s {
          %s
        }
        """
            .formatted(className, String.join(",\n\t", enumConstants) + ";"));

    return codeBuilder.toString();
  }
}
