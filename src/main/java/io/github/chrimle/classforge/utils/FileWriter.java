package io.github.chrimle.classforge.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileWriter {

  public static void writeToFile(
      final String absolutePathPrefix, final String fullyQualifiedClassName, final String content) {
    writeToFile(
        Path.of(
            "%s/%s.java".formatted(absolutePathPrefix, fullyQualifiedClassName.replace(".", "/"))),
        content);
  }

  public static void writeToFile(final Path filePath, final String content) {
    try {
      Files.createDirectories(filePath.getParent());
      Files.writeString(
          filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
