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
package io.github.chrimle.classforge.test.utils;

import java.io.IOException;
import java.nio.file.*;
import javax.tools.*;

public class JavaSourceCompiler {
  public static void compile(Path javaFile) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      throw new IllegalStateException(
          "No system Java compiler available. Are you running on a JRE instead of a JDK?");
    }

    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    Iterable<? extends JavaFileObject> compilationUnits =
        fileManager.getJavaFileObjects(javaFile.toFile());

    boolean success =
        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
    fileManager.close();

    if (!success) {
      throw new RuntimeException("Compilation failed for: " + javaFile);
    }
  }
}
