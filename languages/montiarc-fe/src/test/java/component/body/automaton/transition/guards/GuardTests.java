package component.body.automaton.transition.guards;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

public class GuardTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "component.body.automaton.transition.guards";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGuardIsBoolean() {
    checkValid(MP, PACKAGE + "." + "GuardIsBoolean");
  }
  
  @Test
  public void testGuardNotBoolean() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "GuardNotBoolean");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(3, "xMA036"));
  }
  
}
