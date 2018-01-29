package component.body.automaton.transition.assignments;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related to assignments
 *
 * @author Andreas Wortmann
 */
public class AssignmentTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "component.body.automaton.transition.assignments";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }


  @Test
  public void testAssignmentWithAlternatives() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AssignmentWithAlternatives");
    // Both errors have the same meaning?
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA020", "xMA062"));
  }

  @Test
  public void testForbiddenExprInstanceOf() {
    // failed because exression type resolver tried to resolve storage as JavaTypeSymbol but its VariableSymbol
    ASTMontiArcNode node = getAstNode(MP, "contextconditions.invalid.InstanceOfAndObjectInstantiation");
    // 1 Error because the models contains an ASTInstanceOfExpression
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA023"));
  }
  

  @Test
  public void testAutomatonHasCorrectAssignments(){
    ASTMontiArcNode node = getAstNode(MP, "contextconditions.invalid.AutomatonWithWrongAssignments");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(4, "xMA017", "xMA016"));
  }

  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testOutputContainsWrongType() {
    ASTMontiArcNode node = getAstNode(MP, "contextconditions.invalid.OutputContainsWrongType");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA042"));
  }

  @Test
  public void testValueListOutput() {
    ASTMontiArcNode node = getAstNode(MP, "contextconditions.invalid.OutputValueList");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(1,  "xMA064"));
  }
}