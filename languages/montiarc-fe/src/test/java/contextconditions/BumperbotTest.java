package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc.cocos.MontiArcCoCos;

public class BumperbotTest extends AbstractCoCoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBumperbot() {
    // bumperbot must be valid
    checkValid("", "contextconditions.valid.BumpControl");
  }
  
  @Test
  public void testBumperbotSpeed() {
    // bumperbot must be valid
    checkValid("", "contextconditions.valid.BumpSpeed");
  }
  
  @Test
  public void testNavi() {
    
    checkInvalid(MontiArcCoCos.createChecker(),getAstNode("", "contextconditions.invalid.Navi"), new ExpectedErrorInfo(1, "xMA064"));
    
  }
}
