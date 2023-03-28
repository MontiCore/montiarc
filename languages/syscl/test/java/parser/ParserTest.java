/* (c) https://github.com/MontiCore/monticore */
package parser;

import de.se_rwth.commons.logging.Log;
import montiarc.util.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import syscl.SysCLMill;
import syscl._ast.ASTSysCLCompilationUnit;
import syscl._parser.SysCLParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ParserTest extends AbstractTest {

  @BeforeEach
  public void setUp() {
    Log.clearFindings();
    SysCLMill.globalScope().clear();
    SysCLMill.reset();
    SysCLMill.init();
  }

  protected static final String PACKAGE = "parser";

  @ParameterizedTest
  @ValueSource(strings = {
    "prepost/Adder.spec",
    "prepost/Delay.spec",
    "prepost/Nor.spec",
    "prepost/Storage.spec",
    "prepost/SumUp.spec",
    "ag/Add.spec",
    "ag/AcSum.spec",
    "ag/FragileDelay.spec",
    "ag/PropPaxExch.spec",
    "ag/SynchAdd.spec"
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
}