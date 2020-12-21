package montiarc;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.MontiArcArtifactScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Holds tests for methods of {@link MontiArcTool}.
 */
public class MontiArcToolTest extends AbstractTest {

  protected static final String TEST_PATH = Paths.get("montiarc", "tool").toString();

  protected MontiArcTool maTool = new MontiArcTool();

  protected MontiArcTool getMATool() {
    return this.maTool;
  }

  protected static Stream<Path> steamValidMAModelFiles() {
    return Stream.of(Paths.get("valid", "example1", "Composed.arc"),
      Paths.get("valid", "example1", "InComp.arc"),
      Paths.get("valid", "example1", "OutComp.arc"));
  }

  protected static Stream<Arguments> validMAModelFileNameProvider() {
    return steamValidMAModelFiles().map(Path::toString).map(Arguments::of);
  }

  protected static Stream<Path> streamValidCDModelFiles() {
    return Stream.of(Paths.get("valid", "example3", "Colors.cd"));
  }

  protected static Stream<Arguments> validCDModelFileNameProvider() {
    return streamValidCDModelFiles().map(Path::toString).map(Arguments::of);
  }

  protected static Stream<Arguments> validMAModelPathAndExpectedValuesProvider() {
    return Stream.of(Arguments.of(Paths.get("valid", "example1").toString(), 3),
      Arguments.of(Paths.get("valid", "example2").toString(), 3),
      Arguments.of(Paths.get("valid", "example3").toString(), 3));
  }

  protected static Stream<Arguments> validCDModelPathAndExpectedValuesProvider() {
    return Stream.of(Arguments.of(Paths.get("valid", "example1").toString(), 0),
      Arguments.of(Paths.get("valid", "example2").toString(), 0),
      Arguments.of(Paths.get("valid", "example3").toString(), 1));
  }

  protected static Stream<Arguments> invalidModelPathProvider() {
    return Stream.of(Arguments.of(Paths.get("invalid", "example1").toString()));
  }

  /**
   * Method under test {@link MontiArcTool#parseMAModel(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validMAModelFileNameProvider")
  public void shouldParseMAModelFromFile(@NotNull String filename) {
    //Given
    Path file = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename);

    //When
    Optional<ASTMACompilationUnit> ast = this.getMATool().parseMAModel(file);

    //Then
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#parseCDModel(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validCDModelFileNameProvider")
  public void shouldParseCDModelFromFile(@NotNull String filename) {
    //Given
    Path file = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename);

    //When
    Optional<ASTCDCompilationUnit> ast = this.getMATool().parseCDModel(file);

    //Then
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#parseMAModel(String)}.
   */
  @ParameterizedTest
  @MethodSource("validMAModelFileNameProvider")
  public void shouldParseMAModelFromFileName(@NotNull String filename) {
    //Given
    String respFileName = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename).toString();

    //When
    Optional<ASTMACompilationUnit> ast = this.getMATool().parseMAModel(respFileName);

    //Then
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#parseCDModel(String)}.
   */
  @ParameterizedTest
  @MethodSource("validCDModelFileNameProvider")
  public void shouldParseCDModelFromFileName(@NotNull String filename) {
    //When
    String respFileName = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename).toString();

    //When
    Optional<ASTCDCompilationUnit> ast = this.getMATool().parseCDModel(respFileName);

    //Then
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#parseMAModels(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validMAModelPathAndExpectedValuesProvider")
  public void shouldParseMAModelsFromDirectory(@NotNull String directoryName, int expNumModels) {
    //Given
    Path directory = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName);

    //When
    Collection<ASTMACompilationUnit> models = this.getMATool().parseMAModels(directory);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, models.size());
  }

  /**
   * Method under test {@link MontiArcTool#parseCDModels(Path)} (Path)}.
   */
  @ParameterizedTest
  @MethodSource("validCDModelPathAndExpectedValuesProvider")
  public void shouldParseCDModelsFromDirectory(@NotNull String directoryName, int expNumModels) {
    //Given
    Path directory = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName);

    //When
    Collection<ASTCDCompilationUnit> models = this.getMATool().parseCDModels(directory);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, models.size());
  }

  /**
   * Method under test {@link MontiArcTool#parseModels(IMontiArcGlobalScope)}.
   */
  @ParameterizedTest
  @MethodSource("validMAModelPathAndExpectedValuesProvider")
  public void shouldParseMAModelsFromScope(@NotNull String directoryName, int expNumModels) {
    //Given
    IMontiArcGlobalScope scope = MontiArcMill.montiArcGlobalScopeBuilder()
      .setModelPath(new ModelPath(Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName)))
      .setModelFileExtension(this.getMATool().getMAFileExtension()).build();

    //When
    Collection<ASTMACompilationUnit> models = this.getMATool().parseModels(scope);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, models.size());
  }


  /**
   * Method under test {@link MontiArcTool#parseModels(ICD4CodeGlobalScope)}.
   */
  @ParameterizedTest
  @MethodSource("validCDModelPathAndExpectedValuesProvider")
  public void shouldParseCDModelsFromScope(@NotNull String directoryName, int expNumModels) {
    //Given
    ICD4CodeGlobalScope scope = CD4CodeMill.cD4CodeGlobalScopeBuilder()
      .setModelPath(new ModelPath(Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName)))
      .setModelFileExtension(this.getMATool().getCDFileExtension()).build();

    //When
    Collection<ASTCDCompilationUnit> models = this.getMATool().parseModels(scope);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, models.size());
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @ParameterizedTest
  @MethodSource("validMAModelPathAndExpectedValuesProvider")
  public void shouldProcessValidModels(@NotNull String modelPathName, int expNumModels) {
    //Given
    MontiArcTool tool = new MontiArcTool();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    IMontiArcGlobalScope scope = tool.processModels(modelPath);

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
    tool.processModels(modelPath);

    //Then
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }
}