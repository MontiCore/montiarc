package contextconditions;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.AtMostOneInitBlock;
import org.junit.BeforeClass;
import org.junit.Test;

public class AJavaAtMostOneInitBlockTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp(){
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testAtMostOneInitBlock(){
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new AtMostOneInitBlock());
    ASTMontiArcNode node = getAstNode("contextconditions", "invalid.MoreThanOneInitBlock");
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA078"));
  }
}
