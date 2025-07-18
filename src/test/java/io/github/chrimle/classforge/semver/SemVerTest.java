package io.github.chrimle.classforge.semver;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.semver.SemVer.Change;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SemVerTest {

  @Nested
  class MajorTests {

    @Test
    void testZeroThrows() {
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new SemVer(-1, 0, 0));
      assertEquals("SemVer.major MUST NOT be less than 0", exception.getMessage());
    }

    @Test
    void testIncrement() {
      final var semVer = new SemVer(0, 0, 0);
      final var updatedSemVer = semVer.incrementMajor();
      assertEquals(1, updatedSemVer.major());
    }

    @Test
    void testIncrementWithChange() {
      final var semVer = new SemVer(0, 0, 0);
      final var updatedSemVer = semVer.incrementVersion(Change.MAJOR);
      assertEquals(1, updatedSemVer.major());
    }
  }

  @Nested
  class MinorTests {

    @Test
    void testZeroThrows() {
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new SemVer(0, -1, 0));
      assertEquals("SemVer.minor MUST NOT be less than 0", exception.getMessage());
    }

    @Test
    void testIncrement() {
      final var semVer = new SemVer(0, 0, 0);
      final var updatedSemVer = semVer.incrementMinor();
      assertEquals(1, updatedSemVer.minor());
    }

    @Test
    void testIncrementWithChange() {
      final var semVer = new SemVer(0, 0, 0);
      final var updatedSemVer = semVer.incrementVersion(Change.MINOR);
      assertEquals(1, updatedSemVer.minor());
    }
  }

  @Nested
  class PatchTests {

    @Test
    void testZeroThrows() {
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new SemVer(0, 0, -1));
      assertEquals("SemVer.patch MUST NOT be less than 0", exception.getMessage());
    }

    @Test
    void testIncrement() {
      final var semVer = new SemVer(0, 0, 0);
      final var updatedSemVer = semVer.incrementPatch();
      assertEquals(1, updatedSemVer.patch());
    }

    @Test
    void testIncrementWithChange() {
      final var semVer = new SemVer(0, 0, 0);
      final var updatedSemVer = semVer.incrementVersion(Change.PATCH);
      assertEquals(1, updatedSemVer.patch());
    }
  }
}
