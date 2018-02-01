package components.body.invariants;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.IdentifiersAreUnique;

/**
 * This class checks all context conditions directly related to port definitions
 *
 * @author Andreas Wortmann
 */
public class InvariantTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "components.body.invariants";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  

  @Test
  public void testInexistingPortType() {
    //TODO: Star imports?
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AmbiguousInvariantNames");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()),
        node,
        new ExpectedErrorInfo(2, "xMA052"));
  }
    
}