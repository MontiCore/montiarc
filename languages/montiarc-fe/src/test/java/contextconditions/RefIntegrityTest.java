package contextconditions;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

public class RefIntegrityTest extends AbstractCoCoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInitialStateNotDefined() {
	 ASTMontiArcNode node = getAstNode("contextconditions", "invalid.InitialStateNotDefined");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA025")); 
  }
  
  
  @Test
  public void testUseOfUndeclaredField() {
	 ASTMontiArcNode node = getAstNode("contextconditions", "invalid.UseOfUndeclaredField");
    // 2 Errors because we use 2 undeclared fields
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(2, "xMA023")); 
  }
  
  @Test
  public void testUseOfUndefinedState() {
	 ASTMontiArcNode node = getAstNode("contextconditions", "invalid.UseOfUndefinedStates");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(6, "xMA026", "xMA027")); 
  }
  
  @Test
  public void testAmbiguousMatching() {
	  ASTMontiArcNode node = getAstNode("contextconditions", "invalid.AmbiguousMatching");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(2, "xMA024")); 
  }
}
