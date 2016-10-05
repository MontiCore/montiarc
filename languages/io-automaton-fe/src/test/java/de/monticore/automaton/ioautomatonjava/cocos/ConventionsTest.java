package de.monticore.automaton.ioautomatonjava.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.automaton.ioautomaton._ast.ASTIOAutomatonNode;
import de.se_rwth.commons.logging.Log;

public class ConventionsTest extends AbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAutomatonHasNoInput() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.AutomatonWithoutEvent");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA110"));
  }
  
  @Test
  public void testAutomatonHasNoOutput() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.AutomatonWithoutAction");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA120"));
  }
  
  @Test
  public void testAutomatonHasNoStates() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.AutomatonWithoutState");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA130"));
  }
  
  @Test
  public void testUpperLowerCase() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.caseConventions");
    checkInvalid(node, new ExpectedErrorInfo(5, "xAA140", "xAA160", "xAA161", "xAA162", "xAA190"));
  }
  
  @Test
  public void testIllegalExpression() {
    
    // As class instantiation expressions are not prohibited anymore, this does
    // not yield errors
    checkValid("src/test/resources/", "invalid.IllegalExpressions");
  }
  
  @Test
  public void testOutputInExpression() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.OutputInExpression");
    checkInvalid(node, new ExpectedErrorInfo(4, "xAA1A0"));
  }
  
  @Test
  public void testReactionsWithAlternatives() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.ReactionWithAlternatives");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA180", "xAA181"));
  }
  
  /**
   * Checks if CoCo @link{UseOfForbiddenExpression} detects illegal instanceof
   * expressions.
   */
  @Test
  public void testForbiddenExprInstanceOf() {
    // failed because exression type resolver tried to resolve storage as JavaTypeSymbol but its VariableSymbol
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.InstanceOfAndObjectInstantiation");
  // 1 Error because the models contains an ASTInstanceOfExpression
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA1B0"));
  }
  
  @Test
  public void testMultipleAssignmentsSameIdentifier() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.MultipleOutputsSamePort");
    // {v=2, y=1, v=3, o = 3, o = 4, x = 1, x = 5} => 3: for v,for x, for o
    checkInvalid(node, new ExpectedErrorInfo(3, "xAA170"));
  }
  
  @Test
  public void testAutomatonHasNoInitialStates() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.NoInitialState");
    // automaton has states but no initial state -> exactly 1 error.
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA100"));
  }
  
  @Test
  public void testAutomatonHasCorrectAssignments(){
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.AutomatonWithWrongAssignments");
    checkInvalid(node, new ExpectedErrorInfo(4, "xAA150", "xAA151"));
  }
  
  @Test
  public void testValueListContainsWrongType() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.OutputValueListContainsWrongType");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA431", "xAA110"));

  }
}
