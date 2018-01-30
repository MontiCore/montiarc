package component.body.variables;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class checks all context conditions related the combination of elements in component
 * bodies
 *
 * @author Andreas Wortmann
 */
public class VariableTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.variables";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAmbiguousVariableNames() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AmbiguousVariableNames");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(2, "xMA035"));
  }
  
}
