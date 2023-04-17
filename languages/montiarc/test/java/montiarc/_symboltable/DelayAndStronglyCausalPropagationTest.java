/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Optional;

public class DelayAndStronglyCausalPropagationTest extends MontiArcAbstractTest {

  protected static final String TEST_MODEL_PATH = "symboltable/completion/";

  @Test
  public void propagateTransitiveDelay() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    tool.initializeClass2MC();

    // When
    ASTMACompilationUnit ast = tool.parse(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH, "timing/PropagateTiming.arc"))
        .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
    tool.createSymbolTable(ast);
    tool.runSymbolTablePhase2(ast);
    tool.runSymbolTablePhase3(ast);
    tool.runAfterSymbolTablePhase3Trafos(ast);
    tool.runDefaultCoCos(ast);
    IArcBasisScope scope = ast.getComponentType().getSpannedScope();

    // Then
    Optional<PortSymbol> pDirectDelayed = scope.resolvePort("pDirectDelayed"),
      pChainedDelayed = scope.resolvePort("pChainedDelayed"),
      pNotDelayed = scope.resolvePort("pNotDelayed"),
      pNoPathToIn = scope.resolvePort("pNoPathToIn");

    Assertions.assertTrue(pDirectDelayed.isPresent());
    Assertions.assertTrue(pChainedDelayed.isPresent());
    Assertions.assertTrue(pNotDelayed.isPresent());
    Assertions.assertTrue(pNoPathToIn.isPresent());

    Assertions.assertTrue(pDirectDelayed.get().isDelayed());
    Assertions.assertTrue(pDirectDelayed.get().isStronglyCausal());

    Assertions.assertFalse(pChainedDelayed.get().isDelayed());
    Assertions.assertTrue(pChainedDelayed.get().isStronglyCausal());

    Assertions.assertFalse(pNotDelayed.get().isDelayed());
    Assertions.assertFalse(pNotDelayed.get().isStronglyCausal());

    Assertions.assertFalse(pNoPathToIn.get().isDelayed());
    Assertions.assertTrue(pNoPathToIn.get().isStronglyCausal());
  }
}
