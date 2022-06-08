/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.Class2MCResolver;
import de.se_rwth.commons.logging.LogStub;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc.util.Error;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.*;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Holds tests for {@link MontiArcTool}.
 */
public class MontiArcToolTest extends AbstractTest {

  protected final static String TEST_DIR = "clitool";

  @BeforeEach
  @Override
  public void init() {
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

  protected static Stream<Arguments> runTasksExceptionProvider() throws ParseException {
    MontiArcTool mArcCLI = new MontiArcTool();
    Options options = mArcCLI.initOptions();
    String[] args = new String[]{"-mp", "invalid/model/path"};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);
    return Stream.of(
      Arguments.of(null, NullPointerException.class),
      Arguments.of(cli, IllegalArgumentException.class)
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
    String modelPath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, subTestDIr);
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
  public void parseDirectoriesShouldThrowException(@Nullable String fileExt,
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
    String subTestDIr = "industryModels/industry";
    String modelPath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, subTestDIr);
    String[] args = new String[]{"-mp", modelPath};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);
    return Stream.of(
      Arguments.of(null, tool.createModelPath(cli).getEntries(), NullPointerException.class),
      Arguments.of("", tool.createModelPath(cli).getEntries(), IllegalArgumentException.class),
      Arguments.of(".arc", null, NullPointerException.class)
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
    String subTestDir = "industryModels";
    String modelPath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, subTestDir);
    String[] args = new String[]{"-mp", modelPath};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);
    tool.parse(".arc", tool.createModelPath(cli).getEntries());

    // When
    Collection<ASTMACompilationUnit> asts = tool.parse(".arc", tool.createModelPath(cli).getEntries());

    //Then
    Assertions.assertTrue(asts.stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("industryModels.industry")));
    Assertions.assertTrue(asts.stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("industryModels.industry2")));
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Path)}
   */
  @ParameterizedTest
  @MethodSource("parseDirectoryExpectedExceptionProvider")
  public void parseDirectoryShouldThrowException(@Nullable String fileExt,
                                                 @Nullable Path directory,
                                                 @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.parse(fileExt, directory));
  }

  protected static Stream<Arguments> parseDirectoryExpectedExceptionProvider() {

    String industrySubDir = "industryModels/industry";
    String industryModelPath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, industrySubDir);
    String piControllerPath = String.join("/", industryModelPath, "PIController.arc");

    return Stream.of(
      Arguments.of(null, Paths.get(industryModelPath).toAbsolutePath(), NullPointerException.class),
      Arguments.of("", Paths.get(industryModelPath).toAbsolutePath(), IllegalArgumentException.class),
      Arguments.of(".arc", null, NullPointerException.class),
      Arguments.of(".arc", Paths.get("invalid/model/path").toAbsolutePath(),
        IllegalArgumentException.class),
      Arguments.of(".arc", Paths.get(piControllerPath).toAbsolutePath(),
        IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Path)}
   */
  @Test
  public void shouldParseDirectory() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String subTestDir = "industryModels/industry";
    String modelPath = String.join("/", RELATIVE_MODEL_PATH + "/", TEST_DIR, subTestDir + "/");

    // When && Then
    Assertions.assertTrue(tool.parse(".arc", Paths.get(modelPath).toAbsolutePath()).stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("industryModels.industry")));
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
      Arguments.of(Paths.get(RELATIVE_MODEL_PATH + "/" + TEST_DIR).toAbsolutePath(),
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
    String qualifiedModelName = "industryModels/industry/PIController.arc";
    String modelLocation = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, qualifiedModelName);

    // When
    Optional<ASTMACompilationUnit> ast2 = tool.parse(Paths.get(modelLocation).toAbsolutePath());

    // Then
    Assertions.assertTrue(ast2.isPresent());
    Assertions.assertEquals("PIController", ast2.get().getComponentType().getName());
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
    String subTestDir = "industryModels/";
    String modelPath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, subTestDir);
    Collection<ASTMACompilationUnit> asts = tool.parse(".arc", Paths.get(modelPath));
    Preconditions.checkState(!asts.isEmpty());

    // When
    Collection<IMontiArcArtifactScope> scopes = tool.createSymbolTable(asts);

    // Then
    Assertions.assertEquals(asts.size(), scopes.size());
    for (ASTMACompilationUnit ast: asts) {
      Assertions.assertNotNull(ast.getSpannedScope());
      Assertions.assertTrue(scopes.contains((IMontiArcArtifactScope) ast.getSpannedScope()));
    }
  }

  /**
   * Method under test {@link MontiArcTool#createSymbolTable(ASTMACompilationUnit)}
   */
  @Test
  public void shouldCreateSymbolTable() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String qualifiedModelName = "industryModels/industry/PIController.arc";
    String modelLocation = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, qualifiedModelName);
    Optional<ASTMACompilationUnit> ast = tool.parse(Paths.get(modelLocation));
    Preconditions.checkState(ast.isPresent());
    
    // When
    IMontiArcArtifactScope scope = tool.createSymbolTable(ast.get());
    
    // Then
    Assertions.assertEquals(scope, ast.get().getSpannedScope());
  }

  /**
   * Method under test {@link MontiArcTool#completeSymbolTable(Collection)}
   */
  @Test
  public void completeSymbolTableCollectionShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, 
      () -> tool.completeSymbolTable((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#completeSymbolTable(ASTMACompilationUnit)}
   */
  @Test
  public void completeSymbolTableShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.completeSymbolTable((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#completeSymbolTable(ASTMACompilationUnit)}
   */
  @Test
  public void shouldCompleteSymbolTable() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String pakkage = "symboltable/completer";
    String packagePath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, pakkage);
    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      Paths.get(packagePath + "/" + "A.arc").toAbsolutePath().toString()).orElseThrow(IllegalArgumentException::new);
    ASTMACompilationUnit astB = MontiArcMill.parser().parse(
      Paths.get(packagePath + "/" + "B.arc").toAbsolutePath().toString()).orElseThrow(IllegalArgumentException::new);
    tool.createSymbolTable(astA);
    tool.createSymbolTable(astB);

    // When
    tool.completeSymbolTable(astB);

    // Then
    Assertions.assertTrue(astB.getSpannedScope().getSubScopes().get(0)
      .resolveComponentInstanceLocally("a").isPresent());
    Assertions.assertNotNull(astB.getSpannedScope().getSubScopes().get(0)
      .resolveComponentInstanceLocally("a").get().getType());
  }

  /**
   * Method under test {@link MontiArcTool#runDefaultTasks(Collection)}
   */
  @Test
  public void runDefaultTasksShouldThrowNullPointerException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.runDefaultTasks(null));
  }

  /**
   * Method under test {@link MontiArcTool#runAdditionalTasks(Collection, CommandLine)}
   */
  @Test
  public void runAdditionalTasksShouldThrowException() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String pakkage = "nestedComponent";
    String packagePath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, pakkage);
    Collection<ASTMACompilationUnit> innerComponents =
      tool.parse(".arc", Paths.get(packagePath).toAbsolutePath());
    String[] args = new String[]{"-pp", RELATIVE_MODEL_PATH + "/" + "file.arc", "-s", RELATIVE_MODEL_PATH};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAdditionalTasks(null, cli));
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAdditionalTasks(innerComponents, null));
  }

  /**
   * Method under test {@link MontiArcTool#runAdditionalTasks(Collection, CommandLine)}
   */
  @Test
  public void runAdditionalTasksShouldPrettyPrintFile() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String modelBasePath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR);
    File file = new File(Paths.get(modelBasePath + "/prettyprint/WithInnerComponents.arc")
      .toAbsolutePath().toString());
    if (file.exists()) {
      Assertions.assertTrue(file.delete());
    }
    Collection<ASTMACompilationUnit> innerComponents =
      tool.parse(".arc", Paths.get(modelBasePath + "/" + "nestedComponent").toAbsolutePath());
    String[] args = new String[]{"-pp", modelBasePath + "/" + "prettyprint/"};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When && Then
    Assertions.assertDoesNotThrow(() -> tool.runAdditionalTasks(innerComponents, cli));
    Assertions.assertTrue(file.exists());
  }

  /**
   * Method under test {@link MontiArcTool#runAdditionalTasks(Collection, CommandLine)}
   */
  @Test
  public void runAdditionalTasksShouldOutputSymbolTable() throws ParseException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    Options options = tool.initOptions();
    String serializeDir = String.join("/", System.getProperty("buildDir"), "test-sources", TEST_DIR, "symboltable");
    File file = new File(Paths.get(serializeDir + "/WithInnerComponents.arcsym")
      .toAbsolutePath().toString());
    if (file.exists()) {
      Assertions.assertTrue(file.delete());
    }
    String parsePath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR, "nestedComponent");
    Collection<ASTMACompilationUnit> innerComponents =
      tool.parse(".arc", Paths.get(parsePath).toAbsolutePath());
    String[] args = new String[]{"-s", serializeDir};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);
    tool.createSymbolTable(innerComponents);
    tool.completeSymbolTable(innerComponents);

    // When && Then
    Assertions.assertDoesNotThrow(() -> tool.runAdditionalTasks(innerComponents, cli));
    Assertions.assertTrue(file.exists());

    // Clean-up
    file.delete();
  }

  /**
   * Method under test {@link MontiArcTool#storeSymbols(Collection, String)}
   */
  @ParameterizedTest
  @MethodSource("storeSymbolsCollectionExpectedExceptionProvider")
  public void storeSymbolsCollectionShouldThrowException(@Nullable String path,
                                                         @Nullable Collection<ASTMACompilationUnit> asts,
                                                         @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.storeSymbols(asts, path));
  }

  protected static Stream<Arguments> storeSymbolsCollectionExpectedExceptionProvider() {
    MontiArcTool tool = new MontiArcTool();
    String baseModelPath = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR);
    Collection<ASTMACompilationUnit> innerComponents =
      tool.parse(".arc", Paths.get(baseModelPath + "/nestedComponent").toAbsolutePath());
    return Stream.of(
      Arguments.of(null, innerComponents, NullPointerException.class),
      Arguments.of(baseModelPath +"/symboltable/", null, NullPointerException.class),
      Arguments.of("", innerComponents, IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link MontiArcTool#storeSymbols(ASTMACompilationUnit, String)}
   */
  @ParameterizedTest
  @MethodSource("storeSymbolsAstExpectedExceptionProvider")
  public void storeSymbolsAstShouldThrowException(@Nullable String path,
                                                  @Nullable ASTMACompilationUnit ast,
                                                  @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.storeSymbols(ast, path));
  }

  protected static Stream<Arguments> storeSymbolsAstExpectedExceptionProvider() {
    MontiArcTool tool = new MontiArcTool();

    String baseTestDir = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR);
    ASTMACompilationUnit ast = tool.parse(
      Paths.get(baseTestDir + "/nestedComponent/WithInnerComponents.arc").toAbsolutePath()).orElseThrow(IllegalStateException::new);
    ast.setSpannedScope(MontiArcMill.scope());
    return Stream.of(
      Arguments.of(null, ast, NullPointerException.class),
      Arguments.of(baseTestDir + "/symboltable/", null, NullPointerException.class),
      Arguments.of("", ast, IllegalArgumentException.class)
    );
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
    String baseTestDir = String.join("/", RELATIVE_MODEL_PATH, TEST_DIR);
    Optional<ASTMACompilationUnit> optAst = tool.parse(
      Paths.get(baseTestDir + "/nestedComponent/WithInnerComponents.arc").toAbsolutePath()
    );
    Assertions.assertTrue(optAst.isPresent());
    ASTMACompilationUnit ast = optAst.get();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    IMontiArcArtifactScope scope = (MontiArcArtifactScope) ast.getSpannedScope();
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
      stream().anyMatch(symbolResolver -> symbolResolver instanceof Class2MCResolver));
    Assertions.assertFalse(MontiArcMill.globalScope().getAdaptedOOTypeSymbolResolverList().
      stream().anyMatch(symbolResolver -> symbolResolver instanceof Class2MCResolver));
    String[] args = new String[]{"-c2mc"};
    CommandLineParser cliParser = new DefaultParser();
    CommandLine cli = cliParser.parse(options, args);

    // When
    tool.initializeClass2MC(cli);

    // Then
    Assertions.assertTrue(MontiArcMill.globalScope().getAdaptedTypeSymbolResolverList().
      stream().anyMatch(symbolResolver -> symbolResolver instanceof Class2MCResolver));
    Assertions.assertTrue(MontiArcMill.globalScope().getAdaptedOOTypeSymbolResolverList().
      stream().anyMatch(symbolResolver -> symbolResolver instanceof Class2MCResolver));
  }
}