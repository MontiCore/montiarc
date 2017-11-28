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


  @Test
  public void testReactionsWithAlternatives() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.AutomatonReactionWithAlternatives");
    //Both errors have the same meaning?
    checkInvalid(node, new ExpectedErrorInfo(2, "xMA020", "xMA062"));
  }

  @Test
  public void testForbiddenExprInstanceOf() {
    // failed because exression type resolver tried to resolve storage as JavaTypeSymbol but its VariableSymbol
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.InstanceOfAndObjectInstantiation");
    // 1 Error because the models contains an ASTInstanceOfExpression
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA023"));
  }

  @Test
  public void testMultipleAssignmentsSameIdentifier() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.MultipleOutputsSamePort");
    // {v=2, y=1, v=3, o = 3, o = 4, x = 1, x = 5} => 3: for v,for x, for o
    checkInvalid(node, new ExpectedErrorInfo(3, "xMA019"));
  }

  @Test
  public void testAutomatonHasNoInitialStates() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.NoInitialState");
    // automaton has states but no initial state -> exactly 1 error.
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA013"));
  }

  @Test
  public void testAutomatonHasCorrectAssignments(){
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.AutomatonWithWrongAssignments");
    checkInvalid(node, new ExpectedErrorInfo(4, "xMA017", "xMA016"));
  }

  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testOutputContainsWrongType() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.OutputContainsWrongType");
    checkInvalid(node, new ExpectedErrorInfo(1, "xMA042"));
  }

  @Test
  public void testValueListOutput() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.OutputValueList");
    checkInvalid(node, new ExpectedErrorInfo(1,  "xMA064"));
  }
}