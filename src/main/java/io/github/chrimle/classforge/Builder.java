package io.github.chrimle.classforge;

/**
 * Builder of a Java <i>class</i> - which <b>MAY</b> generate a {@code class}, {@code enum} or
 * {@code record}.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public sealed interface Builder permits AbstractBuilder {

  /**
   * <em><strong>Updates</strong></em> the {@code absolutePathPrefix} of the <em>currently
   * uncommitted</em> class.
   *
   * @param absolutePathPrefix of the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  Builder updateAbsolutePathPrefix(final String absolutePathPrefix);

  /**
   * <em><strong>Updates</strong></em> the {@code className} of the <em>currently uncommitted</em>
   * class.
   *
   * @param className of the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  Builder updateClassName(final String className);

  /**
   * <em><strong>Updates</strong></em> the {@code packageName} of the <em>currently uncommitted</em>
   * class.
   *
   * @param packageName of the class.
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  Builder updatePackageName(final String packageName);

  /**
   * <em><strong>Commits</strong></em> the <em>currently uncommitted</em> class.
   *
   * @return <em>this</em> {@code Builder}.
   * @since 0.1.0
   */
  Builder commit();
}
