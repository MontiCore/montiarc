package de.monticore.automata.ioautomatajava.coco;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.automaton.ioautomaton._ast.ASTIOAutomatonNode;
import de.se_rwth.commons.logging.Log;

public class RefIntegrityTest extends AbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInitialStateNotDefined() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.InitialStateNotDefined");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA210"));
  }
  
  @Test
  public void testUseOfUndeclaredField() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.UseOfUndeclaredField");
    // 2 Errors because we use 2 undeclared fields
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA230"));
  }
  
  @Test
  public void testUseOfUndefinedState() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.UseOfUndefinedStates");
    checkInvalid(node, new ExpectedErrorInfo(6, "xAA220", "xAA221"));
  }
  
  @Test
  public void testAmbiguousMatching() {
    ASTIOAutomatonNode node = getAstNode("src/test/resources/", "invalid.AmbiguousMatching");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAA200")); // 
  }
}
