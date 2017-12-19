package contextconditions;

import com.sun.org.apache.xpath.internal.operations.Mod;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.AutomatonInitialDeclaredMultipleTimes;
import montiarc.cocos.AutomatonStateDefinedMultipleTimes;
import montiarc.cocos.MontiArcCoCos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.jws.WebParam;

public class AutomatonUniquenessOfNamesTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDoubleDefinitionOfInitials() {
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.DoubleDefinitionOfSameInitial");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(2, "xMA029"));
  }

  @Test
  public void testStateDefinedMultipleTimes() {
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.StateDefinedMultipleTimes");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new AutomatonStateDefinedMultipleTimes());
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(2, "xMA031"));
  }
  
  @Test
  public void testStateDefinedMultipleTimesStereotypesDontMatch() {
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.StateDefinedMultipleTimesStereotypesDontMatch");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(4, "xMA031", "xMA034"));
  }

  @Test
  public void testVariableConflictIO() {
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.NameConflictVariableAndIO");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(6,  "xMA035", "xMA053"));
  }
  
  @Test
  public void testVariableMoreThanOnceConflict() {
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.VariableMoreThanOnceConflict");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(1, "xMA035"));
  }
}
