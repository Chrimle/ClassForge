package io.github.chrimle.classforge;

/**
 * Builder of a Java <i>class</i> - which <b>MAY</b> generate a {@code class}, {@code enum} or
 * {@code record}.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public sealed interface Builder<T extends Builder<T>> permits AbstractBuilder {

  /**
   * The placement of version information in generated <em>classes</em>.
   *
   * @since 0.3.0
   * @author Chrimle
   */
  enum VersionPlacement {
    /** <strong>Default.</strong> Version information is excluded. */
    NONE,
    /**
     * Version information is included in the {@code package}-name.
     *
     * <p><strong>Example: </strong>{@code 1.2.3}
     *
     * <pre>{@code package com.example.v1_2_3;}
     * </pre>
     */
    PACKAGE_NAME
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
   * <em><strong>Commits</strong></em> the <em>currently uncommitted</em> class.
   *
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  T commit();
}
