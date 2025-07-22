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

import static io.github.chrimle.classforge.test.utils.TestConstants.DIRECTORY;
import static io.github.chrimle.classforge.test.utils.TestConstants.PACKAGE_NAME;
import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.Builder.VersionPlacement;
import io.github.chrimle.classforge.internal.ExceptionFactory;
import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import io.github.chrimle.classforge.test.utils.TestConstants;
import io.github.chrimle.semver.Change;
import io.github.chrimle.semver.SemVer;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BuilderTests {

  static <T extends AbstractBuilder<?>> T instantiateBuilder(final Class<T> builderClass) {
    if (builderClass == ClassBuilder.class) {
      return builderClass.cast(ClassForge.newClassBuilder());
    }
    if (builderClass == EnumBuilder.class) {
      return builderClass.cast(ClassForge.newEnumBuilder());
    }
    throw new UnsupportedOperationException();
  }

  static Class<?> compileAndLoadClass(final String className) throws Exception {
    compileClass(className);
    return loadClass(className);
  }

  static Class<?> compileAndLoadClass(final String packageName, final String className)
      throws Exception {
    final String fullyQualifiedName = String.join(".", packageName, className);
    compileClass(fullyQualifiedName);
    return loadClass(fullyQualifiedName);
  }

  static void compileClass(final String fullyQualifiedName) throws IOException {
    JavaSourceCompiler.compile(
        Path.of(TestConstants.DIRECTORY).resolve(fullyQualifiedName.replace(".", "/") + ".java"));
  }

  static Class<?> loadClass(final String fullyQualifiedName) throws Exception {
    return DynamicClassLoader.loadClass(Path.of(TestConstants.DIRECTORY), fullyQualifiedName);
  }

  @Nested
  class ClassNameTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testWithoutClassName(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () ->
                  instantiateBuilder(builderClass)
                      .updateDirectory(TestConstants.DIRECTORY)
                      .commit());
      assertEquals(
          ExceptionFactory.notMatchingRegExException("className", ClassForge.VALID_CLASS_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testNull(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).updateClassName(null));
      assertEquals(
          ExceptionFactory.notMatchingRegExException("className", ClassForge.VALID_CLASS_NAME_REGEX)
              .getMessage(),
          exception.getMessage());
    }
  }

  @Nested
  class PackageNameTests {

    /**
     * A {@code packageName} set to {@code null} should be allowed, as it should be treated as not
     * belonging to any package.
     */
    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testNullValue(final Class<? extends AbstractBuilder<?>> builderClass) throws Exception {
      final var className = builderClass.getSimpleName() + "_Test_NullPackageName";
      assertDoesNotThrow(
          () ->
              instantiateBuilder(builderClass)
                  .updateDirectory(TestConstants.DIRECTORY)
                  .updatePackageName(null)
                  .updateClassName(className)
                  .commit());

      assertNotNull(compileAndLoadClass(className));
    }

    /**
     * An empty {@code packageName} should be allowed, as it should be treated as not belonging to
     * any package.
     */
    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testEmptyValue(final Class<? extends AbstractBuilder<?>> builderClass) throws Exception {
      final var className = builderClass.getSimpleName() + "_Test_EmptyPackageName";
      assertDoesNotThrow(
          () ->
              instantiateBuilder(builderClass)
                  .updateDirectory(TestConstants.DIRECTORY)
                  .updatePackageName("")
                  .updateClassName(className)
                  .commit());

      assertNotNull(compileAndLoadClass(className));
    }
  }

  @Nested
  class DirectoryTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testInvalidValues(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).updateDirectory(null));
      assertEquals(
          ExceptionFactory.nullException("directory").getMessage(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testCommittingClassWithoutDirectory(
        final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () ->
                  instantiateBuilder(builderClass)
                      .updateClassName("ClassWithoutDirectory")
                      .commit());
      assertEquals(
          ExceptionFactory.nullException("directory").getMessage(), exception.getMessage());
    }
  }

  @Nested
  class SemVerTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testNullSemVerThrows(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).setSemVer(null));
      assertEquals(ExceptionFactory.nullException("semVer").getMessage(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testValidChange(final Class<? extends AbstractBuilder<?>> builderClass) throws Exception {
      final var className = builderClass.getSimpleName() + "_Test_CustomCommitChange";
      final var classBuilder =
          instantiateBuilder(builderClass)
              .updateDirectory(DIRECTORY)
              .updatePackageName(PACKAGE_NAME)
              .updateClassName(className)
              .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION);
      assertDoesNotThrow(() -> classBuilder.setSemVer(new SemVer(42, 7, 10)));
      assertDoesNotThrow(() -> classBuilder.commit(Change.PATCH));

      assertNotNull(compileAndLoadClass(PACKAGE_NAME + ".v42_7_11", className));
    }

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testValidSemVer(final Class<? extends AbstractBuilder<?>> builderClass) throws Exception {
      final var className = builderClass.getSimpleName() + "_Test_CustomCommitSemVer";
      final var classBuilder =
          instantiateBuilder(builderClass)
              .updateDirectory(DIRECTORY)
              .updatePackageName(PACKAGE_NAME)
              .updateClassName(className)
              .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION);
      assertDoesNotThrow(() -> classBuilder.commit(new SemVer(3, 2, 1)));

      assertNotNull(compileAndLoadClass(PACKAGE_NAME + ".v3_2_1", className));
    }
  }

  @Nested
  class CommitTests {

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testNullChange(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).commit((Change) null));
      assertEquals(ExceptionFactory.nullException("change").getMessage(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
    void testNullSemVer(final Class<? extends AbstractBuilder<?>> builderClass) {
      final var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> instantiateBuilder(builderClass).commit((SemVer) null));
      assertEquals(ExceptionFactory.nullException("semVer").getMessage(), exception.getMessage());
    }
  }

  @Nested
  class VersionPlacementTests {

    @Nested
    class CompletePackageNameTests {

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testDefaultCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_DefaultCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit() // Version 1.0.0
            .commit() // Version 2.0.0
            .commit(); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3_0_0", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMajorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MajorCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(Change.MAJOR) // Version 1.0.0
            .commit(Change.MAJOR) // Version 2.0.0
            .commit(Change.MAJOR); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2_0_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3_0_0", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMinorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MinorCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(Change.MINOR) // Version 0.1.0
            .commit(Change.MINOR) // Version 0.2.0
            .commit(Change.MINOR); // Version 0.3.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_1_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_2_0", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_3_0", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testPatchCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_PatchCompletePackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(Builder.VersionPlacement.PACKAGE_NAME_WITH_COMPLETE_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(Change.PATCH) // Version 0.0.1
            .commit(Change.PATCH) // Version 0.0.2
            .commit(Change.PATCH); // Version 0.0.3

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_3", className));
      }
    }

    @Nested
    class ShortPackageNameTests {

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testDefaultCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_DefaultShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit() // Version 1.0.0
            .commit() // Version 2.0.0
            .commit(); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMajorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MajorShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(Change.MAJOR) // Version 1.0.0
            .commit(Change.MAJOR) // Version 2.0.0
            .commit(Change.MAJOR); // Version 3.0.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v3", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testMinorCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_MinorShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(Change.MINOR) // Version 0.1.0
            .commit(Change.MINOR) // Version 0.2.0
            .commit(Change.MINOR); // Version 0.3.0

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_3", className));
      }

      @ParameterizedTest
      @ValueSource(classes = {ClassBuilder.class, EnumBuilder.class})
      void testPatchCommits(final Class<? extends AbstractBuilder<?>> builderClass)
          throws Exception {
        final var className = builderClass.getSimpleName() + "_Test_PatchShortPackageName";
        instantiateBuilder(builderClass)
            .setVersionPlacement(VersionPlacement.PACKAGE_NAME_WITH_SHORTENED_VERSION)
            .updateDirectory(TestConstants.DIRECTORY)
            .updatePackageName(TestConstants.PACKAGE_NAME)
            .updateClassName(className)
            .commit(Change.PATCH) // Version 0.0.1
            .commit(Change.PATCH) // Version 0.0.2
            .commit(Change.PATCH); // Version 0.0.3

        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_1", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_2", className));
        assertNotNull(compileAndLoadClass(TestConstants.PACKAGE_NAME + ".v0_0_3", className));
      }
    }
  }
}
