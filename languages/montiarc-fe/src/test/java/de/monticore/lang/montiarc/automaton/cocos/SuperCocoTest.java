package de.monticore.lang.montiarc.automaton.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.se_rwth.commons.logging.Log;

/**
 * Tests some cocos from IOAutomaton and MontiArc to ensure that they are also
 * working in MontiArcAutomaton.
 * 
 * @author Gerrit Leonhardt
 */
public class SuperCocoTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testIOAutomatonCocos() {
    // test if IO-Automaton cocos are working by testing the
    // AutomatonWithoutState coco
    ASTMontiArcNode node = getAstNode("src/test/resources/", "automaton.invalid.AutomatonWithoutState");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA130"));
  }
  
  @Test
  public void testMontiArcCocos() {
    // test if MontiArc cocos are working by testing the UniquePorts coco
    ASTMontiArcNode node = getAstNode("src/test/resources/", "automaton.invalid.UniquePorts");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAC002"));
  }
  
}
