package components.body.variables;

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
public class VariableTests extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.variables";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAmbiguousVariableNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousVariableNames");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(2, "xMA035"));
  }
  
}
