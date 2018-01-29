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
  public void testVariableConflictIO() {
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.NameConflictVariableAndIO");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(6,  "xMA035", "xMA053"));
  }
  
  @Test
  public void testVariableMoreThanOnceConflict() {
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.VariableMoreThanOnceConflict");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA035"));
  }
}
