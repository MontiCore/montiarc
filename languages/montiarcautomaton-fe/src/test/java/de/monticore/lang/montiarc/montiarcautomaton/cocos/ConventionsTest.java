package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.se_rwth.commons.logging.Log;

public class ConventionsTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInitialStateNotDefined() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "invalid.AutomatonHasInOutputsVariables");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAB110", "xAB120"));
  }

  @Test
  public void testAutomatonBehaviorImplementation() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "invalid.InvalidAutomatonBehaviorImpl");
    checkInvalid(node, new ExpectedErrorInfo(3, "xAB140", "xAB130"));
  }
  
  
}
