package components.body.automaton;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.MultipleBehaviorImplementation;

/**
 * This class checks all context conditions directly related to automata (not their sub-elements)
 *
 * @author Andreas Wortmann
 */
public class AutomatonTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.automaton";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMutipleBehaviors() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "MultipleAutomata");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new MultipleBehaviorImplementation()), node, new ExpectedErrorInfo(2, "xMA050"));
  }
  
  @Test
  public void testImplementationInNonAtomicComponent() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AutomatonInComposedComponent");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA051"));
  }
  
  @Test
  public void testLowerCaseAutomatonName() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AutomatonWithLowerCaseName");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA015"));
  }
  
  @Test
  public void testAutomatonHasStates() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AutomatonWithoutState");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA014"));
  }
  
  @Test
  public void testValidAutomaton() {
    checkValid(PACKAGE + "." + "ValidAutomaton");
  }
  
}
