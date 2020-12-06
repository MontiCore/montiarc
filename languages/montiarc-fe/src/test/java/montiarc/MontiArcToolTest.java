package montiarc;

import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc._symboltable.MontiArcGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Holds tests for methods of {@link MontiArcTool}.
 */
public class MontiArcToolTest extends AbstractTest {

  protected static final String TEST_PATH = Paths.get("montiarc", "tool").toString();

  protected static Stream<Arguments> validModelPathAndExpectedValuesProvider() {
    return Stream.of(Arguments.of(Paths.get("valid", "example1").toString(), 3),
      Arguments.of(Paths.get("valid", "example2").toString(), 3),
      Arguments.of(Paths.get("valid", "example3").toString(), 3));
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @ParameterizedTest
  @MethodSource("validModelPathAndExpectedValuesProvider")
  public void shouldProcessValidModels(@NotNull String modelPathName, int expNumModels) {
    //Given
    MontiArcTool tool = new MontiArcTool();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    MontiArcGlobalScope scope = tool.processModels(modelPath);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, scope.getSubScopes().size());
    Assertions.assertTrue(scope.getSubScopes().stream().allMatch(s -> s instanceof MontiArcArtifactScope));
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @ParameterizedTest
  @MethodSource("invalidModelPathProvider")
  public void shouldLogError(@NotNull String modelPathName) {
    //Given
    MontiArcTool tool = new MontiArcTool();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    MontiArcGlobalScope scope = tool.processModels(modelPath);

    //Then
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }

  protected static Stream<Arguments> invalidModelPathProvider() {
    return Stream.of(Arguments.of(Paths.get("invalid", "example1").toString()));
  }
}