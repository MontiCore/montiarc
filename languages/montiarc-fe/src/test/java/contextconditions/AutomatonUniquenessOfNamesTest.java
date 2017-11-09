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

  private final String MODEL_PATH = super.MODEL_PATH + "contextconditions/";

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

  @Test
  public void testVariableConflictIO() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "invalid.NameConflictVariableAndIO");
    checkInvalid(node, new ExpectedErrorInfo(6, "xMA035", "xMA053", "xMA063", "xMA065"));
  }
  
  @Test
  public void testVariableMoreThanOnceConflict() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "invalid.VariableMoreThanOnceConflict");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA035"));
  }
}
