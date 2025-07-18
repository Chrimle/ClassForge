package io.github.chrimle.classforge.semver;

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
   * Constructs a <em>valid</em> {@link SemVer} instance.
   *
   * @param major version.
   * @param minor version.
   * @param patch version.
   * @since 0.3.0
   */
  public SemVer {
    if (major < 0) throw new IllegalArgumentException("SemVer.major MUST NOT be less than 0");
    if (minor < 0) throw new IllegalArgumentException("SemVer.minor MUST NOT be less than 0");
    if (patch < 0) throw new IllegalArgumentException("SemVer.patch MUST NOT be less than 0");
  }

  /**
   * Creates a new {@link SemVer} with the {@link #major}-version incremented.
   *
   * @since 0.3.0
   * @return the new {@code SemVer}.
   */
  public SemVer incrementMajor() {
    return new SemVer(major + 1, 0, 0);
  }

  /**
   * Creates a new {@link SemVer} with the {@link #minor}-version incremented.
   *
   * @since 0.3.0
   * @return the new {@code SemVer}.
   */
  public SemVer incrementMinor() {
    return new SemVer(major, minor + 1, 0);
  }

  /**
   * Creates a new {@link SemVer} with the {@link #patch}-version incremented.
   *
   * @since 0.3.0
   * @return the new {@code SemVer}.
   */
  public SemVer incrementPatch() {
    return new SemVer(major, minor, patch + 1);
  }
}
