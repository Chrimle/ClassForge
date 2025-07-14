package io.github.chrimle.classforge;

import io.github.chrimle.classforge.utils.FileWriter;
import java.util.Objects;
import java.util.Optional;

public final class ClassBuilder {

  private final String absolutePathPrefix;
  private final String fullyQualifiedClassName;
  private final String packageName;
  private final String className;

  public ClassBuilder(
      final String absolutePathPrefix, final String packageName, final String className) {
    this.absolutePathPrefix =
        Objects.requireNonNull(absolutePathPrefix, "`absolutePathPrefix` MUST NOT be `null`!");
    this.packageName = packageName;
    this.className = className;
    this.fullyQualifiedClassName =
        Optional.ofNullable(packageName).map(p -> String.join(".", p, className)).orElse(className);
  }

  public void build() {
    final String code =
        """
        package %s;

        public class %s {

        }
        """
            .formatted(packageName, className);
    FileWriter.writeToFile(absolutePathPrefix, fullyQualifiedClassName, code);
  }
}
