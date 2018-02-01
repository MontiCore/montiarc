package components.body.automaton.states;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
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
  
  private static final String PACKAGE = "components.body.automaton.states";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoubleDefinitionOfSameInitial() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "DoubleDefinitionOfSameInitial");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(2, "xMA029"));
  }
  
  @Test
  public void testStateDefinedMultipleTimes() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "StateDefinedMultipleTimes");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new AutomatonStateDefinedMultipleTimes());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA031"));
  }
  
  @Test
  public void testConflictingStereotypes() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ConflictingStereotypes");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(4, "xMA031", "xMA034"));
  }
  
  @Test
  public void testUndefinedInitialState() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "UndefinedInitialState");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA025"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testInvalidInitialAssignment() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "InvalidInitialAssignment");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(2, "xMA039"));
  }
  
}
