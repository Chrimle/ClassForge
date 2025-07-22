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
package io.github.chrimle.classforge.internal;

import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Factory-class for instantiating {@link Exception}-classes with <em>message presets</em>.
 *
 * <p><em><strong>FOR INTERNAL USE ONLY.</strong></em>
 *
 * @since 0.5.0
 * @author Chrimle
 */
@API(status = API.Status.INTERNAL, since = "0.6.0", consumers = "io.github.chrimle.classforge")
public final class ExceptionFactory {

  private ExceptionFactory() {}

  /**
   * Creates a new {@link IllegalArgumentException}-instance with a {@code message} representing a
   * <em>Null</em>-exception.
   *
   * @param key which had a {@code null}-value.
   * @return the exception.
   * @since 0.5.0
   */
  @NotNull
  @Contract("_ -> new")
  public static IllegalArgumentException nullException(final String key) {
    return new IllegalArgumentException("`%s` MUST NOT be `null`".formatted(key));
  }

  /**
   * Creates a new {@link IllegalArgumentException}-instance with a {@code message} representing a
   * <em>Null or Empty</em>-exception.
   *
   * @param key which had a <em>null or empty</em> value.
   * @return the exception.
   * @since 0.5.0
   */
  @NotNull
  @Contract("_ -> new")
  public static IllegalArgumentException nullOrEmptyException(final String key) {
    return new IllegalArgumentException("`%s` MUST NOT be `null` or empty".formatted(key));
  }

  /**
   * Creates a new {@link IllegalArgumentException}-instance with a {@code message} representing a
   * <em>Less Than Zero</em>-exception.
   *
   * @param key which had a <em>less than zero</em> value.
   * @return the exception.
   * @since 0.5.0
   */
  @NotNull
  @Contract("_ -> new")
  public static IllegalArgumentException lessThanZeroException(final String key) {
    return new IllegalArgumentException("`%s` MUST NOT be less than `0`".formatted(key));
  }

  /**
   * Creates a new {@link IllegalArgumentException}-instance with a {@code message} representing a
   * <em>Not Matching RegEx</em>-exception.
   *
   * @param key which had a <em>non-matching</em> value.
   * @param regEx to match.
   * @return the exception.
   * @since 0.5.0
   */
  @NotNull
  @Contract("_, _ -> new")
  public static IllegalArgumentException notMatchingRegExException(
      final String key, final String regEx) {
    return new IllegalArgumentException("`%s` MUST match the RegEx `%s`".formatted(key, regEx));
  }

  /**
   * Creates a new {@link IllegalArgumentException}-instance with a {@code message} representing an
   * <em>Already Exists</em>-exception.
   *
   * @param type of the entity.
   * @param name of the entity.
   * @return the exception.
   * @since 0.5.0
   */
  @NotNull
  @Contract(value = "_, _ -> new")
  public static IllegalArgumentException alreadyExistsException(
      final String type, final String name) {
    return new IllegalArgumentException("`%s` named `%s` already exists".formatted(type, name));
  }

  /**
   * Creates a new {@link IllegalArgumentException}-instance with a {@code message} representing a
   * <em>Does Not Exist</em>-exception.
   *
   * @param type of the entity.
   * @param name of the entity.
   * @return the exception.
   * @since 0.5.0
   */
  @NotNull
  @Contract("_, _ -> new")
  public static IllegalArgumentException doesNotExistException(
      final String type, final String name) {
    return new IllegalArgumentException("`%s` named `%s` does not exist".formatted(type, name));
  }

  /**
   * Creates a new {@link IllegalArgumentException}-instance with a {@code message} representing a
   * <em>Reserved Java Keyword</em>-exception.
   *
   * @param key which had <em>Reserved Java Keyword</em>-value.
   * @return the exception.
   * @since 0.5.0
   */
  @NotNull
  @Contract("_ -> new")
  public static IllegalArgumentException reservedJavaKeywordException(final String key) {
    return new IllegalArgumentException("`%s` MUST NOT be a Reserved Java Keyword".formatted(key));
  }
}
