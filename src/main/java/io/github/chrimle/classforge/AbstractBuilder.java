package io.github.chrimle.classforge;

import io.github.chrimle.classforge.utils.FileWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract sealed class AbstractBuilder implements Builder permits ClassBuilder {

  protected final Set<String> reservedClassNames = new HashSet<>();
  protected String absolutePathPrefix;
  protected String packageName;
  protected String className;

  protected abstract String generateFileContent();

  protected void generateClassFile() {
    FileWriter.writeToFile(
        absolutePathPrefix, resolveFullyQualifiedClassName(), generateFileContent());
  }

  protected String resolveFullyQualifiedClassName() {
    return Optional.ofNullable(packageName)
        .filter(pN -> !pN.isBlank())
        .map(pN -> String.join(".", pN, className))
        .orElse(className);
  }
}
