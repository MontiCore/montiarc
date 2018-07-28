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
public class StateTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.automaton.states";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoubleDefinitionOfSameInitial() {
    ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "DoubleDefinitionOfSameInitial");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2, "xMA029");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
  @Test
  public void testStateDefinedMultipleTimes() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "StateDefinedMultipleTimes");
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new AutomatonStateDefinedMultipleTimes());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA031");
    checkInvalid(cocos, node, errors);
  }
  
  @Test
  public void testConflictingStereotypes() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "ConflictingStereotypes");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA031");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
  @Test
  /*
   * Tests CoCo [Wor16] AC4: The automaton has at least one initial state.
   * (p. 99, Lst. 5.14)
   */
  public void testHasInitialState() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "AutomatonHasNoInitialState");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA013");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
  @Test
  public void testUndefinedInitialState() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "UndefinedInitialState");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA025");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testInvalidInitialAssignment() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "InvalidInitialAssignment");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2, "xMA039");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
  @Test
  public void testAutomatonStateNameUppercase() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "StateNameUppercase");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA021");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
}
