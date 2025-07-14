package io.github.chrimle.classforge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class ClassBuilder {

  private final String absolutePathPrefix;
  private final String packageName;
  private final String className;

  public ClassBuilder(
      final String absolutePathPrefix, final String packageName, final String className) {
    this.absolutePathPrefix = absolutePathPrefix;
    this.packageName = packageName;
    this.className = className;
  }

  public void build() {
    try {
      final String code =
          """
          package %s;

          public class %s {

          }
          """
              .formatted(packageName, className);

      final Path outputPath =
          Path.of(
              absolutePathPrefix + "/" + packageName.replace('.', '/') + "/", className + ".java");
      Files.createDirectories(outputPath.getParent());
      Files.writeString(
          outputPath, code, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
