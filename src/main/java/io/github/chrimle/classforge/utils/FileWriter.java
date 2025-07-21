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
package io.github.chrimle.classforge.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.apiguardian.api.API;

/**
 * Writes content to files - creating directories and files if needed.
 *
 * @since 0.1.0
 * @author Chrimle
 */
@API(since = "0.6.0", status = API.Status.INTERNAL, consumers = "io.github.chrimle.classforge")
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
