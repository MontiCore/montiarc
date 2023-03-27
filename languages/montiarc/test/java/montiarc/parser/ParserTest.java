/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTArcTiming;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc.util.Error;
import montiarc.util.MontiArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest extends MontiArcAbstractTest {

  protected static final String PACKAGE = "parser";

  static public Optional<ASTMACompilationUnit> parse(String relativeFilePath,
      boolean expParserErrors) {
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> optAst;
    try {
      optAst = parser.parse(relativeFilePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Assertions.assertEquals(expParserErrors, parser.hasErrors(), Log.getFindings().toString());
    Assertions.assertEquals(!expParserErrors, optAst.isPresent());
    return optAst;
  }

  static public Optional<ASTMACompilationUnit> parse_String(String content,
      boolean expParserErrors) {
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> optAst;
    try {
      optAst = parser.parse_String(content);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (expParserErrors) {
      assertThat(parser.hasErrors()).isTrue();
      assertThat(optAst).isNotPresent();
    }
    else {
      if (parser.hasErrors()) {
        System.err.println(Log.getFindings().toString());
      }
      assertThat(parser.hasErrors()).isFalse();
      assertThat(optAst).isPresent();
    }
    return optAst;
  }

  @ParameterizedTest
  @ValueSource(strings = {"ComponentCoveringMostOfConcreteSyntax.arc", "VariabilitySyntax.arc"})
  public void shouldParseWithoutError(String fileName) {
    parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), false);
  }

  @ParameterizedTest
  @ValueSource(strings = "MultipleInheritance.arc")
  public void shouldParseWithErrors(String fileName) {
    parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), true);
  }

  @ParameterizedTest
  @MethodSource("filenameAndErrorCodeProvider")
  public void shouldParseWithSpecifiedErrorsOnly(String fileName,
    Error... expErrors) {
    Path path = Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName);
    parse(path.toString(), true);
    this.checkOnlyExpectedErrorsPresent(expErrors, path.toAbsolutePath());
  }

  @ParameterizedTest
  @ValueSource(strings = {"ComponentCoveringMostOfConcreteSyntax.arc", "VariabilitySyntax.arc"})
  public void shouldPrintWithoutError(String fileName) {
    ASTMACompilationUnit unit = parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), false).orElseThrow();
    String s = MontiArcMill.prettyPrint(unit, true);
    ASTMACompilationUnit similarUnit = parse_String(s,false).orElse(null);
    if(!unit.deepEquals(similarUnit)){
      Log.error("PrettyPrinted ASTMACompilationUnit has changed");
    }
  }

  @ParameterizedTest
  @MethodSource("filenameAndASTProvider")
  public void shouldParseWithoutErrorAndBeEqualTo(String fileName, ASTMACompilationUnit unit) {
    Path path = Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName);
    ASTMACompilationUnit parsedUnit =
      parse(path.toString(), false).orElse(null);
    Assertions.assertNotNull(parsedUnit, path.toAbsolutePath().toString());
    Assertions.assertTrue(unit.deepEquals(parsedUnit), path.toAbsolutePath().toString());
  }

  @Test
  public void shouldParseTimingAsCorrectNode() {
    String fileName = "ComponentWithTiming.arc";
    ASTMACompilationUnit ast =
      parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), false).orElse(null);
    Assertions.assertNotNull(ast);
    Assertions.assertEquals(3, ast.getComponentType().getBody().getArcElementList().size());
    Assertions.assertTrue(ast.getComponentType().getBody().getArcElement(0) instanceof ASTArcTiming);
    Assertions.assertTrue(ast.getComponentType().getBody().getArcElement(1) instanceof ASTArcTiming);
    Assertions.assertTrue(ast.getComponentType().getBody().getArcElement(2) instanceof ASTArcTiming);
  }

  static Stream<Arguments> filenameAndErrorCodeProvider() {
    return Stream.of(
      Arguments.of("ComponentAndFileNameDiffer.arc",
        new Error[]{MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER}),
      Arguments.of("PackageAndLocationDiffer.arc",
        new Error[]{MontiArcError.COMPONENT_AND_FILE_PACKAGE_DIFFER}));
  }

  static Stream<Arguments> filenameAndASTProvider() {
    return Stream.of(
      Arguments.of("VariabilitySyntax.arc",
        VariabilityParserTestHelper.getVariabilitySyntaxArcASTUnit().build()));
  }
}