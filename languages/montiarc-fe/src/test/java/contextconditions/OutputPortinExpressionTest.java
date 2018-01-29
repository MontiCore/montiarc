package contextconditions;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class OutputPortinExpressionTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testAutomatonOutputInExpression() {
    ASTMontiArcNode node = getAstNode("", "contextconditions.invalid.AutomatonOutputInExpression");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(4, "xMA022"));
  }

  @Test
  public void testAJavaOutputInExpression() {
    checkValid("", "contextconditions.valid.AJavaOutputInExpression");
  }
}
