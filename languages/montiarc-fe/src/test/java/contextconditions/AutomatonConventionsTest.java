package contextconditions;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

public class AutomatonConventionsTest extends AbstractCoCoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMutipleBehaviors() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.MultipleBehaviors");
    checkInvalid(MontiArcCoCos.createChecker(), node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA050"));
  }

  @Test
  public void testLowerCaseAutomatonName() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.LowerCaseAutomaton");
    checkInvalid(MontiArcCoCos.createChecker(), node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA049"));
  }

  @Test
  public void testImplementationInNonAtomicComponent() {
    checkInvalid(MontiArcCoCos.createChecker(), getAstNode("", "contextconditions.invalid.ImplementationInNonAtomicComponent"), new AbstractCoCoTestExpectedErrorInfo(1, "xMA051"));
  }
  
  @Test
  public void testInvalidImports() {
    //TODO: Star imports?
    ASTMontiArcNode node = getAstNode("","contextconditions.invalid.InvalidCD");
    checkInvalid(MontiArcCoCos.createChecker(), node, new AbstractCoCoTestExpectedErrorInfo(1,"xMA076"));

    checkValid("","contextconditions.valid.BumpControl");
  }

  @Test
  public void testAutomatonHasStates() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.AutomatonWithoutState");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA014"));
  }


  @Test
  public void testReactionsWithAlternatives() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.AutomatonReactionWithAlternatives");
    //Both errors have the same meaning?
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA020", "xMA062"));
  }

  @Test
  public void testForbiddenExprInstanceOf() {
    // failed because exression type resolver tried to resolve storage as JavaTypeSymbol but its VariableSymbol
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.InstanceOfAndObjectInstantiation");
    // 1 Error because the models contains an ASTInstanceOfExpression
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA023"));
  }

  @Test
  public void testMultipleAssignmentsSameIdentifier() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.MultipleOutputsSamePort");
    // {v=2, y=1, v=3, o = 3, o = 4, x = 1, x = 5} => 3: for v,for x, for o
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(3, "xMA019"));
  }

  @Test
  public void testAutomatonHasNoInitialStates() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.NoInitialState");
    // automaton has states but no initial state -> exactly 1 error.
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA013"));
  }

  @Test
  public void testAutomatonHasCorrectAssignments(){
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.AutomatonWithWrongAssignments");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(4, "xMA017", "xMA016"));
  }

  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testOutputContainsWrongType() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.OutputContainsWrongType");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA042"));
  }

  @Test
  public void testValueListOutput() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.OutputValueList");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1,  "xMA064"));
  }
}