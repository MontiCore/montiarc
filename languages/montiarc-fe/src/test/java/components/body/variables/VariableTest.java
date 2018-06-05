package components.body.variables;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related the combination of elements in component bodies
 *
 * @author Andreas Wortmann
 */
public class VariableTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.variables";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAmbiguousVariableNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousVariableNames");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(4, "xMA035"));
  }
  
  @Test
  public void testVariableNamesAreLowerCase() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "VariableNameUpperCase");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA018"));
  }

  @Ignore("NoData is forbidden by grammar, so a test is superfluous at the moment.")
  @Test
  public void testNoDataNotAssigned() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "NoDataNotAssigned");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA999"));

  }
}
