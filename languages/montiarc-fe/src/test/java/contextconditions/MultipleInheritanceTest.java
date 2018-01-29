package contextconditions;

import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._parser.MontiArcParser;
import montiarc.cocos.MontiArcCoCos;

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
    Optional<ASTMACompilationUnit> maModel = Optional.empty();
    MontiArcParser parser = new MontiArcParser();
    try {
      maModel = parser.parse("contextconditions.invalid.MultipleInheritance");
    }
    catch (Exception e) {
        return;
    }
    fail("Component contextconditions.invalid.MultipleInheritance should not be parseable.");
  }

}