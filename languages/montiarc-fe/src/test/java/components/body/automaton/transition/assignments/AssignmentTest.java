/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.AutomatonReactionWithAlternatives;
import montiarc.cocos.IOAssignmentCallFollowsMethodCall;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.MultipleAssignmentsSameIdentifier;
import montiarc.cocos.UseOfForbiddenExpression;
import montiarc.cocos.UseOfUndeclaredField;
import montiarc.cocos.UseOfValueLists;

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
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IOAssignmentCallFollowsMethodCall()), node,
        new ExpectedErrorInfo(1, "xMA091"));
    
  }
  
  @Test
  public void testAssignsAttributeOfCD() {
    checkValid(PACKAGE + "." + "AssignsAttributeOfCD");
  }
  
  
  @Test
  public void testMethodCallWithoutCallKeyword() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "MethodCallWithoutCallKeyword");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IOAssignmentCallFollowsMethodCall()), node,
        new ExpectedErrorInfo(1, "xMA090"));
  }
  
  @Test
  public void testCallOfOverloadedMethods() {
    checkValid(PACKAGE + "." + "CallOfOverloadedMethods"); 
  }
  
  @Test
  public void testAssignmentWithAlternatives() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AssignmentWithAlternatives");
    checkInvalid(new MontiArcCoCoChecker()
        .addCoCo((MontiArcASTInitialStateDeclarationCoCo) new AutomatonReactionWithAlternatives()),
        node,
        new ExpectedErrorInfo(1, "xMA020"));
  }
  
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
  
  @Test
  /*
   * Tests CoCo [Wor16] AR5.
   */
  public void testAmbiguousMatching() {
    final String qualifiedModelName = PACKAGE + "." + "AmbiguousMatching";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2, "xMA024");
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testAssigningUndefinedVariables() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AssigningUndefinedVariables");
    // 2 Errors because we use 2 undeclared fields
    checkInvalid(
        new MontiArcCoCoChecker().addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfUndeclaredField())
            .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfUndeclaredField()),
        node,
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
  public void testIncompatibleVariableAssignment() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "IncompatibleVariableAssignment");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDifferSimple() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "IncompatibleVariableAssignmentGenericTypesDifferSimple");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(2, "xMA043", "xMA043"));
  }
  
  @Test
  public void testIncompatibleVariableAssignmentGenericTypesDiffer() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "IncompatibleVariableAssignmentGenericTypesDiffer");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA042"));
  }
  
  
  @Test
  public void testUseOfForbiddenExpressions() {
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(
        PACKAGE + "." + "UseOfForbiddenExpressions");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker();
    cocos.addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfForbiddenExpression());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA023");
    checkInvalid(cocos, astMontiArcNode, errors);
  }
  
  @Test
  public void testAssignmentOfSequences() {
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(
        PACKAGE + "." + "AssignmentOfSequence");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new UseOfValueLists());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA081");
    checkInvalid(cocos, astMontiArcNode, errors);
  }
  
  @Test
  public void testAssignmentTypeConflictWithCD() {
    final ASTMontiArcNode ast = loadComponentAST(PACKAGE + "." + "AssignmentTypeConflictWithCD");
    checkInvalid(MontiArcCoCos.createChecker(), ast,
        new ExpectedErrorInfo(1, "xMA042"));  }
  
  @Test
  public void testMultipleMessagesPerCycle() {
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(
        PACKAGE + "." + "OneAssignmentPerCycle");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new UseOfValueLists())
        .addCoCo(new MultipleAssignmentsSameIdentifier());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA081", "xMA019");
    checkInvalid(cocos, astMontiArcNode, errors);
  }

  @Test
  public void testUninitializedVariableAssignment() {
    final String modelName = PACKAGE + "." + "UninitializedVariableAssignment";
    final MontiArcCoCoChecker cocos = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(6, "xMA122");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
}
