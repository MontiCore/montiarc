package contextconditions;

import montiarc._ast.ASTMontiArcNode;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

import de.se_rwth.commons.logging.Log;

public class RefIntegrityTest extends AutomatonAbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInitialStateNotDefined() {
	 ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.InitialStateNotDefined");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA025")); 
  }
  
  
  @Test
  public void testUseOfUndeclaredField() {
	 ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.UseOfUndeclaredField");
    // 2 Errors because we use 2 undeclared fields

    checkInvalid(node, new ExpectedErrorInfo(2, "xMA023")); 
  }
  
  @Test
  public void testUseOfUndefinedState() {
	 ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.UseOfUndefinedStates");
    checkInvalid(node, new ExpectedErrorInfo(6, "xMA026", "xMA027")); 
  }
  
  @Ignore //stimulis are not being read
  @Test
  public void testAmbiguousMatching() {
	  ASTMontiArcNode node = getAstNode(MODEL_PATH + "contextconditions/", "invalid.AmbiguousMatching");
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA024")); 
  }
}
