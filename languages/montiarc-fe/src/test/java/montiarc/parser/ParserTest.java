/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

import de.se_rwth.commons.logging.Log;
import montiarc._parser.MontiArcParser;
import montiarc.util.Error;
import montiarc.util.MontiArcError;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.AbstractTest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ParserTest extends AbstractTest {

  protected static final String PACKAGE = "montiarc/parser";

  @Override
  protected Pattern supplyErrorCodePattern() {
    return MontiArcError.ERROR_CODE_PATTERN;
  }

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

  static Stream<Arguments> filenameAndErrorCodeProvider() {
    return Stream.of(
      Arguments.of("ComponentAndFileNameDiffer.arc",
        new Error[]{MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER}),
      Arguments.of("PackageAndLocationDiffer.arc",
        new Error[]{MontiArcError.COMPONENT_AND_FILE_PACKAGE_DIFFER}));
  }
}