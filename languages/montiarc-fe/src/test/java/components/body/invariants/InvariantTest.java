package components.body.invariants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.IdentifiersAreUnique;

/**
 * This class checks all context conditions directly related to invariant definitions
 *
 * @author Andreas Wortmann
 */
public class InvariantTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.invariants";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAmbiguousInvariantNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousInvariantNames");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()),
        node,
        new ExpectedErrorInfo(4, "xMA052"));
  }
  
}
