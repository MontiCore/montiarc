package component.body.invariants;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
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
  private static final String PACKAGE = "component.body.invariants";
  
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
        new AbstractCoCoTestExpectedErrorInfo(2, "xMA052"));
  }
    
}