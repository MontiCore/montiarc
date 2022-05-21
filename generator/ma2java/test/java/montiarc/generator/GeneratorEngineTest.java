/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import org.apache.commons.io.FileUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Holds test for {@link GeneratorEngine}
 */
public class GeneratorEngineTest {

  protected final static String TEST_TARGET_PATH = "target/generated-test-sources/generator-engine/";

  protected final static String TEST_TARGET_SHADOW = "target/generated-test-sources/generator-engine-shadow/";

  protected final static String TEST_RESOURCE_PATH = "test/resources/generator-engine/";

  protected GeneratorSetup setup;

  protected GeneratorSetup getSetup() {
    return this.setup;
  }

  @BeforeEach
  public void setUp() {
    this.setup = new GeneratorSetup();
    this.setup.setOutputDirectory(new File(TEST_TARGET_PATH));
    this.setup.setAdditionalTemplatePaths(Collections.singletonList(new File(TEST_RESOURCE_PATH)));
  }

  @AfterEach
  public void cleanUp() throws IOException {
    FileUtils.deleteDirectory(new File(TEST_TARGET_PATH));
    FileUtils.deleteDirectory(new File(TEST_TARGET_SHADOW));
  }

  /**
   * Method under test {@link GeneratorEngine#generate(String, Formatter, Path, ASTNode, Object...)}
   */
  @ParameterizedTest
  @MethodSource("targetAndExpectedFileProvider")
  public void shouldGenerateFile(@NotNull String target, @NotNull String expected) throws FormatterException {
    Preconditions.checkNotNull(target);
    Preconditions.checkArgument(!target.isEmpty());
    Preconditions.checkArgument(!Paths.get(expected).toFile().exists());

    // Given
    GeneratorEngine engine = new GeneratorEngine(this.getSetup());

    // When
    engine.generate("Empty.ftl", new Formatter(), Paths.get(target), Mockito.mock(ASTCNode.class));

    // Then
    Assertions.assertTrue(Paths.get(expected).toFile().exists());
  }

  /**
   * Method under test {@link GeneratorEngine#generateNoA(String, Formatter, Path, Object...)}
   */
  @ParameterizedTest
  @MethodSource("targetAndExpectedFileProvider")
  public void shouldGenerateFileNoA(@NotNull String target, @NotNull String expected) throws FormatterException {
    Preconditions.checkNotNull(target);
    Preconditions.checkArgument(!target.isEmpty());
    Preconditions.checkArgument(!Paths.get(expected).toFile().exists());

    // Given
    GeneratorEngine engine = new GeneratorEngine(this.getSetup());

    // When
    engine.generateNoA("Empty.ftl", new Formatter(), Paths.get(target));

    // Then
    Assertions.assertTrue(Paths.get(expected).toFile().exists());
  }

  protected static Stream<Arguments> targetAndExpectedFileProvider() {
    return Stream.of(
      Arguments.of("A", TEST_TARGET_PATH + "A"),
      Arguments.of("A.java", TEST_TARGET_PATH + "A.java"),
      Arguments.of("a/b/A.java", TEST_TARGET_PATH + "a/b/A.java"),
      Arguments.of(Paths.get(TEST_TARGET_SHADOW + "A").toAbsolutePath().toString(), TEST_TARGET_SHADOW + "A"));
  }

  /**
   * Method under test {@link GeneratorEngine#generate(String, Formatter, Path, ASTNode, Object...)}
   */
  @Test
  public void generateShouldThrowException() {
    // Given
    GeneratorEngine engine = new GeneratorEngine(this.getSetup());

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> engine.generate(null, new Formatter(), Paths.get("A"), Mockito.mock(ASTCNode.class)));
    Assertions.assertThrows(NullPointerException.class,
      () -> engine.generate("Empty.ftl", (Formatter) null, Paths.get("A"), Mockito.mock(ASTCNode.class)));
    Assertions.assertThrows(NullPointerException.class,
      () -> engine.generate("Empty.ftl", new Formatter(), null, Mockito.mock(ASTCNode.class)));
    Assertions.assertThrows(NullPointerException.class,
      () -> engine.generate("Empty.ftl", new Formatter(), Paths.get("A"), null));
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> engine.generate("", new Formatter(), Paths.get("A"), Mockito.mock(ASTCNode.class)));
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> engine.generate("Empty.ftl", new Formatter(), Paths.get(""), Mockito.mock(ASTCNode.class)));
  }

  /**
   * Method under test {@link GeneratorEngine#generateNoA(String, Formatter, Path, Object...)}
   */
  @Test
  public void generateNoAShouldThrowException() {
    // Given
    GeneratorEngine engine = new GeneratorEngine(this.getSetup());

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> engine.generateNoA(null, new Formatter(), Paths.get("A")));
    Assertions.assertThrows(NullPointerException.class,
      () -> engine.generateNoA("Empty.ftl", (Formatter) null, Paths.get("A")));
    Assertions.assertThrows(NullPointerException.class,
      () -> engine.generateNoA("Empty.ftl", new Formatter(), null));
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> engine.generateNoA("", new Formatter(), Paths.get("A")));
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> engine.generateNoA("Empty.ftl", new Formatter(), Paths.get("")));
  }

  /**
   * Method under test {@link GeneratorEngine#GeneratorEngine(GeneratorSetup)}
   */
  @Test
  public void constructorShouldThrowException() {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> new GeneratorEngine(null));
  }

  /**
   * Method under test {@link GeneratorEngine#GeneratorEngine(GeneratorSetup)}
   */
  @Test
  public void constructorShouldCreateAsExpected() {
    // Given
    GeneratorSetup setup = new GeneratorSetup();

    // When
    GeneratorEngine engine = new GeneratorEngine(setup);

    // Then
    Assertions.assertEquals(setup, engine.getSetup());
  }
}