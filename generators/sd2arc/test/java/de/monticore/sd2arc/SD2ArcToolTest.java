/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class SD2ArcToolTest extends AbstractTest {

  Path output = Paths.get("build/sd2arc");

  @Test
  public void testCorrectEnd2End() {
    String[] args = new String[]{
      "--coco",
      "-path", SYMBOL_PATH,
      "--input", SYMBOL_PATH + "/sd2arc/correct",
      "--output", output.toAbsolutePath().toString(),
      "--defaultTicks", String.valueOf(20),
    };

    SD2ArcTool.main(args);
  }

  @ParameterizedTest
  @MethodSource("provideIncorrectAndErrorCodes")
  public void toolCocosOnlyExpectedErrorTest(String model, String[] errors) {
    SD2ArcTool tool = new SD2ArcTool();

    tool.init();
    tool.initGlobalScope();
    SD4ComponentsMill.globalScope().setSymbolPath(new MCPath(SYMBOL_PATH));
    Log.enableFailQuick(false);

    ASTSDArtifact ast = tool.parse(SYMBOL_PATH + "/sd2arc/incorrect/" + model);
    Assertions.assertNotNull(ast, Log.getFindings().toString());
    tool.createSymbolTable(ast);

    tool.runDefaultCoCos(ast);
    tool.runAdditionalCoCos(ast);

    checkOnlyExpectedErrorsPresent(errors);
  }

  protected static Stream<Arguments> provideIncorrectAndErrorCodes() {
    return Stream.of(
      Arguments.of("ImpliedConnectorsFitEmbeddingComponent.sd", new String[]{"0xB5101"}),
      Arguments.of("ObserveOnUnconnectedPort.sd", new String[]{"0xB5102"}),
      Arguments.of("ObserveOnUnconnectedPortEmbedded.sd", new String[]{"0xB5102"}),
      Arguments.of("SubcomponentExistsInEmbeddingComponent.sd", new String[]{"0xB5100"})
    );
  }
}
