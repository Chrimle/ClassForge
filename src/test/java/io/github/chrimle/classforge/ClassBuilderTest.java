package io.github.chrimle.classforge;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ClassBuilderTest {

  @Test
  void test() throws Exception {
    Path outputDir = Path.of("target/generated-test-sources");
    new ClassBuilder(outputDir.toString(), "packageName", "ClassName").build();

    JavaSourceCompiler.compile(outputDir.resolve("packageName/ClassName.java"));

    Class<?> clazz = DynamicClassLoader.loadClass(outputDir, "packageName.ClassName");
    assertNotNull(clazz);
  }
}
