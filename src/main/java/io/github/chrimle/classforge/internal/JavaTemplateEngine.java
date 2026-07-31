/*
 * Copyright 2025-2026 Chrimle
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

package io.github.chrimle.classforge.internal;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.chrimle.classforge.Model;
import io.github.chrimle.classforge.classes.ClassModel;
import io.github.chrimle.classforge.enums.EnumModel;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.apiguardian.api.API;

/**
 * Wrapper of <i>Java Template Engine</i>.
 *
 * @since 0.11.0
 */
@API(status = API.Status.INTERNAL, since = "0.11.0")
public final class JavaTemplateEngine {

  private static final CodeResolver codeResolver;
  private static final TemplateEngine templateEngine;

  private JavaTemplateEngine() {}

  static {
    try {
      codeResolver =
          new DirectoryCodeResolver(Path.of(ClassLoader.getSystemResource("jte").toURI()));
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Could not load JTE-templates!", e);
    }
    templateEngine = TemplateEngine.create(codeResolver, ContentType.Plain);
  }

  public static StringOutput generateModelAsString(final Model model) {
    if (model instanceof EnumModel<?> enumModel) {
      return generateEnumClassAsString(enumModel);
    }
    if (model instanceof ClassModel classModel) {
      return generateClassAsString(classModel);
    }
    throw new UnsupportedOperationException("Failed to generate code from Model: " + model);
  }

  private static StringOutput generateEnumClassAsString(final EnumModel<?> enumModel) {
    final var output = new StringOutput();
    templateEngine.render("enum.jte", enumModel, output);
    return output;
  }

  public static StringOutput generateClassAsString(final ClassModel classModel) {
    final var output = new StringOutput();
    templateEngine.render("class.jte", classModel, output);
    return output;
  }
}
