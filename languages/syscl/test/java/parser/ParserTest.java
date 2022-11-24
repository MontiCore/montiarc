/* (c) https://github.com/MontiCore/monticore */
package parser;

import de.se_rwth.commons.logging.Log;
import montiarc.util.AbstractTest;
import montiarc.util.ArcError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import syscl.SysCLMill;
import syscl._ast.ASTSysCLCompilationUnit;
import syscl._parser.SysCLParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ParserTest extends AbstractTest {

  @BeforeEach
  public void init() {
    Log.init();
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    SysCLMill.globalScope().clear();
    SysCLMill.reset();
    SysCLMill.init();
  }

  protected static final String PACKAGE = "parser";

  @ParameterizedTest
  @ValueSource(strings = {
    "lecture/Adder.spec",
    "lecture/Delay.spec",
    "lecture/Nor.spec",
    "lecture/Storage.spec",
    "lecture/SumUp.spec"
  })
  public void shouldParseWithoutError(String fileName) {
    SysCLParser parser = SysCLMill.parser();
    Optional<ASTSysCLCompilationUnit> optAst;
    try {
      optAst = parser.parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertAll(
      () -> assertThat(optAst.isPresent()).isTrue(),
      () -> assertThat(parser.hasErrors()).isFalse(),
      () -> assertThat(Log.getFindings()).isEmpty()
    );
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }
}