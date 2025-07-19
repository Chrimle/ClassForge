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
