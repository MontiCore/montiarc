package de.montiarcautomaton.automatongenerator;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.se_rwth.commons.logging.Log;

public class UsecaseDependentTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testNondeterminism() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "invalid.Nondeterminism");
    checkInvalid(node, new ExpectedErrorInfo(3, "xAC100", "xAC110", "xAA440"));
  }

  @Test
  public void testValuelists() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "invalid.UseOfValuelists");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAC120"));
  }
}
