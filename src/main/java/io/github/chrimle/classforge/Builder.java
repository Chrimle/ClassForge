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

import io.github.chrimle.classforge.semver.SemVer;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;

/**
 * Builder of a Java <i>class</i> - which <b>MAY</b> generate a {@code class}, {@code enum} or
 * {@code record}.
 *
 * @since 0.1.0
 * @author Chrimle
 */
@API(status = API.Status.STABLE, since = "0.6.0")
public sealed interface Builder<T extends Builder<T>> permits AbstractBuilder {

  /**
   * The placement of version information in generated <em>classes</em>.
   *
   * @since 0.3.0
   * @author Chrimle
   */
  enum VersionPlacement {
    /**
     * <strong>Default.</strong> Version information is excluded.
     *
     * @since 0.3.0
     */
    NONE,
    /**
     * The <em>complete</em> version is included in the {@code package}-name - always consisting of
     * {@code v{major}_{minor}_{patch}}.
     *
     * <p><strong>Example: </strong>{@code 1.2.3}
     *
     * <pre>{@code package com.example.v1_2_3;}</pre>
     *
     * <p><strong>Example: </strong>{@code 1.3.0}
     *
     * <pre>{@code package com.example.v1_3_0;}</pre>
     *
     * <p><strong>Example: </strong>{@code 2.0.0}
     *
     * <pre>{@code package com.example.v2_0_0;}</pre>
     *
     * @since 0.4.0
     */
    PACKAGE_NAME_WITH_COMPLETE_VERSION,
    /**
     * The <em>shortened</em> version is included in the {@code package}-name - omitting trailing
     * zeros i.e. {@code v2}.
     *
     * <p><strong>Example: </strong>{@code 1.2.3}
     *
     * <pre>{@code package com.example.v1_2_3;}</pre>
     *
     * <p><strong>Example: </strong>{@code 1.3.0}
     *
     * <pre>{@code package com.example.v1_3;}</pre>
     *
     * <p><strong>Example: </strong>{@code 2.0.0}
     *
     * <pre>{@code package com.example.v2;}</pre>
     *
     * @since 0.4.0
     */
    PACKAGE_NAME_WITH_SHORTENED_VERSION
  }

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
  default T commit(final SemVer.Change change) {
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
