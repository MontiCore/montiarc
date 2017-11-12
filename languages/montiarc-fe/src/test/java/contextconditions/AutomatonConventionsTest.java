package contextconditions;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;

public class AutomatonConventionsTest extends AutomatonAbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMutipleBehaviors() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.MultipleBehaviors");
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA050"));
  }

  @Test
  public void testLowerCaseAutomatonName() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.LowerCaseAutomaton");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA049"));
  }

  @Test
  public void testImplementationInNonAtomicComponent() {
    checkInvalid(getAstNode("src/test/resources", "contextconditions.invalid.ImplementationInNonAtomicComponent"), new ExpectedErrorInfo(1, "xMA051"));
  }
  
  @Test
  public void testInvalidImports() {
    //TODO: Star imports?
    ASTMontiArcNode node = getAstNode(MODEL_PATH,"contextconditions.invalid.InvalidCD");
    checkInvalid(node, new ExpectedErrorInfo(1,"xMA076"));

    checkValid(MODEL_PATH,"contextconditions.valid.BumpControl");
  }

  @Test
  public void testAutomatonHasStates() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.AutomatonWithoutState");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA014"));
  }

  @Ignore
  @Test
  //Todo: CoCo is not working. Needs to check if output ports are only used as the left side of an assignment.
  public void testOutputInExpression() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.AutomatonOutputInExpression");
    checkInvalid(node, new ExpectedErrorInfo(4, "xMA022"));
  }

  @Test
  public void testReactionsWithAlternatives() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.AutomatonReactionWithAlternatives");
    //Both errors have the same meaning?
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA020", "xMA062"));
  }


}