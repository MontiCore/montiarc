/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc.util.ArcError;
import montiarc.util.Error;
import montiarc.util.MCError;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds tests for {@link MontiArcTool}.
 */
public class MontiArcToolTest extends MontiArcAbstractTest {

  protected final static String TEST_DIR = "clitool";

  @TempDir
  Path tempDir;

  @BeforeEach
  @Override
  public void setUp() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();
    LogStub.init();
  }

  /**
   * Method under test {@link MontiArcTool#run(String[])}
   */
  @Test
  public void runShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.run(null));
  }

  /**
   * Method under test {@link MontiArcTool#run(String[])}
   */
  @Test
  public void runInvalidOptionShouldThrowToolParseIOException() {
    // Given
    String[] args = new String[]{"-notavalidoption"};
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    this.checkExpectedErrorsPresent(new Error[]{MontiArcError.TOOL_PARSE_IOEXCEPTION});
  }

  /**
   * Method under test {@link MontiArcTool#initGlobalScope(CommandLine)}
   */
  @Test
  public void initGlobalScopeCommandLineShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.initGlobalScope((CommandLine) null));
  }

  /**
   * Method under test {@link MontiArcTool#createModelPath(CommandLine)}
   */
  @Test
  public void createModelPathShouldHandleVariousFormats() throws Exception {
    // Given
    Path p1 = Path.of("some", "where");
    Path p2 = Path.of("foo", "bar");
    Path p3 = Path.of("another", "model", "path");
    String[] stringArgs = new String[]{"--modelpath", p1 + File.pathSeparator + p2, p3.toString()};

    MontiArcTool tool = new MontiArcTool();
    Options options = new Options();
    tool.addStandardOptions(options);
    CommandLineParser parser = new DefaultParser();
    CommandLine args = parser.parse(options, stringArgs);

    // When
    MCPath modelPath = tool.createModelPath(args);

    // Then
    List<Path> expectedPaths = List.of(p1.toAbsolutePath(), p2.toAbsolutePath(), p3.toAbsolutePath());
    List<Path> returnedPaths = modelPath.getEntries().stream()
      .map(Path::toAbsolutePath)
      .collect(Collectors.toList());
    Assertions.assertEquals(expectedPaths, returnedPaths);
    this.checkOnlyExpectedErrorsPresent(/* none */);
  }

  /**
   * Method under test {@link MontiArcTool#initGlobalScope(CommandLine)}
   */
  @Test
  public void initGlobalScopeShouldSetSymbolPath() throws ParseException {
    // Given
    String path = "test/resources/CLI/industryModels/industry";
    String[] args = new String[]{"-path", path};
    CommandLineParser cliParser = new DefaultParser();
    MontiArcTool tool = new MontiArcTool();
    tool.initOptions();
    Options options = tool.initOptions();
    CommandLine cli = cliParser.parse(options, args);

    // When
    tool.initGlobalScope(cli);

    // Then
    Assertions.assertEquals(1, MontiArcMill.globalScope().getSymbolPath().getEntries().size());
    Assertions.assertTrue(MontiArcMill.globalScope().getSymbolPath().getEntries()
      .contains(Paths.get(path).toAbsolutePath()));
  }

  /**
   * Method under test {@link MontiArcTool#initGlobalScope(Path...)}
   */
  @Test
  public void initGlobalScopePathsShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.initGlobalScope((Path[]) null));
  }

  /**
   * Method under test {@link MontiArcTool#initGlobalScope(String...)}
   */
  @Test
  public void initGlobalScopeStringsShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.initGlobalScope((String[]) null));
  }

  /**
   * Method under test {@link MontiArcTool#initGlobalScope(Collection)}
   */
  @Test
  public void initGlobalScopeCollectionShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.initGlobalScope((Collection<Path>) null));
  }

  /**
   * Method under test {@link MontiArcTool#initGlobalScope(Collection)}
   */
  @Test
  public void initGlobalScopeCollectionShouldThrowIllegalArgumentsException() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Collection<Path> entries = new ArrayList<>();
    entries.add(null);

    // When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> tool.initGlobalScope(entries));
  }

  /**
   * Method under test {@link MontiArcTool#runTasks(CommandLine)}
   */
  @ParameterizedTest
  @MethodSource("runTasksExceptionProvider")
  public void runTasksShouldThrowException(@Nullable CommandLine cli,
                                           @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.runTasks(cli));
  }

  protected static Stream<Arguments> runTasksExceptionProvider() {
    return Stream.of(
      Arguments.of(null, NullPointerException.class)
    );
  }

  /**
   * Method under test {@link MontiArcTool#createModelPath(CommandLine)}
   */
  @Test
  public void createModelPathShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.createModelPath(null));
  }

  /**
   * Method under test {@link MontiArcTool#createModelPath(CommandLine)}
   */
  @Test
  public void createModelPathWithoutModelPathOption() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String[] args = new String[]{};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When && Then
    Assertions.assertTrue(tool.createModelPath(cli).getEntries().isEmpty());
  }

  /**
   * Method under test {@link MontiArcTool#createModelPath(CommandLine)}
   */
  @Test
  public void shouldCreateModelPath() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String subTestDIr = "industryModels/industry";
    String modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, subTestDIr).toAbsolutePath().toString();
    String[] args = new String[]{"-mp", modelPath};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When && Then
    Assertions.assertTrue(tool.createModelPath(cli).getEntries().stream()
      .map(Path::toString).anyMatch(x -> x.equals(Paths.get(modelPath).toAbsolutePath().toString())));
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Collection)}
   */
  @ParameterizedTest
  @MethodSource("parseDirectoriesExpectedExceptionProvider")
  public void parseDirectoriesShouldGiveWarnings(@Nullable String fileExt,
                                                 @Nullable Collection<Path> directories,
                                                 @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();
    // When && Then
    Assertions.assertThrows(expected, () -> tool.parse(fileExt, directories));
  }

  protected static Stream<Arguments> parseDirectoriesExpectedExceptionProvider() throws ParseException {
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String subTestDir = "validFileStructureMock/validPackageMock";
    String modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, subTestDir).toString();
    String[] args = new String[]{"-mp", modelPath};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);
    return Stream.of(
      Arguments.of(null, tool.createModelPath(cli).getEntries(), NullPointerException.class),
      Arguments.of("", tool.createModelPath(cli).getEntries(), IllegalArgumentException.class),
      Arguments.of(".arc", null, NullPointerException.class)
    );
  }

  @ParameterizedTest
  @MethodSource("nonExistentDirectories")
  public void parseNonExistentDirectoryShouldGiveWarning(@Nullable String fileExt,
                                                         @Nullable Collection<Path> directories) {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.parse(fileExt, directories);

    // Then
    Assertions.assertEquals(1, Log.getFindingsCount());
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  protected static Stream<Arguments> nonExistentDirectories() throws ParseException {
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String subTestDir = "non/existent";
    String modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, subTestDir).toString();
    String[] args = new String[]{"-mp", modelPath};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);
    return Stream.of(
      Arguments.of(".arc", tool.createModelPath(cli).getEntries())
    );
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Collection)}
   */
  @Test
  public void shouldParseDirectories() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String subTestDir = "validFileStructureMock";
    String modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, subTestDir).toString();
    String[] args = new String[]{"-mp", modelPath};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);
    tool.parse(".arc", tool.createModelPath(cli).getEntries());

    // When
    Collection<ASTMACompilationUnit> asts = tool.parse(".arc", tool.createModelPath(cli).getEntries());

    //Then
    Assertions.assertTrue(asts.stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("validFileStructureMock.validPackageMock")));
    Assertions.assertTrue(asts.stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("validFileStructureMock.validPackageMock2")));
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Path)}
   */
  @ParameterizedTest
  @MethodSource("parseDirectoryExpectedExceptionProvider")
  public void parseDirectoryShouldGiveWarning(@Nullable String fileExt,
                                              @Nullable Path directory,
                                              @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    //tool.parse(fileExt, directory);
    Assertions.assertThrows(expected, () -> tool.parse(fileExt, directory));
    //Assertions.assertEquals(Log.getErrorCount(),0);
  }

  @Test
  public void parseDirectoryShouldGiveWarnings() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    tool.parse(".arc", Paths.get(""));
    Assertions.assertEquals(Log.getErrorCount(), 0);

  }

  protected static Stream<Arguments> parseDirectoryExpectedExceptionProvider() {
    String subPackageDir = "validFileStructureMock/validPackageMock";
    Path mockModelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, subPackageDir);

    return Stream.of(
      Arguments.of(null, mockModelPath.toAbsolutePath(), NullPointerException.class),
      Arguments.of("", mockModelPath, IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Path)}
   */
  @Test
  public void shouldParseDirectory() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String subTestDir = "validFileStructureMock/validPackageMock";
    String modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, subTestDir).toAbsolutePath().toString();

    // When && Then
    Assertions.assertTrue(tool.parse(".arc", Paths.get(modelPath).toAbsolutePath()).stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("validFileStructureMock.validPackageMock")));
  }

  /**
   * Method under test {@link MontiArcTool#parse(Path)}
   */
  @ParameterizedTest
  @MethodSource("parseFileExpectedExceptionProvider")
  public void parseFileShouldThrowException(@Nullable Path file,
                                            @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.parse(file));
  }

  protected static Stream<Arguments> parseFileExpectedExceptionProvider() {
    return Stream.of(
      Arguments.of(null, NullPointerException.class),
      Arguments.of(Paths.get("invalid/model/path").toAbsolutePath(),
        IllegalArgumentException.class),
      // It is illegal to pass a directory, a file is expected:
      Arguments.of(Paths.get(RELATIVE_MODEL_PATH + TEST_DIR).toAbsolutePath(),
        IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link MontiArcTool#parse(Path)}
   */
  @Test
  public void shouldParseFile() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String qualifiedModelName = "validFileStructureMock/validPackageMock/ValidMockComponent.arc";
    Path modelLocation = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, qualifiedModelName);

    // When
    Optional<ASTMACompilationUnit> ast2 = tool.parse(modelLocation.toAbsolutePath());

    // Then
    Assertions.assertTrue(ast2.isPresent());
    Assertions.assertEquals("ValidMockComponent", ast2.get().getComponentType().getName());
  }

  /**
   * Method under test {@link MontiArcTool#createSymbolTable(Collection)}
   */
  @Test
  public void createSymbolTableCollectionShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.createSymbolTable((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#createSymbolTable(ASTMACompilationUnit)}
   */
  @Test
  public void createSymbolTableShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.createSymbolTable((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#createSymbolTable(Collection)}
   */
  @Test
  public void shouldCreateSymbolTableCollection() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String subTestDir = "validFileStructureMock/";
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, subTestDir);
    Collection<ASTMACompilationUnit> asts = tool.parse(".arc", modelPath);
    Preconditions.checkState(!asts.isEmpty());

    // When
    Collection<IMontiArcArtifactScope> scopes = tool.createSymbolTable(asts);

    // Then
    Assertions.assertEquals(asts.size(), scopes.size());
    for (ASTMACompilationUnit ast : asts) {
      Assertions.assertNotNull(ast.getEnclosingScope());
      Assertions.assertTrue(scopes.contains((IMontiArcArtifactScope) ast.getEnclosingScope()));
    }
  }

  /**
   * Method under test {@link MontiArcTool#createSymbolTable(ASTMACompilationUnit)}
   */
  @Test
  public void shouldCreateSymbolTable() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String qualifiedModelName = "validFileStructureMock/validPackageMock/ValidMockComponent.arc";
    Path modelLocation = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, qualifiedModelName);
    Optional<ASTMACompilationUnit> ast = tool.parse(modelLocation);
    Preconditions.checkState(ast.isPresent());

    // When
    IMontiArcArtifactScope scope = tool.createSymbolTable(ast.get());

    // Then
    Assertions.assertEquals(scope, ast.get().getEnclosingScope());
  }

  /**
   * Method under test {@link MontiArcTool#runSymbolTablePhase2(Collection)}
   */
  @Test
  void runSymbolTablePhase2CollectionShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runSymbolTablePhase2((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#runSymbolTablePhase2(ASTMACompilationUnit)}
   */
  @Test
  void runSymbolTablePhase2ShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.runSymbolTablePhase2((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#runSymbolTablePhase3(Collection)}
   */
  @Test
  void runSymbolTablePhase3CollectionShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runSymbolTablePhase3((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#runSymbolTablePhase3(ASTMACompilationUnit)}
   */
  @Test
  void runSymbolTablePhase3ShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.runSymbolTablePhase3((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#runAfterParsingTrafos(Collection)}
   */
  @Test
  void runAfterParsingTrafosCollectionShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAfterParsingTrafos((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#runAfterParsingTrafos(ASTMACompilationUnit)}
   */
  @Test
  void runAfterParsingTrafosShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAfterParsingTrafos((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#runAfterSymbolTablePhase3Trafos(Collection)}
   */
  @Test
  void runAfterSymbolTablePhase3TrafosCollectionShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAfterSymbolTablePhase3Trafos((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#runAfterSymbolTablePhase3Trafos(ASTMACompilationUnit)}
   */
  @Test
  void runAfterSymbolTablePhase3TrafosShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAfterSymbolTablePhase3Trafos((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#runSymbolTablePhase2(ASTMACompilationUnit)}
   */
  @Test
  public void shouldRunSymbolTablePhase2() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String pakkage = "symboltable/pass2";
    Path packagePath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, pakkage);

    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      packagePath.resolve("A.arc").toAbsolutePath().toString()).orElseThrow(IllegalArgumentException::new);
    ASTMACompilationUnit astB = MontiArcMill.parser().parse(
      packagePath.resolve("B.arc").toAbsolutePath().toString()).orElseThrow(IllegalArgumentException::new);
    tool.createSymbolTable(astA);
    tool.createSymbolTable(astB);

    // When
    tool.runSymbolTablePhase2(astB);

    // Then
    Assertions.assertTrue(astB.getEnclosingScope().getSubScopes().get(0)
      .resolveSubcomponentLocally("a").isPresent());
    Assertions.assertNotNull(astB.getEnclosingScope().getSubScopes().get(0)
      .resolveSubcomponentLocally("a").get().getType());
  }

  /**
   * Method under test {@link MontiArcTool#runSymbolTablePhase3(ASTMACompilationUnit)}
   */
  @Test
  void shouldRunSymbolTablePhase3() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String pakkage = "symboltable/pass3";
    Path packagePath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, pakkage);

    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      packagePath.resolve("A.arc").toAbsolutePath().toString()).orElseThrow();
    ASTMACompilationUnit astB = MontiArcMill.parser().parse(
      packagePath.resolve("B.arc").toAbsolutePath().toString()).orElseThrow();
    tool.createSymbolTable(astA);
    tool.createSymbolTable(astB);
    tool.runSymbolTablePhase2(astA);
    tool.runSymbolTablePhase2(astB);

    // When
    tool.runSymbolTablePhase3(astB);

    // Then
    ComponentTypeSymbol aCompType = astA.getComponentType().getSymbol();
    ComponentTypeSymbol bCompType = astB.getComponentType().getSymbol();
    ArcPortSymbol aInPort = aCompType.getArcPort("inPortA").orElseThrow();
    ArcPortSymbol bInPort = bCompType.getArcPort("inPortB").orElseThrow();
    SubcomponentSymbol aInstance = bCompType.getSubcomponents("a").orElseThrow();

    ASTConnector connector = astB.getComponentType().getConnectors().get(0);
    ASTPortAccess bAccess = connector.getSource();
    ASTPortAccess aAccess = connector.getTarget(0);

    Assertions.assertAll(
      () -> Assertions.assertTrue(bAccess.isPresentPortSymbol(), "Port of b should be present."),
      () -> Assertions.assertTrue(aAccess.isPresentPortSymbol(), "Port of a should be present."),
      () -> Assertions.assertTrue(aAccess.isPresentComponentSymbol(), "Comp of a should be present."),
      () -> Assertions.assertFalse(bAccess.isPresentComponentSymbol(), "Comp of b should not be present.")
    );

    Assertions.assertAll(
      () -> Assertions.assertSame(bInPort, bAccess.getPortSymbol(), "B Port mismatch"),
      () -> Assertions.assertSame(aInPort, aAccess.getPortSymbol(), "A Port mismatch"),
      () -> Assertions.assertSame(aInstance, aAccess.getComponentSymbol(), "B component mismatch")
    );
  }

  @Test
  void shouldRunAfterParsingTransformations() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    tool.initializeBasicTypes();
    String pakkage = "transformations/afterParsingTrafos";
    Path packagePath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, pakkage);

    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      packagePath.resolve("A.arc").toAbsolutePath().toString()).orElseThrow();

    int connectorCountBeforeTrafo = astA.getComponentType().getConnectors().size();

    // When
    tool.runAfterParsingTrafos(astA);

    // Then
    int connectorCountAfterTrafo = astA.getComponentType().getConnectors().size();

    Assertions.assertTrue(connectorCountAfterTrafo > connectorCountBeforeTrafo, "Expected new connectors");
  }


  /**
   * Method under test {@link MontiArcTool#runAfterSymbolTablePhase3Trafos(ASTMACompilationUnit)}
   */
  @Test
  void shouldRunAfterSymbolLinkingTransformations() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    tool.initializeBasicTypes();
    String pakkage = "transformations/afterSymbolLinkingTrafos";
    Path packagePath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, pakkage);

    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      packagePath.resolve("A.arc").toAbsolutePath().toString()).orElseThrow();
    ASTMACompilationUnit astB = MontiArcMill.parser().parse(
      packagePath.resolve("B.arc").toAbsolutePath().toString()).orElseThrow();
    tool.createSymbolTable(astA);
    tool.createSymbolTable(astB);
    tool.runSymbolTablePhase2(astA);
    tool.runSymbolTablePhase2(astB);
    tool.runSymbolTablePhase3(astA);
    tool.runSymbolTablePhase3(astB);

    int connectorCountBeforeTrafo = astB.getComponentType().getConnectors().size();

    // When
    tool.runAfterSymbolTablePhase3Trafos(astB);

    // Then
    long connectorCountAfterTrafo = astB.getComponentType().getConnectors().size();

    Assertions.assertTrue(connectorCountAfterTrafo > connectorCountBeforeTrafo, "Expected new connectors");
  }

  /**
   * Method under test {@link MontiArcTool#runTasks(Collection, CommandLine)}
   */
  @Test
  public void runTasksShouldThrowException() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String pakkage = "validFileStructureMock";
    Path packagePath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, pakkage);
    Collection<ASTMACompilationUnit> asts =
      tool.parse(".arc", packagePath.toAbsolutePath());
    String[] args = new String[]{"-pp", RELATIVE_MODEL_PATH + "/" + "file.arc", "-s", RELATIVE_MODEL_PATH};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runTasks(null, cli));
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runTasks(asts, null));
  }

  /**
   * Method under test {@link MontiArcTool#runTasks(Collection, CommandLine)}
   */
  @Test
  public void runTasksShouldPrettyPrintFile() throws ParseException {
    // Given
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, "validFileStructureMock");
    String ppTargetDir = tempDir.toAbsolutePath().toString();
    Path expectedPpFile1 = Paths.get(ppTargetDir, Names.getPathFromQualifiedName("validFileStructureMock.validPackageMock"), "ValidMockComponent.arc");
    Path expectedPpFile2 = Paths.get(ppTargetDir, Names.getPathFromQualifiedName("validFileStructureMock.validPackageMock2"), "ValidMockComponent2.arc");

    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    Collection<ASTMACompilationUnit> innerComponents = tool.parse(".arc", modelPath.toAbsolutePath());
    String[] args = new String[]{"-pp", ppTargetDir};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When && Then
    Assertions.assertDoesNotThrow(() -> tool.runTasks(innerComponents, cli));
    Assertions.assertTrue(expectedPpFile1.toFile().isFile());
    Assertions.assertTrue(expectedPpFile2.toFile().isFile());
  }

  /**
   * Method under test {@link MontiArcTool#runTasks(Collection, CommandLine)}
   */
  @Test
  public void runTasksShouldOutputSymbolTable() throws ParseException {
    // Given
    String parsePath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, "storeSymbols").toAbsolutePath().toString();
    File serializeFile = tempDir.resolve("WithInnerComponents.arcsym").toFile();

    MontiArcTool tool = new MontiArcTool();
    Collection<ASTMACompilationUnit> innerComponents = tool.parse(".arc", Paths.get(parsePath).toAbsolutePath());
    String[] args = new String[]{"-s", tempDir.toAbsolutePath().toString()};
    Options options = tool.initOptions();
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When && Then
    Assertions.assertDoesNotThrow(() -> tool.runTasks(innerComponents, cli));
    Assertions.assertTrue(serializeFile.exists());
  }

  /**
   * Method under test {@link MontiArcTool#storeSymbols(IMontiArcArtifactScope, String)}
   */
  @Disabled("The mill is only initialized after creating the symbol table " +
    "resulting in errors if the test is run in isolation.")
  @ParameterizedTest
  @MethodSource("storeSymbolsScopeExpectedExceptionProvider")
  public void storeSymbolsScopeShouldThrowException(@Nullable IMontiArcArtifactScope scope,
                                                    @Nullable String path,
                                                    @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.storeSymbols(scope, path));
  }

  protected static Stream<Arguments> storeSymbolsScopeExpectedExceptionProvider() {
    MontiArcTool tool = new MontiArcTool();
    Path baseTestDir = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR);
    Optional<ASTMACompilationUnit> optAst = tool.parse(
      baseTestDir.resolve("storeSymbols").resolve("WithInnerComponents.arc").toAbsolutePath()
    );
    Assertions.assertTrue(optAst.isPresent());
    ASTMACompilationUnit ast = optAst.get();
    tool.createSymbolTable(ast);
    tool.runSymbolTablePhase2(ast);
    tool.runSymbolTablePhase3(ast);
    tool.runAfterSymbolTablePhase3Trafos(ast);
    IMontiArcArtifactScope scope = (MontiArcArtifactScope) ast.getEnclosingScope();
    return Stream.of(
      Arguments.of(null, System.getProperty("buildDir") + "/test-sources/resources/CLI/symboltable", NullPointerException.class),
      Arguments.of(scope, null, NullPointerException.class),
      Arguments.of(scope, "", IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link MontiArcTool#runDefaultCoCos(Collection)}
   */
  @Test
  public void runDefaultCoCosCollectionShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runDefaultCoCos((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#runDefaultCoCos(ASTMACompilationUnit)}
   */
  @Test
  public void runDefaultCoCosShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runDefaultCoCos((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#runAdditionalCoCos(ASTMACompilationUnit)}
   */
  @Test
  public void runAdditionalCoCosCollectionShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAdditionalCoCos((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#runAdditionalCoCos(ASTMACompilationUnit)}
   */
  @Test
  public void runAdditionalCoCosShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAdditionalCoCos((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#initializeClass2MC(CommandLine)}
   */
  @Test
  public void initializeClass2MCShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.initializeClass2MC(null));
  }

  /**
   * Method under test {@link MontiArcTool#initializeClass2MC(CommandLine)}
   */
  @Test
  public void shouldInitializeClass2MC() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    Assertions.assertFalse(MontiArcMill.globalScope().getAdaptedTypeSymbolResolverList().
      stream().anyMatch(symbolResolver -> symbolResolver instanceof OOClass2MCResolver));
    Assertions.assertFalse(MontiArcMill.globalScope().getAdaptedOOTypeSymbolResolverList().
      stream().anyMatch(symbolResolver -> symbolResolver instanceof OOClass2MCResolver));
    String[] args = new String[]{"-c2mc"};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When
    tool.initializeClass2MC(cli);

    // Then
    Assertions.assertTrue(MontiArcMill.globalScope().getAdaptedTypeSymbolResolverList().
      stream().anyMatch(symbolResolver -> symbolResolver instanceof OOClass2MCResolver));
    Assertions.assertTrue(MontiArcMill.globalScope().getAdaptedOOTypeSymbolResolverList().
      stream().anyMatch(symbolResolver -> symbolResolver instanceof OOClass2MCResolver));
  }

  @Test
  void shouldAddJavaLangImport() throws IOException {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit("component A {}").orElseThrow();

    MontiArcTool tool = new MontiArcTool();

    // When
    tool.defaultImportTrafo(ast);
    tool.createSymbolTable(ast);

    // Then
    List<ImportStatement> imports = ((IMontiArcArtifactScope) ast.getEnclosingScope()).getImportsList();
    Assertions.assertTrue(imports.stream()
      .anyMatch(i -> i.getStatement().equals("java.lang") && i.isStar()), "Import to java.lang.* should be present.");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "compTypesFromSymFiles", "connectedSubComponents", "importFromSubPackage", "ooTypesFromSymFiles", "variableCompFromSymFiles"
  })
  void validModelsShouldPassEndToEnd(@NotNull String packageName) {
    Preconditions.checkNotNull(packageName);

    // Given
    String path = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, "endToEnd").toString();
    String modelPath = Paths.get(path, packageName).toString();
    MontiArcTool tool = new MontiArcTool();
    String[] args = new String[]{"--modelpath", modelPath, "-path", path};

    // When
    tool.run(args);

    // Then
    this.checkOnlyExpectedErrorsPresent();
    Assertions.assertFalse(
      MontiArcMill.globalScope().getSubScopes().isEmpty(),
      "It seams that no model was processed in this test (GlobalScope has no sub scopes)."
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModelAndErrorProvider")
  void invalidModelsShouldFailEndToEnd(@NotNull String model, @NotNull Error[] errors) {
    Preconditions.checkNotNull(model);

    // Given
    String modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_DIR, "cocos", model).toString();
    String[] args = new String[]{"--modelpath", modelPath};
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }

  protected static Stream<Arguments> invalidModelAndErrorProvider() {
    return Stream.of(
      Arguments.of("CircularInheritance.arc", new Error[]{ArcError.CIRCULAR_INHERITANCE}),
      Arguments.of("MissingCompType1.arc", new Error[]{ArcError.MISSING_COMPONENT}),
      Arguments.of("MissingCompType2.arc", new Error[]{ArcError.MISSING_COMPONENT}),
      Arguments.of("MissingCompType3.arc", new Error[]{ArcError.MISSING_COMPONENT, ArcError.MISSING_COMPONENT}),
      Arguments.of("MissingCompType4.arc", new Error[]{ArcError.MISSING_COMPONENT}),
      Arguments.of("MissingPortType1.arc", new Error[]{MCError.CANT_FIND_SYMBOL}),
      Arguments.of("MissingPortType2.arc", new Error[]{MCError.CANT_FIND_SYMBOL}),
      Arguments.of("MissingPortType3.arc", new Error[]{MCError.CANT_FIND_SYMBOL, MCError.CANT_FIND_SYMBOL}),
      Arguments.of("MissingPortType4.arc", new Error[]{MCError.CANT_FIND_SYMBOL, MCError.CANT_FIND_SYMBOL}),
      Arguments.of("MissingPortType5.arc", new Error[]{MCError.CANT_FIND_SYMBOL, ArcError.CONNECTOR_TYPE_MISMATCH}),
      Arguments.of("MissingPortType6.arc", new Error[]{MCError.CANT_FIND_SYMBOL, ArcError.CONNECTOR_TYPE_MISMATCH}),
      Arguments.of("MissingPortType7.arc", new Error[]{MCError.CANT_FIND_SYMBOL, ArcError.CONNECTOR_TYPE_MISMATCH}),
      Arguments.of("MissingPortType8.arc", new Error[]{MCError.CANT_FIND_SYMBOL, ArcError.CONNECTOR_TYPE_MISMATCH}),
      Arguments.of("MissingPortType9.arc", new Error[]{MCError.CANT_FIND_SYMBOL, ArcError.CONNECTOR_TYPE_MISMATCH}),
      Arguments.of("MissingPortType10.arc", new Error[]{MCError.CANT_FIND_SYMBOL, ArcError.CONNECTOR_TYPE_MISMATCH}),
      //Arguments.of("MissingPortType11.arc", new Error[]{MCError.CANT_FIND_SYMBOL, MCError.CANT_FIND_SYMBOL}),
      Arguments.of("MissingPortType12.arc", new Error[]{MCError.CANT_FIND_SYMBOL, MCError.CANT_FIND_SYMBOL, ArcError.CONNECTOR_TYPE_MISMATCH}),
      //Arguments.of("MissingPortType13.arc", new Error[]{MCError.CANT_FIND_SYMBOL, SCError.PRECONDITION_NOT_BOOLEAN}),
      //Arguments.of("MissingPortType14.arc", new Error[]{MCError.CANT_FIND_SYMBOL, MCError.INCOMPATIBLE_TYPE}),
      //Arguments.of("MissingPortType15.arc", new Error[]{MCError.CANT_FIND_SYMBOL, MCError.INCOMPATIBLE_TYPE}),
      //Arguments.of("MissingPortType16.arc", new Error[]{MCError.CANT_FIND_SYMBOL}),
      //Arguments.of("NameClashParamParam.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      //Arguments.of("NameClashParamPort.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      //Arguments.of("NameClashParamVar.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      Arguments.of("NameClashPortPort1.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      Arguments.of("NameClashPortPort2.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      Arguments.of("NameClashPortVar.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      //Arguments.of("NameClashTypeParam.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      Arguments.of("NameClashVarPort.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES}),
      Arguments.of("NameClashVarVar.arc", new Error[]{ArcError.UNIQUE_IDENTIFIER_NAMES})
    );
  }
}