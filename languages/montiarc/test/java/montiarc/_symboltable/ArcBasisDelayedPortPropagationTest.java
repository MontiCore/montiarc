/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.IArcBasisScope;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class ArcBasisDelayedPortPropagationTest extends AbstractTest {

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
    tool.runDefaultCoCos(ast);
    tool.completeSymbolTable(ast);
    IArcBasisScope scope = ast.getComponentType().getSpannedScope();

    // Then
    Assertions.assertTrue(scope.resolvePort("pDirectDelayed").isPresent());
    Assertions.assertTrue(scope.resolvePort("pDirectDelayed").get().isDelayed());
    Assertions.assertTrue(scope.resolvePort("pChainedDelayed").isPresent());
    Assertions.assertTrue(scope.resolvePort("pChainedDelayed").get().isDelayed());
    Assertions.assertTrue(scope.resolvePort("pNotDelayed").isPresent());
    Assertions.assertFalse(scope.resolvePort("pNotDelayed").get().isDelayed());
  }
}
