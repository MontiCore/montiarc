package contextconditions;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;

public class IncompatibleVariableAssignment extends AutomatonAbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,
        "contextconditions.invalid.IncompatibleVariableAssignment");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA431"));
  }
  
  @Ignore("@JP, BS: Bitte angucken, warum das (anscheinend  in der JavaDSL) nicht funktioniert.")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,
        "contextconditions.invalid.IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAA431"));
  }
  
}
