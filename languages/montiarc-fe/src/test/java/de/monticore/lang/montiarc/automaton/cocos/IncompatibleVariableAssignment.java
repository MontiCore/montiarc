package de.monticore.lang.montiarc.automaton.cocos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import contextconditions.AutomatonAbstractCocoTest;
import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.se_rwth.commons.logging.Log;

public class IncompatibleVariableAssignment extends AutomatonAbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,
        "automaton.invalid.IncompatibleVariableAssignment");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA431"));
  }
  
  @Ignore("@JP, BS: Bitte angucken, warum das (anscheinend  in der JavaDSL) nicht funktioniert.")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,
        "automaton.invalid.IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA431"));
  }
  
}
