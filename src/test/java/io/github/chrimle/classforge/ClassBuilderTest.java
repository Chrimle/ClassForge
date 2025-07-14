package io.github.chrimle.classforge;

import static org.junit.jupiter.api.Assertions.*;

import io.github.chrimle.classforge.test.utils.DynamicClassLoader;
import io.github.chrimle.classforge.test.utils.JavaSourceCompiler;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ClassBuilderTest {

  public static final String GENERATED_TEST_SOURCES = "target/generated-test-sources";
  public static final String PACKAGE_NAME = "io.github.chrimle.classforge";
  public static final String PACKAGE_NAME_DIRECTORY = PACKAGE_NAME.replace(".", "/");

  @Test
  void test() throws Exception {
    final Path outputDir = Path.of(GENERATED_TEST_SOURCES);
    new ClassBuilder(outputDir.toString(), PACKAGE_NAME, "ClassName").build();

    JavaSourceCompiler.compile(outputDir.resolve(PACKAGE_NAME_DIRECTORY + "/ClassName.java"));

    final Class<?> clazz = DynamicClassLoader.loadClass(outputDir, PACKAGE_NAME + ".ClassName");
    assertNotNull(clazz);
  }
}
