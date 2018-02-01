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
public class InvariantTests extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.invariants";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInexistingPortType() {
    // TODO: Star imports?
    ASTMontiArcNode node = getAstNode(PACKAGE + "." + "AmbiguousInvariantNames");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()),
        node,
        new ExpectedErrorInfo(2, "xMA052"));
  }
  
  @Ignore("TODO ocl invariants?")
  @Test
  public void testAdaptOCLFieldToPort() {
    Scope symTab = this.loadDefaultSymbolTable();
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "OCLFieldToPort", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
  }
  
  @Ignore("TODO ocl invariants?")
  @Test
  public void testAdaptOCLFieldToArcdField() {
    Scope symTab = this.loadDefaultSymbolTable();
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "OCLFieldToArcField", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
  }
  
}
