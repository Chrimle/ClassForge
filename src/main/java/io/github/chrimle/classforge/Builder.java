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

import io.github.chrimle.semver.Change;
import io.github.chrimle.semver.SemVer;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;

/**
 * Builder of a Java <i>class</i> - which <b>MAY</b> generate a {@code class}, {@code enum} or
 * {@code record}.
 *
 * @author Chrimle
 * @since 0.1.0
 */
@API(status = API.Status.STABLE, since = "0.6.0")
public sealed interface Builder<T extends Builder<T>> permits AbstractBuilder {

  /**
   * The {@link SemVer} <em><strong>format</strong></em> of a generated <em>class</em>.
   *
   * @author Chrimle
   * @see VersionPlacement
   * @since 0.9.0
   */
  enum VersionFormat {
    /**
     * The <em><strong>complete</strong></em> version - always displayed as {@code
     * {major}.{minor}.{patch}}.
     *
     * <p><strong>Examples</strong>
     *
     * <ul>
     *   <li>{@code 1.2.3}
     *   <li>{@code 1.3.0}
     *   <li>{@code 2.0.0}
     * </ul>
     *
     * @since 0.9.0
     */
    COMPLETE,
    /**
     * The <em><strong>short</strong></em> version - omitting trailing zeroes.
     *
     * <p><strong>Examples</strong>
     *
     * <ul>
     *   <li>{@code 1.2.3}
     *   <li>{@code 1.3}
     *   <li>{@code 2}
     * </ul>
     *
     * @since 0.9.0
     */
    SHORT
  }

  /**
   * The {@link SemVer} <em>placement</em> in generated <em>classes</em>.
   *
   * <p><strong>Default: </strong> {@link #NONE}.
   *
   * @author Chrimle
   * @see VersionFormat
   * @since 0.3.0
   */
  enum VersionPlacement {

    /**
     * The {@link SemVer} is appended as a <em>suffix</em> to the generated <em>class</em> name.
     *
     * <p><strong>Example: </strong> {@code class ExampleV1_2_3 {}}.
     *
     * @since 0.9.0
     */
    CLASS_NAME_SUFFIX,

    /**
     * <strong>Default.</strong> Version information is excluded.
     *
     * @since 0.3.0
     */
    NONE,

    /**
     * The {@link SemVer} is appended as a <em>suffix</em> to the generated <em>class</em> {@code
     * package}-name.
     *
     * <p><strong>Example: </strong> {@code package io.github.chrimle.v1_2_3;}.
     *
     * @since 0.9.0
     */
    PACKAGE_NAME_SUFFIX
  }

  /**
   * <em>Sets</em> the {@code versionFormat} of the <em>currently uncommitted</em> class.
   *
   * <p><strong>Default:</strong> {@link VersionFormat#COMPLETE}
   *
   * @param versionFormat for the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.9.0
   */
  T setVersionFormat(final VersionFormat versionFormat);

  /**
   * <em>Sets</em> the {@code versionPlacement} of the <em>currently uncommitted</em> class.
   *
   * <p><strong>Default: </strong> {@link VersionPlacement#NONE}.
   *
   * @param versionPlacement for the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.3.0
   */
  T setVersionPlacement(final VersionPlacement versionPlacement);

  /**
   * <em>Gets</em> the {@code semVer} of the <em>currently uncommitted</em> class.
   * <em><strong>NOTE:</strong></em> the {@code semVer} will be updated when <em>committing</em> the
   * changes.
   *
   * @return the {@code semVer} of the class.
   * @since 0.6.0
   */
  SemVer getSemVer();

  /**
   * <em>Sets</em> the {@code semVer} of the <em>currently uncommitted</em> class.
   *
   * <p><strong>NOTE:</strong> the {@code semVer} will be updated when <em>committing</em> the
   * changes.
   *
   * @param semVer for the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.5.0
   */
  T setSemVer(final SemVer semVer);

  /**
   * <em><strong>Updates</strong></em> the {@code directory} of the <em>currently uncommitted</em>
   * class.
   *
   * @param directory of the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  T updateDirectory(final String directory);

  /**
   * <em><strong>Updates</strong></em> the {@code className} of the <em>currently uncommitted</em>
   * class.
   *
   * @param className of the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  T updateClassName(final String className);

  /**
   * <em><strong>Updates</strong></em> the {@code packageName} of the <em>currently uncommitted</em>
   * class.
   *
   * @param packageName of the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  T updatePackageName(final String packageName);

  /**
   * <em><strong>Commits</strong></em> the <em>currently uncommitted</em> changes as a new
   * <em>class</em>. The {@code semVer} of the new <em>class</em> will be set
   * <em><strong>automatically</strong></em>.
   *
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  T commit();

  /**
   * <em><strong>Commits</strong></em> the <em>currently uncommitted</em> changes as a new
   * <em>class</em>. The {@code semVer} of the new <em>class</em> will be set according to the given
   * {@code change}.
   *
   * @param change for the new {@code semVer}.
   * @return <em>this</em> {@code Builder}
   * @throws IllegalArgumentException if {@code change} is {@code null}.
   * @since 0.6.0
   */
  @Contract("null -> fail; _ -> this")
  default T commit(final Change change) {
    return commit(getSemVer().incrementVersion(change));
  }

  /**
   * <em><strong>Commits</strong></em> the <em>currently uncommitted</em> changes as a new
   * <em>class</em>. The {@code semVer} of the new <em>class</em> will be set to the given {@code
   * semVer}.
   *
   * @param semVer for the new <em>class</em>.
   * @return <em>this</em> {@code Builder}.
   * @since 0.6.0
   */
  T commit(final SemVer semVer);
}
