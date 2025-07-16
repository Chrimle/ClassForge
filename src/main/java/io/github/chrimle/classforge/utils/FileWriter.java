package io.github.chrimle.classforge.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Writes content to files - creating directories and files if needed.
 *
 * @since 0.1.0
 * @author Chrimle
 */
public class FileWriter {

  /**
   * Writes the {@code content} to a file at {@code directory} and the {@code package}-name from the
   * {@code fullyQualifiedClassName}.
   *
   * @param directory of the file.
   * @param fullyQualifiedClassName of the class.
   * @param content of the file.
   * @since 0.1.0
   */
  public static void writeToFile(
      final String directory, final String fullyQualifiedClassName, final String content) {
    writeToFile(
        Path.of("%s/%s.java".formatted(directory, fullyQualifiedClassName.replace(".", "/"))),
        content);
  }

  /**
   * Writes the {@code content} to a file at {@code filePath}. Creates the directory and file if
   * needed.
   *
   * @param filePath of the file.
   * @param content of the file.
   * @since 0.1.0
   */
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
