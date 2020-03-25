/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.states;

import montiarc.cocos.GenericInitValues;
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
 */
public class StateTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.body.automaton.states";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  /*
   * Tests [Wor16] AU2: Each state is declared initial at most once.
   *  (p. 97, Lst. 5.9)
   */
  public void testDoubleDefinitionOfSameInitial() {
    final String qualifiedModelName = PACKAGE + "." + "DoubleDefinitionOfSameInitial";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2, "xMA029");
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testGenericInitAssignment() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "GenericInitAssignment");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new GenericInitValues()), node,
        new ExpectedErrorInfo(1, "xMA047"));
  }

  @Test
  public void testStateDefinedMultipleTimes() {
    final String qualifiedModelName = PACKAGE + "." + "StateDefinedMultipleTimes";
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new AutomatonStateDefinedMultipleTimes());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA031");
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testConflictingStereotypes() {
    final String qualifiedModelName = PACKAGE + "." + "ConflictingStereotypes";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA031");
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
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
  /*
   * Tests [Wor16] AR3: Used states exist (p. 104. Lst. 5.21)
   */
  public void testUndefinedInitialState() {
    final String qualifiedModelName = PACKAGE + "." + "UndefinedInitialState";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA025");
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testInvalidInitialAssignment() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "InvalidInitialAssignment");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2, "xMA042");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }

  @Test
  /**
   * Tests CoCo [Wor16] AC8.
   */
  public void testAutomatonStateNameUppercase() {
    final String qualifiedModelName = PACKAGE + "." + "StateNameUppercase";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA021");
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

}
