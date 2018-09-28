package components.body.automaton.transition.guards;

import montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.UseOfUndeclaredField;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.AutomatonUsesCorrectPortDirection;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related to automaton guards
 *
 * @author Andreas Wortmann
 */
public class GuardTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.automaton.transition.guards";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGuardIsBoolean() {
    checkValid(PACKAGE + "." + "GuardIsBoolean");
  }
  
  @Test
  /*
   * Tests
   * [Wor16] AT1: Guard expressions evaluate to a Boolean truth value.
   *  (p.105, Lst. 5.23)
   */
  public void testGuardNotBoolean() {
    final String qualifiedModelName = PACKAGE + "." + "GuardNotBoolean";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(3, "xMA036");
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testGuardNotBooleanValid() {
    checkValid(PACKAGE + "." + "GuardHasComplexExpressionWithCD");
  }
  
  @Test
  public void testGuardWithInstanceOfExpression() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "GuardWithInstanceOfExpression");
    // 1 Error because the models contains an ASTInstanceOfExpression
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA023"));
  }
  
  @Test
  /* @implements [Wor16] AR2: Inputs, outputs, and variables are used correctly.
   * (p.103, Lst 5.20) */
  public void testGuardUsesOutgoingPort() {
    final String qualifiedModelName = PACKAGE + "." + "GuardUsesOutgoingPort";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(8, "xMA022", "xMA102", "xMA103");
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testGuardUsesUndeclaredField() {
    ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "GuardUsesUndeclaredField");
    final MontiArcCoCoChecker checker
        = new MontiArcCoCoChecker().addCoCo(
            (MontiArcASTGuardExpressionCoCo) new UseOfUndeclaredField());
    checkInvalid(checker, node, new ExpectedErrorInfo(3, "xMA079"));
  }
  
  @Test
  public void testComplexExpressionInGuard() {
    checkValid(PACKAGE + "." + "GuardHasComplexExpressionWithCD");
  }

  @Test
  public void testMultipleGuardTypeConflics() {
    final String modelName = PACKAGE + "." + "MultipleGuardTypeConflicts";
    ASTMontiArcNode node = loadComponentAST(modelName);
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1,"xMA037");
    final MontiArcCoCoChecker cocos = MontiArcCoCos.createChecker();
    checkInvalid(cocos, node, errors);
  }
}
