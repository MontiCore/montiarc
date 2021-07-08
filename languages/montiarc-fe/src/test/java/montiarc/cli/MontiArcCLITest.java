/* (c) https://github.com/MontiCore/monticore */
package montiarc.cli;

import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc.util.Error;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Holds tests for {@link MontiArcCLI}.
 */
public class MontiArcCLITest extends AbstractTest {

  protected final PrintStream standardOut = System.out;
  protected final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
  protected final InputStream standardIn = System.in;

  protected static final String PACKAGE = "montiarc/cli";

  @Override
  @BeforeEach
  public void init() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void tearDown() {
    System.setOut(standardOut);
    System.setIn(standardIn);
  }

  @Test
  public void shouldPrintHelp() {
    //Given
    MontiArcCLI cli = new MontiArcCLI();

    //When
    cli.printHelp();

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertTrue(outputStreamCaptor.toString()
      .startsWith("usage: Examples in case the CLI file is called MontiArcCLI.jar:"));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithBuildInTypesProvider")
  public void shouldIncludeBuildInTypes(@NotNull String[] args) throws ParseException {
    Preconditions.checkNotNull(args);
    Preconditions.checkState(MontiArcMill.globalScope().getLocalTypeSymbols().isEmpty());
    //Given
    CommandLine cmd = new CLISetting().handleArgs(args);
    MontiArcCLI cli = new MontiArcCLI();

    //When
    cli.handleBuildInTypes(cmd);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.INT).isPresent());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.DOUBLE).isPresent());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.FLOAT).isPresent());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.SHORT).isPresent());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.LONG).isPresent());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.BOOLEAN).isPresent());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.BYTE).isPresent());
    Assertions.assertTrue(MontiArcMill.globalScope().resolveType(BasicSymbolsMill.CHAR).isPresent());
    Assertions.assertEquals(8, MontiArcMill.globalScope().getLocalTypeSymbols().size());
  }

  @SuppressWarnings("unused")
  protected static Stream<Arguments> argumentsWithBuildInTypesProvider() {
    return Stream.of(
      Arguments.of((Object) new String[]{CLIOption.BUILD_IN_TYPES.printOption(), "true"}),
      Arguments.of((Object) new String[]{CLIOption.BUILD_IN_TYPES.printLongOption(), "true"}),
      Arguments.of((Object) new String[]{}));
  }


  @ParameterizedTest
  @MethodSource("argumentsWithoutBuildInTypesProvider")
  public void shouldExcludeBuildInTypes(@NotNull String[] args) throws ParseException {
    Preconditions.checkNotNull(args);
    Preconditions.checkState(MontiArcMill.globalScope().getLocalTypeSymbols().isEmpty());
    //Given
    CommandLine cmd = new CLISetting().handleArgs(args);
    MontiArcCLI cli = new MontiArcCLI();

    //When
    cli.handleBuildInTypes(cmd);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(0, MontiArcMill.globalScope().getLocalTypeSymbols().size());
  }

  @SuppressWarnings("unused")
  protected static Stream<Arguments> argumentsWithoutBuildInTypesProvider() {
    return Stream.of(
      Arguments.of((Object) new String[]{CLIOption.BUILD_IN_TYPES.printOption()}),
      Arguments.of((Object) new String[]{CLIOption.BUILD_IN_TYPES.printLongOption()}),
      Arguments.of((Object) new String[]{CLIOption.BUILD_IN_TYPES.printOption(), "false"}),
      Arguments.of((Object) new String[]{CLIOption.BUILD_IN_TYPES.printLongOption(), "false"}));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithInputProvider")
  public void shouldParseModelFromFile(@NotNull String[] args) throws ParseException, IOException {
    Preconditions.checkNotNull(args);
    //Given
    CommandLine cmd = new CLISetting().handleArgs(args);
    MontiArcCLI cli = new MontiArcCLI();

    //When
    Optional<ASTMACompilationUnit> ast = cli.parseComponentModel(cmd);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertTrue(ast.isPresent());
  }

  @SuppressWarnings("unused")
  protected static Stream<Arguments> argumentsWithInputProvider() {
    String modelFile =
      Paths.get(RELATIVE_MODEL_PATH, "montiarc/parser/ComponentCoveringMostOfConcreteSyntax.arc").toString();
    return Stream.of(
      Arguments.of((Object) new String[]{CLIOption.INPUT.printOption(), modelFile}),
      Arguments.of((Object) new String[]{CLIOption.INPUT.printLongOption(), modelFile}));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithStdinProvider")
  public void shouldParseModelFromStream(@NotNull String[] args, @NotNull String reader) throws ParseException,
    IOException {
    Preconditions.checkNotNull(args);
    Preconditions.checkNotNull(reader);
    //Given
    CommandLine cmd = new CLISetting().handleArgs(args);
    MontiArcCLI cli = new MontiArcCLI();

    //When
    System.setIn(new ByteArrayInputStream(reader.getBytes()));
    Optional<ASTMACompilationUnit> ast = cli.parseComponentModel(cmd);

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertTrue(ast.isPresent());
  }

  @SuppressWarnings("unused")
  protected static Stream<Arguments> argumentsWithStdinProvider() {
    String model = "component A { }";
    return Stream.of(
      Arguments.of(new String[]{CLIOption.STDIN.printOption()}, model),
      Arguments.of(new String[]{CLIOption.STDIN.printLongOption()}, model));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithPathProvider")
  public void shouldCreateSymbolTableAndCheckCoCos(@NotNull String[] args) throws ParseException, IOException {
    Preconditions.checkNotNull(args);
    //Given
    BasicSymbolsMill.initializePrimitives();
    CommandLine cmd = new CLISetting().handleArgs(args);
    MontiArcCLI cli = new MontiArcCLI();
    Optional<ASTMACompilationUnit> ast = new MontiArcParser()
      .parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, "CLI.arc").toString());

    Preconditions.checkState(ast.isPresent());

    //When
    cli.createSymbolTable(cmd, ast.get());
    cli.checkCoCos(ast.get());

    //Then
    Assertions.assertNotNull(ast.get().getSpannedScope());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @SuppressWarnings("unused")
  protected static Stream<Arguments> argumentsWithPathProvider() {
    return Stream.of(
      Arguments.of((Object) new String[]{CLIOption.PATH.printOption(), Paths.get(RELATIVE_MODEL_PATH, PACKAGE).toString()}),
      Arguments.of((Object) new String[]{CLIOption.PATH.printLongOption(), Paths.get(RELATIVE_MODEL_PATH, PACKAGE).toString()}));
  }

  @Test
  public void shouldDetectCoCoViolation() throws ParseException, IOException {
    //Given
    CommandLine cmd = new CLISetting().handleArgs(new String[]{});
    MontiArcCLI cli = new MontiArcCLI();
    Optional<ASTMACompilationUnit> ast = new MontiArcParser().parse_String("component A { MissingCompType sub; }");

    Preconditions.checkState(ast.isPresent());

    //When
    cli.createSymbolTable(cmd, ast.get());
    cli.checkCoCos(ast.get());

    //Then
    this.checkExpectedErrorsPresent(new Error[]{ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE});
  }
}