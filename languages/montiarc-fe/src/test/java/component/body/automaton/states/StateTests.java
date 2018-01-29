package component.body.automaton.states;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.AutomatonStateDefinedMultipleTimes;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related to automaton states
 *
 * @author Andreas Wortmann
 */
public class StateTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.automaton.states";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoubleDefinitionOfSameInitial() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "DoubleDefinitionOfSameInitial");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA029"));
  }
  
  @Test
  public void testStateDefinedMultipleTimes() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "StateDefinedMultipleTimes");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new AutomatonStateDefinedMultipleTimes());
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA031"));
  }
  
  @Test
  public void testConflictingStereotypes() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ConflictingStereotypes");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(4, "xMA031", "xMA034"));
  }
  
  @Test
  public void testUndefinedInitialState() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "UndefinedInitialState");
    checkInvalid(MontiArcCoCos.createChecker(), node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA025"));
  }
  
}
