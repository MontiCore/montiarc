package components.body;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related the combination of elements in component
 * bodies
 *
 * @author Andreas Wortmann
 */
public class BodyTests extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ComponentWithAJavaAndAutomaton");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA050"));
  }
  
  @Test
  public void testAmbiguousPortAndVariableNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousPortAndVariableNames");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(6,  "xMA035", "xMA053"));
  }
  
}
