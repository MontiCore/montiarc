package component.body.automaton;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.AutomatonSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions directly related to automata (not their sub-elements)
 *
 * @author Andreas Wortmann
 */
public class AutomatonTest extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "component.body.automaton";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMutipleBehaviors() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "MultipleAutomata");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(2, "xMA050"));
  }
  
  @Test
  public void testImplementationInNonAtomicComponent() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AutomatonInComposedComponent");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA051"));
  }

  @Test
  public void testLowerCaseAutomatonName() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AutomatonWithLowerCaseName");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA015"));
  }

  @Test
  public void testAutomatonHasStates() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AutomatonWithoutState");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(1, "xMA014"));
  }

  @Test
  public void testAutomatonHasNoInitialStates() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AutomatonWithoutInitialState");
    // automaton has states but no initial state -> exactly 1 error.
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(1, "xMA013"));
  }
  
  @Test
  public void testValidAutomaton() {
    checkValid(MP, PACKAGE + "." + "ValidAutomaton");
  }
  
  @Test
  public void testResolveBumpControlBehavior() {
    MontiArcTool tool = new MontiArcTool();
    String modelPath = "src/test/resources";
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "BumpControl");
    assertNotNull(node);
    
    Scope symtab = tool.createSymbolTable(modelPath);
    Optional<ComponentSymbol> oBControl = symtab
        .<ComponentSymbol> resolve(PACKAGE + "." + "BumpControl", ComponentSymbol.KIND);
    assertTrue(oBControl.isPresent());
    
    ComponentSymbol bControl = oBControl.get();
    Optional<AutomatonSymbol>  autSymbol = bControl.getSpannedScope().<AutomatonSymbol>
            resolve("BumpControl", AutomatonSymbol.KIND);
    assertTrue(autSymbol.isPresent());
  }

}