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
   * @since 0.3.0
   * @throws IllegalArgumentException if {@code major}, {@code minor} or {@code patch} is less than
   *     {@code 0}.
   */
  public SemVer {
    if (major < 0) throw new IllegalArgumentException("SemVer.major MUST NOT be less than 0");
    if (minor < 0) throw new IllegalArgumentException("SemVer.minor MUST NOT be less than 0");
    if (patch < 0) throw new IllegalArgumentException("SemVer.patch MUST NOT be less than 0");
  }

  /**
   * Creates a new {@link SemVer} with the corresponding version incremented.
   *
   * @param change for determining the new {@code SemVer}.
   * @return the new {@code SemVer}.
   * @since 0.3.0
   * @throws IllegalArgumentException if {@code change} is {@code null}.
   */
  public SemVer incrementVersion(final Change change) {
    if (change == null) throw new IllegalArgumentException("`change` MUST NOT be null!");
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

  /**
   * Returns <em>this</em> {@link SemVer} as a {@code String} in the format: {@code
   * v{major}.{minor}.{patch}}.
   *
   * @return the formatted {@code String}.
   * @since 0.3.0
   */
  @Override
  public String toString() {
    return "v%d.%d.%d".formatted(major, minor, patch);
  }
}
