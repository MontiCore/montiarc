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

  /*
    TODO:
    Are initialization values for variables allowed? From the grammar it appears that this is not the case.
    The original test did also check whether inport/output default values were typed correctly. There should not be any default values for ports.

    Conclusion: This test should be obsolete, as the checked values are no longer allowed.
   */
  @Ignore
  @Test
  public void testInitialValueFit() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.InitialValueFit");
    // input int a=5, b="Wrong";
    // variable int c="Wrong";
    // output int d="Wrong", e=5; 
    checkInvalid(node, new ExpectedErrorInfo(3, "xAA420", "xAA421", "xAA422"));
  }
  
}
