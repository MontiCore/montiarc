package de.monticore.lang.montiarc.automaton.cocos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.se_rwth.commons.logging.Log;

public class ConventionsTest extends AbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  @Ignore //XXX: Invalid as both error cases are prevented by the new grammar
  public void testInitialStateNotDefined() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "automaton.invalid.AutomatonHasInOutputsVariables");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAB110", "xAB120"));
  }

  @Test
  public void testAutomatonBehaviorImplementation() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "automaton.invalid.InvalidAutomatonBehaviorImpl");
    checkInvalid(node, new ExpectedErrorInfo(3, "xAB140", "xAB130"));
  }
  
  @Test
  public void testInvalidCDExplicit() {
    Log.getFindings().clear();
    // loads the CD explicitly and checks its cocos
    Scope symTab = createSymTab("src/test/resources/");
    symTab.<CDFieldSymbol> resolve("automaton.invalid.InvalidDatatypes.invalidType.A", CDFieldSymbol.KIND).orElse(null);
    new ExpectedErrorInfo(1, "xC4A05").checkFindings(Log.getFindings());
  }
  
  @Test
  public void testImplementationInNonAtomicComponent() {
    checkInvalid(getAstNode("src/test/resources", "automaton.invalid.ImplementationInNonAtomicComponent"), new ExpectedErrorInfo(1, "xAB141"));
  }
  
  @Ignore
  @Test
  public void testInvalidCDImplicit() {
    // TODO symbols are only loaded if they are resolved. Therefore it is not
    // easily possible to perform CoCo checks to detect errors in CDs that are
    // imported in the given MAA model.
    ASTMontiArcNode node = getAstNode("src/test/resources/", "automaton.invalid.InvalidCD");
    checkInvalid(node, new ExpectedErrorInfo(1, "xC4A05"));
  }
}
