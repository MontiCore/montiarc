package contextconditions;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;

public class BumperbotTest extends AutomatonAbstractCocoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBumperbot() {
    // bumperbot must be valid
    checkValid(MODEL_PATH, "contextconditions.valid.BumpControl");
  }
  
  @Test
  public void testBumperbotSpeed() {
    // bumperbot must be valid
    checkValid(MODEL_PATH, "contextconditions.valid.BumpSpeed");
  }
  
  @Test
  public void testNavi() {
    
    checkInvalid(getAstNode("src/test/resources", "contextconditions.invalid.Navi"), new ExpectedErrorInfo(1, "xMA064"));
    
  }
}
