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
  public void testGuardNotBoolean() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "GuardNotBoolean");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(3, "xMA036"));
  }
  
  @Test
  public void testGuardWithInstanceOfExpression() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "GuardWithInstanceOfExpression");
    // 1 Error because the models contains an ASTInstanceOfExpression
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA023"));
  }
  
  @Ignore("Siehe TODO in AutomatonOutputInExpression coco")
  @Test
  public void testGuardUsesOutgoingPort() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "GuardUsesOutgoingPort");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(4, "xMA022"));
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
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testMultipleGuardTypeConflics() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "MultipleGuardTypeConflics");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(2, "xMA046"));
  }
  
  
}
