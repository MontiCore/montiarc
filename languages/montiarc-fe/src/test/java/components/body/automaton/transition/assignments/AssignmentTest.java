package components.body.automaton.transition.assignments;

import montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;

/**
 * This class checks all context conditions related to automaton assignments
 *
 * @author Andreas Wortmann
 */
public class AssignmentTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.automaton.transition.assignments";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMethodCallAfterCallKeyword() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "MethodCallAfterCallKeyword");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IOAssignmentCallFollowsMethodCall()), node, new ExpectedErrorInfo(1, "xMA091"));

  }
  
  @Test
  public void testMethodCallWithoutCallKeyword() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "MethodCallWithoutCallKeyword");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IOAssignmentCallFollowsMethodCall()), node, new ExpectedErrorInfo(1, "xMA090"));
  }
  
  @Test
  public void testAssignmentWithAlternatives() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AssignmentWithAlternatives");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA020"));
  }

  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testAssignmentTypeConflict() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AssignmentTypeConflict");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Test
  public void testValueListAssignment() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ValueListAssignment");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA081"));
  }
  
  @Test
  public void testMultipleAssignmentsToSamePort() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "MultipleAssignmentsToSamePort");
    // {v=2, y=1, v=3, o = 3, o = 4, x = 1, x = 5} => 3: for v,for x, for o
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(3, "xMA019"));
  }
  
  @Ignore("see #171")
  @Test
  public void testAmbiguousMatching() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousMatching");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(2, "xMA024"));
  }
  
  @Test
  public void testAssigningUndefinedVariables() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AssigningUndefinedVariables");
    // 2 Errors because we use 2 undeclared fields
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(2, "xMA079"));
  }
  
  @Test
  public void testValidAssignmentMatching() {
    checkValid(PACKAGE + "." + "ValidAssignmentMatching");
  }
  
  /**
   * Tests the checking of compatible variables in CoCo
   * AutomatonReactionTypeDoesNotFitOutputType
   */
  @Test
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "IncompatibleVariableAssignment");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDifferSimple() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "IncompatibleVariableAssignmentGenericTypesDifferSimple");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(2, "xMA042","xMA042"));
  }
  
  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Test
  /* @implements [Wor16] AR2: Inputs, outputs, and variables are used correctly.
   * (p.103, Lst 5.20) */
  public void testAssignmentToIncomingPort() {
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(
        PACKAGE + "." + "AssignmentToIncomingPort");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new AutomatonNoAssignmentToIncomingPort());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA034");
    checkInvalid(cocos, astMontiArcNode, errors);
  }
  
  @Test
  public void testUseOfForbiddenExpression() {
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(
        PACKAGE + "." + "UseOfForbiddenExpressions");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker();
    cocos.addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfForbiddenExpression());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA023");
    checkInvalid(cocos, astMontiArcNode, errors);
  }

  @Test
  public void testAssignmentOfSequences() {
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(PACKAGE + "." + "AssignmentOfSequence");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new UseOfValueLists());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA081");
    checkInvalid(cocos, astMontiArcNode,errors);
  }

  @Test
  public void testMultipleMessagesPerCycle() {
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(PACKAGE + "." + "OneAssignmentPerCycle");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new UseOfValueLists())
        .addCoCo(new MultipleAssignmentsSameIdentifier());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA081", "xMA019");
    checkInvalid(cocos, astMontiArcNode,errors);
  }


}
