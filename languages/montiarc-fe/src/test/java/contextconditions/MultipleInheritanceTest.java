package contextconditions;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._parser.MontiArcParser;

/**
 * 
 * Tests whether multiple component inheritance is prohibited
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class MultipleInheritanceTest extends AbstractCoCoTest {
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMutipleInheritance() {
    MontiArcParser parser = new MontiArcParser();
    try {
      parser.parse("contextconditions.invalid.MultipleInheritance");
    }
    catch (Exception e) {
        return;
    }
    fail("Component contextconditions.invalid.MultipleInheritance should not be parseable.");
  }

}