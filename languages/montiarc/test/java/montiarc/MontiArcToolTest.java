/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc.util.Error;
import montiarc.util.MCError;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.CommandLine;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Holds tests for {@link MontiArcTool}.
 */
public class MontiArcToolTest extends MontiArcTestBase {

  protected final static String TEST_DIR = "clitool";

  /**
   * Method under test {@link MontiArcTool#run(String[])}
   */
  @Test
  public void rungWithNullStringShouldThrow() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.run(null));
  }

  /**
   * Method under test {@link MontiArcTool#doRun(CommandLine)}
   */
  @Test
  public void runWithNullCLShouldThrow() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> tool.doRun(null));
  }

  /**
   * Method under test {@link MontiArcTool#run(String[])}
   */
  @Test
  public void runInvalidOptionShouldThrowToolParseIOException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(new String[]{"-notavalidoption"});

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(MontiArcError.TOOL_PARSE_IOEXCEPTION));
  }

  /**
   * Method under test {@link MontiArcTool#initGlobalScope(String...)}
   */
  @Test
  public void initGlobalScopeShouldSetSymbolPath() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    MontiArcMill.globalScope().getSymbolPath().close();
    MontiArcMill.globalScope().setSymbolPath(new MCPath());
    // create copy to see that nothing gets removed

    String path = "test/resources/CLI/industryModels/industry";
    String[] args = new String[]{path};

    // When
    tool.initGlobalScope(args);

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
   * Method under test {@link MontiArcTool#doRun(CommandLine)}
   */
  @ParameterizedTest
  @MethodSource("runTasksExceptionProvider")
  public void runTasksShouldThrowException(@Nullable CommandLine cli,
                                           @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.doRun(cli));
  }

  protected static Stream<Arguments> runTasksExceptionProvider() {
    return Stream.of(
      Arguments.of(null, NullPointerException.class)
    );
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Collection)}
   */
  @ParameterizedTest
  @MethodSource("parseDirectoriesExpectedExceptionProvider")
  public void parseDirectoriesShouldGiveWarnings(@Nullable String fileExt,
                                                 @Nullable Path[] directories,
                                                 @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(expected, () -> tool.parse(fileExt, List.of(directories)));
  }

  protected static Stream<Arguments> parseDirectoriesExpectedExceptionProvider() {
    String modelPath = Paths.get(TEST_RESOURCE, TEST_DIR, "validFileStructureMock/validPackageMock").toString();

    Path path = Paths.get(modelPath);
    return Stream.of(
      Arguments.of(null, new Path[]{path}, NullPointerException.class),
      Arguments.of("", new Path[]{path}, IllegalArgumentException.class),
      Arguments.of(".arc", null, NullPointerException.class)
    );
  }

  @ParameterizedTest
  @MethodSource("nonExistentDirectories")
  public void parseNonExistentDirectoryShouldGiveWarning(@Nullable String fileExt,
                                                         @Nullable Path[] directories) {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.parse(fileExt, List.of(directories));

    // Then
    Assertions.assertEquals(1, Log.getFindingsCount());
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  protected static Stream<Arguments> nonExistentDirectories() {
    String modelPath = Paths.get(TEST_RESOURCE, TEST_DIR, "non/existent").toString();
    return Stream.of(
      Arguments.of(".arc", new Path[]{Path.of(modelPath)})
    );
  }

  /**
   * Method under test {@link MontiArcTool#parse(String, Collection)}
   */
  @Test
  public void shouldParseDirectories() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String modelPath = Paths.get(TEST_RESOURCE, TEST_DIR, "validFileStructureMock").toString();

    // When
    Collection<ASTMACompilationUnit> asts = tool.parse("arc", List.of(Path.of(modelPath)));

    //Then
    Assertions.assertTrue(asts.stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("validPackageMock")));
    Assertions.assertTrue(asts.stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("validPackageMock2")));
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
    tool.parse("arc", Paths.get(""));
    Assertions.assertEquals(Log.getErrorCount(), 0);

  }

  protected static Stream<Arguments> parseDirectoryExpectedExceptionProvider() {
    String subPackageDir = "validFileStructureMock/validPackageMock";
    Path mockModelPath = Paths.get(TEST_RESOURCE, TEST_DIR, subPackageDir);

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
    String subTestDir = "validFileStructureMock";
    String modelPath = Paths.get(TEST_RESOURCE, TEST_DIR, subTestDir).toAbsolutePath().toString();

    // When && Then
    Assertions.assertTrue(tool.parse("arc", Paths.get(modelPath).toAbsolutePath()).stream()
      .anyMatch(ast -> ast.getPackage().getQName().equals("validPackageMock")));
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
      Arguments.of(Paths.get(TEST_RESOURCE + TEST_DIR).toAbsolutePath(),
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
    Path modelLocation = Paths.get(TEST_RESOURCE, TEST_DIR, qualifiedModelName);

    // When
    Optional<ASTMACompilationUnit> ast2 = tool.parse(modelLocation.toAbsolutePath());

    // Then
    Assertions.assertTrue(ast2.isPresent());
    Assertions.assertEquals("ValidMockComponent", ast2.get().getArcComponentType().getName());
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
    String subTestDir = "validFileStructureMock";
    Path modelPath = Paths.get(TEST_RESOURCE, TEST_DIR, subTestDir);
    Collection<ASTMACompilationUnit> asts = tool.parse("arc", modelPath);
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
    Path modelLocation = Paths.get(TEST_RESOURCE, TEST_DIR, qualifiedModelName);
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
   * Method under test {@link MontiArcTool#runAfterSymbolTablePhase2Trafos(Collection)}
   */
  @Test
  void runAfterSymbolTablePhase2TrafosCollectionShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAfterSymbolTablePhase2Trafos((Collection<ASTMACompilationUnit>) null));
  }

  /**
   * Method under test {@link MontiArcTool#runAfterSymbolTablePhase2Trafos(ASTMACompilationUnit)}
   */
  @Test
  void runAfterSymbolTablePhase2TrafosShouldThrowException() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> tool.runAfterSymbolTablePhase2Trafos((ASTMACompilationUnit) null));
  }

  /**
   * Method under test {@link MontiArcTool#runSymbolTablePhase2(ASTMACompilationUnit)}
   */
  @Test
  public void shouldRunSymbolTablePhase2() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String pakkage = "symboltable/pass2";
    Path packagePath = Paths.get(TEST_RESOURCE, TEST_DIR, pakkage);

    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      packagePath.resolve("A.arc").toAbsolutePath().toString()).orElseThrow(IllegalArgumentException::new);
    ASTMACompilationUnit astB = MontiArcMill.parser().parse(
      packagePath.resolve("B.arc").toAbsolutePath().toString()).orElseThrow(IllegalArgumentException::new);
    tool.createSymbolTable(astA);
    tool.createSymbolTable(astB);

    // When
    tool.runSymbolTablePhase2(astB);

    // Then
    Assertions.assertTrue(astB.getEnclosingScope().getSubScopes().getFirst()
      .resolveSubcomponentLocally("a").isPresent());
    Assertions.assertNotNull(astB.getEnclosingScope().getSubScopes().getFirst()
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
    Path packagePath = Paths.get(TEST_RESOURCE, TEST_DIR, pakkage);

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
    ComponentTypeSymbol aCompType = astA.getArcComponentType().getSymbol();
    ComponentTypeSymbol bCompType = astB.getArcComponentType().getSymbol();
    PortSymbol aInPort = aCompType.getPort("inPortA").orElseThrow();
    PortSymbol bInPort = bCompType.getPort("inPortB").orElseThrow();
    SubcomponentSymbol aInstance = bCompType.getSubcomponents("a").orElseThrow();

    ASTConnector connector = astB.getArcComponentType().getConnectors().getFirst();
    ASTPortAccess bAccess = connector.getSource();
    ASTPortAccess aAccess = connector.getTarget(0);

    Assertions.assertTrue(bAccess.isPresentPortSymbol(), "Port of b should be present.");
    Assertions.assertTrue(aAccess.isPresentPortSymbol(), "Port of a should be present.");
    Assertions.assertTrue(aAccess.isPresentComponentSymbol(), "Comp of a should be present.");
    Assertions.assertFalse(bAccess.isPresentComponentSymbol(), "Comp of b should not be present.");
    Assertions.assertSame(bInPort, bAccess.getPortSymbol(), "B Port mismatch");
    Assertions.assertSame(aInPort, aAccess.getPortSymbol(), "A Port mismatch");
    Assertions.assertSame(aInstance, aAccess.getComponentSymbol(), "B component mismatch");
  }

  @Test
  void shouldRunAfterParsingTransformations() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();

    String pakkage = "transformations/afterParsingTrafos";
    Path packagePath = Paths.get(TEST_RESOURCE, TEST_DIR, pakkage);

    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      packagePath.resolve("A.arc").toAbsolutePath().toString()).orElseThrow();

    int connectorCountBeforeTrafo = astA.getArcComponentType().getConnectors().size();

    // When
    tool.runAfterParsingTrafos(astA);

    // Then
    int connectorCountAfterTrafo = astA.getArcComponentType().getConnectors().size();

    Assertions.assertTrue(connectorCountAfterTrafo > connectorCountBeforeTrafo, "Expected new connectors");
  }

  /**
   * Method under test {@link MontiArcTool#runAfterSymbolTablePhase2Trafos(ASTMACompilationUnit)}
   */
  @Test
  void shouldRunAfterSymbolLinkingTransformations() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();
    String pakkage = "transformations/afterSymbolLinkingTrafos";
    Path packagePath = Paths.get(TEST_RESOURCE, TEST_DIR, pakkage);

    ASTMACompilationUnit astA = MontiArcMill.parser().parse(
      packagePath.resolve("A.arc").toAbsolutePath().toString()).orElseThrow();
    ASTMACompilationUnit astB = MontiArcMill.parser().parse(
      packagePath.resolve("B.arc").toAbsolutePath().toString()).orElseThrow();
    tool.createSymbolTable(astA);
    tool.createSymbolTable(astB);
    tool.runSymbolTablePhase2(astA);
    tool.runSymbolTablePhase2(astB);

    int connectorCountBeforeTrafo = astB.getArcComponentType().getConnectors().size();

    // When
    tool.runAfterSymbolTablePhase2Trafos(astB);

    // Then
    long connectorCountAfterTrafo = astB.getArcComponentType().getConnectors().size();

    Assertions.assertTrue(connectorCountAfterTrafo > connectorCountBeforeTrafo, "Expected new connectors");
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
    Path baseTestDir = Paths.get(TEST_RESOURCE, TEST_DIR);
    Optional<ASTMACompilationUnit> optAst = tool.parse(
      baseTestDir.resolve("storeSymbols").resolve("WithInnerComponents.arc").toAbsolutePath()
    );
    Assertions.assertTrue(optAst.isPresent());
    ASTMACompilationUnit ast = optAst.get();
    tool.createSymbolTable(ast);
    tool.runSymbolTablePhase2(ast);
    tool.runAfterSymbolTablePhase2Trafos(ast);
    tool.runSymbolTablePhase3(ast);
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

  @Test
  void shouldAddJavaLangImport() throws IOException {
    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit("component A {}").orElseThrow();

    MontiArcTool tool = new MontiArcTool();

    // When
    tool.defaultImportTrafo(ast, true);
    tool.createSymbolTable(ast);

    // Then
    List<ImportStatement> imports = ((IMontiArcArtifactScope) ast.getEnclosingScope()).getImportsList();
    Assertions.assertTrue(imports.stream()
      .anyMatch(i -> i.getStatement().equals("java.lang") && i.isStar()), "Import to java.lang.* should be present.");
  }

  @ParameterizedTest
  @MethodSource("argAndErrorProvider")
  public void shouldDetectInvalidFilePath(@NotNull String[] args, @NotNull Error[] errors) {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> argAndErrorProvider() {
    final String PATH = Path.of(TEST_RESOURCE, TEST_DIR, "filepath").toString();

    return Stream.of(
      // 1
      Arguments.of(
        new String[]{
          "-i", PATH + "/p", PATH + "/p"
        },
        new Error[]{
          MontiArcError.SUPERIMPOSED_MODELPATH
        }
      ),
      // 2
      Arguments.of(
        new String[]{
          "-i", PATH + "/p",
          PATH + "/p/path"
        },
        new Error[]{
          MontiArcError.SUPERIMPOSED_MODELPATH
        }
      ),
      // 3
      Arguments.of(
        new String[]{
          "-i", PATH + "/p/s",
          PATH + "/p"
        },
        new Error[]{
          MontiArcError.SUPERIMPOSED_MODELPATH
        }
      ),
      // 4
      Arguments.of(
        new String[]{
          "-i", PATH + "/p",
          PATH + "/p/path",
          PATH + "/p/path2"
        },
        new Error[]{
          MontiArcError.SUPERIMPOSED_MODELPATH,
          MontiArcError.SUPERIMPOSED_MODELPATH
        }
      ),
      // 5
      Arguments.of(
        new String[]{
          "-i", PATH + "/p",
          PATH + "/p/path/sub1/Comp1.arc"
        },
        new Error[]{
          MontiArcError.SUPERIMPOSED_MODELPATH
        }
      ),
      // 6
      Arguments.of(
        new String[]{
          "-i",
          PATH + "/p/path/sub1/Comp1.arc",
          PATH + "/p"
        },
        new Error[]{
          MontiArcError.SUPERIMPOSED_MODELPATH
        }
      ),
      // 7
      Arguments.of(
        new String[]{
          "-i",
          PATH + "/p/path/sub1/Comp1.arc",
          PATH + "/p/path/sub1/Comp1.arc"
        },
        new Error[]{
          MontiArcError.SUPERIMPOSED_MODELPATH
        }
      ),
      // 8
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndPathDiffer"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 9
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndPathDiffer2"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 10
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndPathDiffer3"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 11
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndPathDiffer", PATH + "/pkgAndPathDiffer2"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER,
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 12
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndPathDiffer/path/sub1/Comp1.arc"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 13
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndPathDiffer2/path/sub1/Comp2.arc"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 14
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndPathDiffer3/path/sub2/Comp1.arc"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 15
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndSubpathDiffer"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 16
      Arguments.of(
        new String[]{
          "-i", PATH + "/pkgAndSubpathDiffer2"
        },
        new Error[]{
          MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER
        }
      ),
      // 17
      Arguments.of(
        new String[]{
          "-i", PATH + "/nameAndFileDiffer"
        },
        new Error[]{
          MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER
        }
      ),
      // 18
      Arguments.of(
        new String[]{
          "-i", PATH + "/nameAndFileDiffer2"
        },
        new Error[]{
          MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER
        }
      ),
      // 19
      Arguments.of(
        new String[]{
          "-i", PATH + "/nameAndFileDiffer/path/sub1/Comp1.arc"
        },
        new Error[]{
          MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER
        }
      ),
      // 20
      Arguments.of(
        new String[]{
          "-i", PATH + "/nameAndFileDiffer2/path/sub2/Comp1.arc"
        },
        new Error[]{
          MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER
        }
      )
    );
  }

  @ParameterizedTest
  @MethodSource("argProvider")
  public void shouldSucceed(@NotNull String[] args) {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().toString());
  }

  protected static Stream<Arguments> argProvider() {
    final String PATH = Path.of(TEST_RESOURCE, TEST_DIR, "filepath").toString();

    return Stream.of(
      Arguments.of((Object) new String[]{"-i", PATH + "/pkgAndPathMatch"}),
      Arguments.of((Object) new String[]{"-i", PATH + "/pkgAndPathMatch/path/sub1/Comp1.arc"}),
      Arguments.of((Object) new String[]{"-i", PATH + "/pkgAndSubpathMatch/path/sub1/Comp1.arc"}),
      Arguments.of((Object) new String[]{"-i", PATH + "/pkgAndSubpathMatch/path/sub2/Comp1.arc"})
    );
  }

  @Test
  public void expectedErrorForLibraryImport(@TempDir Path tempDir) {
    // Given
    Path lib1Model = Path.of(TEST_RESOURCE, "endtoend", "library", "library1");
    Path lib2Model = Path.of(TEST_RESOURCE, "endtoend", "library", "library2");
    Path mainModel = Path.of(TEST_RESOURCE, "endtoend", "library", "ImportFromTwoLibraries.arc");

    Path symbols1 = tempDir.resolve("symbols1");
    Path symbols2 = tempDir.resolve("symbols2");

    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(new String[]{"-i", lib1Model.toString(), "-s", symbols1.toString()});
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().toString());

    tool.run(new String[]{"-i", lib2Model.toString(), "-s", symbols2.toString()});
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().toString());

    tool.run(new String[]{
      "-i", mainModel.toString(),
      "-path", symbols1.toString(), symbols2.toString()
    });

    // Then
    assertThat(getLoggedErrorCodes()).as(Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(MCError.AMBIGUOUS_MCPATH_ENTRIES, MCError.MISSING_COMPONENT, MontiArcError.IMPORTED_SYMBOL_MISSING));
  }
}
