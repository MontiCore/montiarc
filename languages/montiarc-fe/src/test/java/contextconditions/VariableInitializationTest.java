package contextconditions;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.IdentifiersAreUnique;
import montiarc.cocos.InitBlockOnlyOnEmbeddedAJava;
import org.junit.BeforeClass;
import org.junit.Test;

public class VariableInitializationTest extends AbstractCoCoTest{

  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testValid() {
    checkValid("contextconditions", "valid.InitBlockWithAJava");
  }

  @Test
  public void testInitBlockWithoutBehaviour(){
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InitBlockOnlyOnEmbeddedAJava()),
        getAstNode("contextconditions", "invalid.InitBlockWithoutAJava"),
        new ExpectedErrorInfo(1, "xMA063"));
  }
}
