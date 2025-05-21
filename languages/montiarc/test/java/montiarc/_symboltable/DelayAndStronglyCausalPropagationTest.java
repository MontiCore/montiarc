/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import arcbasis._symboltable.IArcBasisScope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Optional;

public class DelayAndStronglyCausalPropagationTest extends MontiArcTestBase {

  protected static final String TEST_MODEL_PATH = "symboltable/completion/";

  @Test
  public void propagateTransitiveDelay() {
    // Given
    MontiArcTool tool = new MontiArcTool();
    tool.initializeClass2MC();

    // When
    ASTMACompilationUnit ast = tool.parse(Paths.get(TEST_RESOURCE, TEST_MODEL_PATH, "timing/PropagateTiming.arc"))
        .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
    tool.createSymbolTable(ast);
    tool.runSymbolTablePhase2(ast);
    tool.runAfterSymbolTablePhase2Trafos(ast);
    tool.runSymbolTablePhase3(ast);
    tool.runDefaultCoCos(ast);
    IArcBasisScope scope = ast.getArcComponentType().getSpannedScope();

    // Then
    Optional<PortSymbol> pDirectDelayed = scope.resolvePort("pDirectDelayed"),
      pChainedDelayed = scope.resolvePort("pChainedDelayed"),
      pNotDelayed = scope.resolvePort("pNotDelayed"),
      pNoPathToIn = scope.resolvePort("pNoPathToIn");

    Assertions.assertTrue(pDirectDelayed.isPresent());
    Assertions.assertTrue(pChainedDelayed.isPresent());
    Assertions.assertTrue(pNotDelayed.isPresent());
    Assertions.assertTrue(pNoPathToIn.isPresent());

    Assertions.assertTrue(pDirectDelayed.get().getStronglyCausal());

    Assertions.assertTrue(pChainedDelayed.get().getStronglyCausal());

    Assertions.assertFalse(pNotDelayed.get().getStronglyCausal());

    Assertions.assertTrue(pNoPathToIn.get().getStronglyCausal());
  }
}
