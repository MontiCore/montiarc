package de.monticore.lang.montiarc.automaton.cocos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.se_rwth.commons.logging.Log;

public class IncompatibleVariable extends AutomatonAbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = getAstNode("src/test/resources/",
        "automaton.invalid.IncompatibleVariableAssignment");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA431"));
  }
  
  @Ignore
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = getAstNode("src/test/resources/",
        "automaton.invalid.IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA431"));
  }
  
}
