package components.body.variables;

import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;

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
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA035"));
  }
  
  @Test
  public void testVariableNamesAreLowerCase() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "VariableNameUpperCase");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA018"));
  }

  //@Ignore("NoData is forbidden by grammar, so a test is superfluous at the moment.")
  @Test
  public void testNoDataNotAssigned() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "NoDataNotAssigned");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new AutomatonNoDataAssignedToVariable());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA092"));

  }
}
