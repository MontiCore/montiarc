package component.body.automaton.transition.assignments;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related to automaton assignments
 *
 * @author Andreas Wortmann
 */
public class AssignmentTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.automaton.transition.assignments";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAssignmentWithAlternatives() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AssignmentWithAlternatives");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA020"));
  }
  
  @Test
  public void testWrongAssignments() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "WrongAssignments");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(4, "xMA017", "xMA016"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testAssignmentTypeConflict() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AssignmentTypeConflict");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Test
  public void testValueListAssignment() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ValueListAssignment");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA064"));
  }
  
  @Test
  public void testMultipleAssignmentsToSamePort() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "MultipleAssignmentsToSamePort");
    // {v=2, y=1, v=3, o = 3, o = 4, x = 1, x = 5} => 3: for v,for x, for o
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(3, "xMA019"));
  }
  
  @Test
  public void testAmbiguousMatching() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AmbiguousMatching");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(2, "xMA024"));
  }
  
  @Test
  public void testAssigningUndefinedVariables() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AssigningUndefinedVariables");
    // 2 Errors because we use 2 undeclared fields
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(2, "xMA023"));
  }
  
  @Test
  public void testValidAssignmentMatching() {
    checkValid(MP, PACKAGE + "." + "ValidAssignmentMatching");
  }
  
  /**
   * Tests the checking of compatible variables in CoCo AutomatonReactionTypeDoesNotFitOutputType
   */
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "IncompatibleVariableAssignment");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDifferSimple() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "IncompatibleVariableAssignmentGenericTypesDifferSimple");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
}
