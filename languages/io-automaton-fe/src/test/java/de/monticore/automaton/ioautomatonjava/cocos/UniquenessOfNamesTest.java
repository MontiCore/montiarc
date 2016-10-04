package de.monticore.automaton.ioautomatonjava.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.automaton.ioautomaton._ast.ASTIOAutomatonNode;
import de.se_rwth.commons.logging.Log;

public class UniquenessOfNamesTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDoubleDefinitionOfInitials() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.DoubleDefinitionOfSameInitial");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA300"));
  }

  @Test
  public void testStateDefinedMultipleTimes() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.StateDefinedMultipleTimes");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA330"));
  }
  
  @Test
  public void testStateDefinedMultipleTimesStereotypesDontMatch() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.StateDefinedMultipleTimesStereotypesDontMatch");
    checkInvalid(node, new ExpectedErrorInfo(4, "xAA330", "xAA341"));
  }
  
  @Test
  public void testVariableConflictIO() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.NameConflictVariableAndIO");
    checkInvalid(node, new ExpectedErrorInfo(5, "xAA310", "xAA320", "xAA350", "xAA351", "xAA360"));
  }
  
  @Test
  public void testVariableMoreThanOnceConflict() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.VariableMoreThanOnceConflict");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA360"));
  }
}
