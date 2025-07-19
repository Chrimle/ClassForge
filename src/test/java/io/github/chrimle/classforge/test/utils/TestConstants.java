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

import io.github.chrimle.classforge.ClassForge;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class TestConstants {
  public static final String DIRECTORY = "target/generated-test-sources";
  public static final String PACKAGE_NAME = "io.github.chrimle.classforge";

  public static final String METHOD_SOURCE_RESERVED_KEYWORDS =
      "io.github.chrimle.classforge.test.utils.TestConstants#methodSourceReservedKeywords";

  @SuppressWarnings("unused")
  public static Stream<Arguments> methodSourceReservedKeywords() {
    return ClassForge.RESERVED_KEYWORDS.stream().map(Arguments::of);
  }
}
