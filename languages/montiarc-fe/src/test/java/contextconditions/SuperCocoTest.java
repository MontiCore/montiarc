package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;

/**
 * Tests some cocos from IOAutomaton and MontiArc to ensure that they are also
 * working in MontiArcAutomaton.
 * 
 * @author Gerrit Leonhardt
 */
public class SuperCocoTest extends AutomatonAbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testIOAutomatonCocos() {
    // test if IO-Automaton cocos are working by testing the
    // AutomatonWithoutState coco
    ASTMontiArcNode node = getAstNode("src/test/resources/", "contextconditions.invalid.AutomatonWithoutState");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA014"));
  }
  
  @Test
  public void testMontiArcCocos() {
    // test if MontiArc cocos are working by testing the UniquePorts coco
    ASTMontiArcNode node = getAstNode("src/test/resources/", "contextconditions.invalid.UniquePorts");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA053"));
  }
  
}
