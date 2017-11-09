package contextconditions;

import com.sun.org.apache.xpath.internal.operations.Mod;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.AutomatonInitialDeclaredMultipleTimes;
import montiarc.cocos.AutomatonStateDefinedMultipleTimes;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.jws.WebParam;

public class AutomatonUniquenessOfNamesTest extends AutomatonAbstractCocoTest {

  private final String MODEL_PATH = "src/test/resources/contextconditions/";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDoubleDefinitionOfInitials() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "invalid.DoubleDefinitionOfSameInitial");
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA029"));
  }

  @Test
  public void testStateDefinedMultipleTimes() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "invalid.StateDefinedMultipleTimes");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new AutomatonStateDefinedMultipleTimes());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA031"));
  }
  
  @Test
  public void testStateDefinedMultipleTimesStereotypesDontMatch() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "invalid.StateDefinedMultipleTimesStereotypesDontMatch");
    checkInvalid(node, new ExpectedErrorInfo(4, "xMA031", "xMA034"));
  }

  //TODO: Was mit input/output machen? Wenn als Port abgebildet in MAA, dann sollte das durch andere CoCos, die sich um Ports kümmern, abgedeckt sein.
  @Ignore
  @Test
  public void testVariableConflictIO() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "invalid.NameConflictVariableAndIO");
    checkInvalid(node, new ExpectedErrorInfo(5, "xAA310", "xAA320", "xAA350", "xAA351", "xMA035"));
  }
  
  @Test
  public void testVariableMoreThanOnceConflict() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "invalid.VariableMoreThanOnceConflict");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA035"));
  }
}
