package contextconditions;

import montiarc._ast.ASTMontiArcNode;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

public class AutomatonTypeCorrectnessTest extends AutomatonAbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGuardNotBool() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.GuardIsNotBoolean");
    checkInvalid(node, new ExpectedErrorInfo(3, "xMA036"));
  }
  
  @Test
  public void testStimulusAndEventDontFit() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.StimulusAndEventDontFit");
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA046"));
  }
  
  @Test
  public void testInitialReactionAndActionDontFit() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.InitialReactionAndActionDontFit");
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA039"));
  }
  
  @Test
  public void testReactionAndActionFit() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.ReactionAndActionDontFit");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA042"));
  }

  @Test
  public void testInitialValueFit() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.InitialValueFit");
    checkInvalid(node, new ExpectedErrorInfo(3, "xMA038", "xMA039"));
  }
  
}
