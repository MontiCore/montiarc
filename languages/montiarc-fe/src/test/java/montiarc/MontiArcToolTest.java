/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.IMontiArcScope;
import montiarc._symboltable.MontiArcArtifactScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

  protected MontiArcTool tool;

  protected MontiArcTool getTool() {
    return this.tool;
  }

  @BeforeEach
  @Override
  public void init() {
    super.init();
    this.setUpTool();
  }

  public void setUpTool() {
    this.tool = new MontiArcTool();
    this.getTool().initializeBasicTypes();
  }

  protected static Stream<Path> steamValidMontiArcModelFiles() {
    return Stream.of(Paths.get("valid", "example1", "Composed.arc"),
      Paths.get("valid", "example1", "InComp.arc"),
      Paths.get("valid", "example1", "OutComp.arc"));
  }

  protected static Stream<Arguments> validMontiArcModelFileNameProvider() {
    return steamValidMontiArcModelFiles().map(Path::toString).map(Arguments::of);
  }

  protected static Stream<Path> streamValidSerializedModelFileNames() {
    return Stream.of(Paths.get("valid", "example3", "Colors.sym"));
  }

  protected static Stream<Arguments> validSerializedModelFileNameProvider() {
    return streamValidSerializedModelFileNames().map(Path::toString).map(Arguments::of);
  }

  protected static Stream<Arguments> validMontiArcModelPathAndExpectedValuesProvider() {
    return Stream.of(Arguments.of(Paths.get("valid", "example1").toString(), 3),
      Arguments.of(Paths.get("valid", "example2").toString(), 3),
      Arguments.of(Paths.get("valid", "example3").toString(), 3));
  }

  protected static Stream<Arguments> validSerializedModelPathAndExpectedValuesProvider() {
    return Stream.of(Arguments.of(Paths.get("valid", "example1").toString(), 0),
      Arguments.of(Paths.get("valid", "example2").toString(), 0),
      Arguments.of(Paths.get("valid", "example3").toString(), 1));
  }

  protected static Stream<Arguments> invalidModelPathProvider() {
    return Stream.of(Arguments.of(Paths.get("invalid", "example1").toString()),
        Arguments.of(Paths.get("invalid", "example2").toString()));
  }

  /**
   * Method under test {@link MontiArcTool#parse(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validMontiArcModelFileNameProvider")
  public void shouldParseModelGivenPath(@NotNull String filename) {
    //Given
    Path file = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename);

    //When
    Optional<ASTMACompilationUnit> ast = this.getTool().parse(file);

    //Then
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#load(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validSerializedModelFileNameProvider")
  public void shouldLoadModelGivenPath(@NotNull String filename) {
    //Given
    Path file = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename);

    //When
    IMontiArcArtifactScope scope = this.getTool().load(file);

    //Then
    Assertions.assertNotNull(scope);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#parse(String)}.
   */
  @ParameterizedTest
  @MethodSource("validMontiArcModelFileNameProvider")
  public void shouldParseModelGivenFileName(@NotNull String filename) {
    //Given
    String respFileName = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename).toString();

    //When
    Optional<ASTMACompilationUnit> ast = this.getTool().parse(respFileName);

    //Then
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#load(String)}.
   */
  @ParameterizedTest
  @MethodSource("validSerializedModelFileNameProvider")
  public void shouldLoadModelGivenFileName(@NotNull String filename) {
    //When
    String respFileName = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, filename).toString();

    //When
    IMontiArcArtifactScope scope = this.getTool().load(respFileName);

    //Then
    Assertions.assertNotNull(scope);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#parseAll(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validMontiArcModelPathAndExpectedValuesProvider")
  public void shouldParseModelsGivenDirectory(@NotNull String directoryName, int expNumModels) {
    //Given
    Path directory = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName);

    //When
    Collection<ASTMACompilationUnit> models = this.getTool().parseAll(directory);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, models.size());
  }

  /**
   * Method under test {@link MontiArcTool#loadAll(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validSerializedModelPathAndExpectedValuesProvider")
  public void shouldLoadModelsGivenDirectory(@NotNull String directoryName, int expNumModels) {
    //Given
    Path directory = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName);

    //When
    Collection<IMontiArcArtifactScope> scopes = this.getTool().loadAll(directory);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, scopes.size());
  }

  /**
   * Method under test {@link MontiArcTool#parseAll(IMontiArcGlobalScope)}.
   */
  @ParameterizedTest
  @MethodSource("validMontiArcModelPathAndExpectedValuesProvider")
  public void shouldParseModelsGivenScope(@NotNull String directoryName, int expNumModels) {
    //Given
    IMontiArcGlobalScope globalScope = MontiArcMill.globalScope();
    globalScope.setModelPath(new ModelPath(Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName)));
    globalScope.setFileExt(this.getTool().getMAFileExtension());

    //When
    Collection<ASTMACompilationUnit> models = this.getTool().parseAll(globalScope);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, models.size());
  }


  /**
   * Method under test {@link MontiArcTool#loadAll(IMontiArcGlobalScope)}.
   */
  @ParameterizedTest
  @MethodSource("validSerializedModelPathAndExpectedValuesProvider")
  public void shouldLoadModelsGivenScope(@NotNull String directoryName, int expNumModels) {
    //Given
    IMontiArcGlobalScope globalScope = MontiArcMill.globalScope();
    globalScope.setModelPath(new ModelPath(Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, directoryName)));

    //When
    Collection<IMontiArcArtifactScope> scopes = this.getTool().loadAll(globalScope);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, scopes.size());
  }

  /**
   * Method under test {@link MontiArcTool#createSymbolTable(Path)}.
   */
  @ParameterizedTest
  @MethodSource("validMontiArcModelPathAndExpectedValuesProvider")
  public void shouldCreateSymbolTable(@NotNull String modelPathName, int expNumModels) {
    //Given
    MontiArcTool tool = new MontiArcTool();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    Collection<IMontiArcScope> scopes = tool.createSymbolTable(modelPath);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, scopes.stream().flatMap(scope -> scope.getSubScopes().stream()).filter(IScope::isPresentAstNode).count());
    Assertions.assertTrue(scopes.stream().allMatch(scope -> scope instanceof MontiArcArtifactScope));
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @ParameterizedTest
  @MethodSource("validMontiArcModelPathAndExpectedValuesProvider")
  public void shouldProcessValidModels(@NotNull String modelPathName, int expNumModels) {
    //Given
    MontiArcTool tool = new MontiArcTool();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    IMontiArcGlobalScope scope = tool.processModels(modelPath);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, scope.getSubScopes().stream().filter(IScope::isPresentAstNode).count());
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
