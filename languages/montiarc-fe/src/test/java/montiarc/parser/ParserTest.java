/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc._ast.ASTArcTiming;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import montiarc.util.Error;
import montiarc.util.MontiArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest extends AbstractTest {

  protected static final String PACKAGE = "montiarc/parser";

  static public Optional<ASTMACompilationUnit> parse(String relativeFilePath) {
    return parse(relativeFilePath, false);
  }

  static public Optional<ASTMACompilationUnit> parse(String relativeFilePath,
      boolean expParserErrors) {
    MontiArcParser parser = new MontiArcParser();
    Optional<ASTMACompilationUnit> optAst;
    try {
      optAst = parser.parse(relativeFilePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (expParserErrors) {
      Assertions.assertTrue(parser.hasErrors());
      Assertions.assertFalse(optAst.isPresent());
    } else {
      Assertions.assertFalse(parser.hasErrors(), Log.getFindings().toString());
      Assertions.assertTrue(optAst.isPresent());
    }
    return optAst;
  }

  static public Optional<ASTMACompilationUnit> parse_String(String content,
      boolean expParserErrors) {
    MontiArcParser parser = new MontiArcParser();
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
  @ValueSource(strings = {"ComponentCoveringMostOfConcreteSyntax.arc"})
  public void shouldParseWithoutError(String fileName) {
    this.parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), false);
  }

  @ParameterizedTest
  @ValueSource(strings = "MultipleInheritance.arc")
  public void shouldParseWithErrors(String fileName) {
    this.parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), true);
  }

  @ParameterizedTest
  @MethodSource("filenameAndErrorCodeProvider")
  public void shouldParseWithSpecifiedErrorsOnly(String fileName,
    Error... expErrors) {
    this.parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), true);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), expErrors);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ComponentCoveringMostOfConcreteSyntax.arc"})
  public void shouldPrintWithoutError(String fileName) {
    ASTMACompilationUnit unit = parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), false).orElse(null);
    String s = new MontiArcFullPrettyPrinter().prettyprint(unit);
    ASTMACompilationUnit similarUnit = parse_String(s,false).orElse(null);
    if(!unit.deepEquals(similarUnit)){
      Log.error("PrettyPrinted ASTMACompilationUnit has changed");
    }
  }

  @Test
  public void shouldParseTimingAsCorrectNode() {
    String fileName = "ComponentWithTiming.arc";
    ASTMACompilationUnit ast =
      this.parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), false).orElse(null);
    Assertions.assertTrue(ast != null);
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
}