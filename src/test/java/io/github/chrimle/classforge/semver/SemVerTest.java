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

  @Nested
  class ToShortVersionStringTests {

    @Test
    void testMajor() {
      final var semVer = new SemVer(1, 0, 0);
      assertEquals("v1", semVer.toShortVersionString());
    }

    @Test
    void testMajorAndMinor() {
      final var semVer = new SemVer(1, 2, 0);
      assertEquals("v1.2", semVer.toShortVersionString());
    }

    @Test
    void testMajorAndMinorAndPatch() {
      final var semVer = new SemVer(1, 2, 3);
      assertEquals("v1.2.3", semVer.toShortVersionString());
    }
  }
}
