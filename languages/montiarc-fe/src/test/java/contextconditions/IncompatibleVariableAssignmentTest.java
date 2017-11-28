package contextconditions;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;

public class IncompatibleVariableAssignmentTest extends AutomatonAbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  /**
   * Tests the checking of compatible variables in CoCo
   * AutomatonReactionTypeDoesNotFitOutputType
   */
  @Test
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,
        "contextconditions.invalid.IncompatibleVariableAssignment");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDifferSimple() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,
        "contextconditions.invalid.IncompatibleVariableAssignmentGenericTypesDifferSimple");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH,
        "contextconditions.invalid.IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA042"));
  }
  
}
