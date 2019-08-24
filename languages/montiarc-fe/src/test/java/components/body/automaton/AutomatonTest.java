/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
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
    final String modelName = PACKAGE + "." + "MultipleAutomata";
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new MultipleBehaviorImplementation());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2, "xMA050");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void testImplementationInNonAtomicComponent() {
    final String modelName = PACKAGE + "." + "AutomatonInComposedComponent";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA051");
    checkInvalid(checker, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void testLowerCaseAutomatonName() {
    final String modelName = PACKAGE + "." + "AutomatonWithLowerCaseName";
    final MontiArcCoCoChecker cocos = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA015");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
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

  @Test
  public void testAutomatonWithGenerics() {
    checkValid(PACKAGE + "." + "AutomatonWithGenerics");
  }
  
}
