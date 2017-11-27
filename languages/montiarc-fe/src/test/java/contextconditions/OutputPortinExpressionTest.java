package contextconditions;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class OutputPortinExpressionTest extends AutomatonAbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testAutomatonOutputInExpression() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.AutomatonOutputInExpression");
    checkInvalid(node, new ExpectedErrorInfo(4, "xMA022"));
  }

  @Test
  public void testAJavaOutputInExpression() {
    checkValid(MODEL_PATH, "contextconditions.valid.AJavaOutputInExpression");
  }
}