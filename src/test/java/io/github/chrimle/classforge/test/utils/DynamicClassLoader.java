package io.github.chrimle.classforge.test.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class DynamicClassLoader {
  public static Class<?> loadClass(final Path compiledRoot, final String fullyQualifiedName)
      throws Exception {
    final URL[] urls = {compiledRoot.toUri().toURL()};
    try (URLClassLoader classLoader = new URLClassLoader(urls)) {
      return classLoader.loadClass(fullyQualifiedName);
    }
  }
}
