package contextconditions;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

public class IncompatibleVariableAssignmentTest extends AbstractCoCoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  /**
   * Tests the checking of compatible variables in CoCo
   * AutomatonReactionTypeDoesNotFitOutputType
   */
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = getAstNode("",
        "contextconditions.invalid.IncompatibleVariableAssignment");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDifferSimple() {
    ASTMontiArcNode node = getAstNode("",
        "contextconditions.invalid.IncompatibleVariableAssignmentGenericTypesDifferSimple");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = getAstNode("",
        "contextconditions.invalid.IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA042"));
  }
  
}
