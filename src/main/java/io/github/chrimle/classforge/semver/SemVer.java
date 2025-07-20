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
package io.github.chrimle.classforge.semver;

import io.github.chrimle.classforge.utils.ExceptionFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a <em>Semantic Version (SemVer)</em>.
 *
 * @param major version.
 * @param minor version.
 * @param patch version.
 * @since 0.3.0
 * @author Chrimle
 */
public record SemVer(int major, int minor, int patch) {

  /**
   * The <em>type</em> of change.
   *
   * @since 0.3.0
   * @author Chrimle
   */
  public enum Change {
    /** <strong>MAJOR</strong> change. */
    MAJOR,
    /** <strong>MINOR</strong> change. */
    MINOR,
    /** <strong>PATCH</strong> change. */
    PATCH
  }

  /**
   * Constructs a <em>valid</em> {@link SemVer} instance.
   *
   * @param major version.
   * @param minor version.
   * @param patch version.
   * @throws IllegalArgumentException if {@link #major} is less than {@code 0}.
   * @throws IllegalArgumentException if {@link #minor} is less than {@code 0}.
   * @throws IllegalArgumentException if {@link #patch} is less than {@code 0}.
   * @since 0.3.0
   */
  public SemVer {
    if (major < 0) throw ExceptionFactory.lessThanZeroException("major");
    if (minor < 0) throw ExceptionFactory.lessThanZeroException("minor");
    if (patch < 0) throw ExceptionFactory.lessThanZeroException("patch");
  }

  /**
   * Creates a new {@link SemVer} with the corresponding version incremented.
   *
   * @param change for determining the new {@code SemVer}.
   * @return the new {@code SemVer}.
   * @since 0.3.0
   * @throws IllegalArgumentException if {@code change} is {@code null}.
   */
  @Contract("null -> fail")
  public SemVer incrementVersion(final Change change) {
    if (change == null) throw ExceptionFactory.nullException("change");
    return switch (change) {
      case MAJOR -> incrementMajor();
      case MINOR -> incrementMinor();
      case PATCH -> incrementPatch();
    };
  }

  /**
   * Creates a new {@link SemVer} with the {@link #major}-version incremented.
   *
   * @since 0.3.0
   * @return the new {@code SemVer}.
   */
  @NotNull
  @Contract(" -> new")
  public SemVer incrementMajor() {
    return new SemVer(major + 1, 0, 0);
  }

  /**
   * Creates a new {@link SemVer} with the {@link #minor}-version incremented.
   *
   * @since 0.3.0
   * @return the new {@code SemVer}.
   */
  @NotNull
  @Contract(" -> new")
  public SemVer incrementMinor() {
    return new SemVer(major, minor + 1, 0);
  }

  /**
   * Creates a new {@link SemVer} with the {@link #patch}-version incremented.
   *
   * @since 0.3.0
   * @return the new {@code SemVer}.
   */
  @NotNull
  @Contract(" -> new")
  public SemVer incrementPatch() {
    return new SemVer(major, minor, patch + 1);
  }

  /**
   * Returns <em>this</em> {@link SemVer} as a {@code String} in the format: {@code
   * v{major}.{minor}.{patch}}.
   *
   * @return the formatted {@code String}.
   * @since 0.3.0
   */
  @NotNull
  @Contract(pure = true)
  @Override
  public String toString() {
    return "v%d.%d.%d".formatted(major, minor, patch);
  }

  /**
   * Returns <em>this</em> {@link SemVer} as a {@code String} in the format: {@code
   * v{major}.{minor}.{patch}} - where trailing zero sub-versions are omitted.
   *
   * <p><strong>Examples:</strong>
   *
   * <ul>
   *   <li>{@code 1.0.0} returns {@code "v1"}
   *   <li>{@code 1.2.0} returns {@code "v1.2"}
   *   <li>{@code 1.2.3} returns {@code "v1.2.3"}
   * </ul>
   *
   * @return the formatted {@code String}.
   * @since 0.4.0
   */
  @NotNull
  public String toShortVersionString() {
    if (patch > 0) {
      return "v%d.%d.%d".formatted(major, minor, patch);
    }
    if (minor > 0) {
      return "v%d.%d".formatted(major, minor);
    }
    return "v%d".formatted(major);
  }
}
