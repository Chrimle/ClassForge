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
