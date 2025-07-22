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

import io.github.chrimle.classforge.internal.ExceptionFactory;
import io.github.chrimle.classforge.internal.FileWriter;
import io.github.chrimle.semver.Change;
import io.github.chrimle.semver.SemVer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class for building and generating Java classes.
 *
 * @since 0.1.0
 * @author Chrimle
 */
@API(status = API.Status.STABLE, since = "0.6.0")
public abstract sealed class AbstractBuilder<T extends Builder<T>> implements Builder<T>
    permits ClassBuilder, EnumBuilder {

  private static final Predicate<String> directoryValidator =
      string -> Optional.ofNullable(string).isPresent();
  private static final Predicate<String> classNameValidator =
      string ->
          Optional.ofNullable(string)
              .filter(className -> className.matches(ClassForge.VALID_CLASS_NAME_REGEX))
              .isPresent();
  private static final Predicate<String> packageNameValidator =
      string ->
          Optional.ofNullable(string)
              .filter(packageName -> !packageName.isBlank())
              .map(packageName -> packageName.matches(ClassForge.VALID_PACKAGE_NAME_REGEX))
              .orElse(true);

  /** The collection of <em>previously committed</em> classes. */
  protected final Set<String> reservedClassNames = new HashSet<>();

  /** The {@code semVer} of the <em>previously committed</em> class. Starts at {@code 0.0.0}. */
  protected SemVer semVer = new SemVer(0, 0, 0);

  /** The {@code versionPlacement} of the <em>currently uncommitted</em> class. */
  protected VersionPlacement versionPlacement = VersionPlacement.NONE;

  /** The {@code directory} of the <em>currently uncommitted</em> class. */
  protected String directory;

  /** The {@code packageName} of the <em>currently uncommitted</em> class. */
  protected String packageName;

  /** The {@code className} of the <em>currently uncommitted</em> class. */
  protected String className;

  /** {@inheritDoc} */
  @Override
  @NotNull
  public SemVer getSemVer() {
    return semVer;
  }

  /** {@inheritDoc} */
  @Contract("null -> fail; _ -> this")
  @Override
  public T setSemVer(final SemVer semVer) {
    if (semVer == null) {
      throw ExceptionFactory.nullException("semVer");
    }
    this.semVer = semVer;
    return self();
  }

  /** {@inheritDoc} */
  @Override
  @Contract("_ -> this")
  public T setVersionPlacement(final VersionPlacement versionPlacement) {
    this.versionPlacement = Optional.ofNullable(versionPlacement).orElse(VersionPlacement.NONE);
    return self();
  }

  /** {@inheritDoc} */
  @Override
  @Contract("null -> fail; _ -> this")
  public T updateDirectory(final String directory) {
    validateDirectory(directory);
    this.directory = directory;
    return self();
  }

  /** {@inheritDoc} */
  @Override
  @Contract("null -> fail; _ -> this")
  public T updatePackageName(final String packageName) {
    validatePackageName(packageName);
    this.packageName = packageName;
    return self();
  }

  /** {@inheritDoc} */
  @Override
  @Contract("null -> fail; _ -> this")
  public T updateClassName(final String className) {
    validateClassName(className);
    this.className = className;
    return self();
  }

  /** {@inheritDoc} */
  @Override
  @Contract(" -> this")
  public T commit() {
    return commit(determineSemVerChange());
  }

  /** {@inheritDoc} */
  @Override
  @Contract("null -> fail; _ -> this")
  public T commit(final SemVer semVer) {
    if (semVer == null) {
      throw ExceptionFactory.nullException("semVer");
    }
    validateClass();
    final String fullyQualifiedClassName = resolveFullyQualifiedClassName(semVer);
    if (reservedClassNames.contains(fullyQualifiedClassName)) {
      throw new IllegalStateException(
          "Class `%s` has already been generated!".formatted(fullyQualifiedClassName));
    }
    generateClassFile(semVer);
    reservedClassNames.add(fullyQualifiedClassName);
    this.semVer = semVer;
    return self();
  }

  private void validateClass() {
    validateDirectory(this.directory);
    validatePackageName(this.packageName);
    validateClassName(this.className);
    validateAdditionalPredicates();
  }

  /**
   * Determines the {@link Change} for the <em>currently uncommitted</em> changes.
   *
   * @return the {@code Change}.
   */
  protected Change determineSemVerChange() {
    return Change.MAJOR;
  }

  /**
   * Returns <em>this</em> as {@link T}.
   *
   * @return <em>this</em>.
   */
  protected abstract T self();

  /**
   * Validates additional {@link Predicate}s for determining the validity of the <em>currently
   * uncommitted</em> class.
   */
  protected void validateAdditionalPredicates() {}

  /**
   * Generates the complete file contents for a {@code .java} file for the <em>currently
   * uncommitted</em> class.
   *
   * @param semVer for the new class.
   * @return the file contents as a {@code String}.
   */
  protected abstract String generateFileContent(final SemVer semVer);

  /**
   * Generates a {@code .java} class file for the <em>currently uncommitted</em> class.
   *
   * @param semVer for the new class.
   */
  protected void generateClassFile(final SemVer semVer) {
    FileWriter.writeToFile(
        directory, resolveFullyQualifiedClassName(semVer), generateFileContent(semVer));
  }

  /**
   * Resolves the <em>Fully Qualified Class Name (FQCN)</em> for the <em>currently uncommitted</em>
   * class.
   *
   * <p><strong>Example:</strong> {@code module.sub_module.ExampleClass} or {@code
   * AnotherExampleClass}.
   *
   * @param semVer for the new class.
   * @return the <em>FQCN</em>.
   */
  protected String resolveFullyQualifiedClassName(final SemVer semVer) {
    return Optional.ofNullable(resolveEffectivePackageName(semVer))
        .filter(pN -> !pN.isBlank())
        .map(pN -> String.join(".", pN, className))
        .orElse(className);
  }

  /**
   * Resolves the <em>effective package name</em> for the <em>currently uncommitted</em> class.
   *
   * @param semVer for the class.
   * @return the <em>effective package name</em>.
   */
  protected String resolveEffectivePackageName(final SemVer semVer) {
    return switch (versionPlacement) {
      case NONE -> packageName;
      case PACKAGE_NAME_WITH_COMPLETE_VERSION -> {
        final String versionSubPackage = semVer.toCompleteVersionString().replace(".", "_");

        yield Optional.ofNullable(packageName)
            .filter(pN -> !pN.isBlank())
            .map(pN -> String.join(".", pN, versionSubPackage))
            .orElse(versionSubPackage);
      }
      case PACKAGE_NAME_WITH_SHORTENED_VERSION -> {
        final String versionSubPackage = semVer.toShortVersionString().replace(".", "_");

        yield Optional.ofNullable(packageName)
            .filter(pN -> !pN.isBlank())
            .map(pN -> String.join(".", pN, versionSubPackage))
            .orElse(versionSubPackage);
      }
    };
  }

  private static void validateDirectory(final String directory) {
    if (!directoryValidator.test(directory)) {
      throw ExceptionFactory.nullException("directory");
    }
  }

  private static void validatePackageName(final String packageName) {
    if (!packageNameValidator.test(packageName)) {
      throw ExceptionFactory.notMatchingRegExException(
          "packageName", ClassForge.VALID_PACKAGE_NAME_REGEX);
    }
  }

  private static void validateClassName(final String className) {
    if (!classNameValidator.test(className)) {
      throw ExceptionFactory.notMatchingRegExException(
          "className", ClassForge.VALID_CLASS_NAME_REGEX);
    }
  }
}
