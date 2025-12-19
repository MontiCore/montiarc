/* (c) https://github.com/MontiCore/monticore */
package montiarc.visitor;

import arcautomaton._ast.ASTArcStatechart;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class ArcAutomatonPrettyPrinterTest extends MontiArcTestBase {

  private static final String MODELS = "parser/statecharts/valid";

  @ParameterizedTest
  @ValueSource(strings = {
    "A_EmptyStateChart.arc",
    "B_JustSomeStates.arc",
    "C_StatesAndTransitions.arc",
    "D_GuardedTransitions.arc",
    "E_TransitionsWithReactions.arc",
    "F_StateWithBody.arc",
    "G_Actions.arc"})
  public void printAutomatonTest(@NotNull String file) throws IOException {
    Preconditions.checkNotNull(file);

    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS, file).toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().deepEquals(origAST.get()));
  }

  @Test
  public void printEmptyStateChart() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS,
      "A_EmptyStateChart.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertInstanceOf(ASTArcStatechart.class, prettyAST.get().getArcComponentType().getBody().getArcElement(2));
    Assertions.assertEquals(0, ((ASTArcStatechart) prettyAST.get().getArcComponentType().getBody()
      .getArcElement(2)).getSCStatechartElementList().size());
  }

  @Test
  public void printJustSomeStates() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS,
      "B_JustSomeStates.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertInstanceOf(ASTArcStatechart.class, prettyAST.get().getArcComponentType().getBody().getArcElement(2));
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getArcComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(0));
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(1));
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(2));
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());
  }

  @Test
  public void printStatesAndTransitions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS,
      "C_StatesAndTransitions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertInstanceOf(ASTArcStatechart.class, prettyAST.get().getArcComponentType().getBody().getArcElement(2));
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getArcComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(0));
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(1));
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(2));
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());

    // Check transitions
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(3));
    ASTSCTransition opened_locked = (ASTSCTransition) statechart.getSCStatechartElement(3);
    Assertions.assertEquals("Opened", opened_locked.getSource().getName());
    Assertions.assertEquals("Locked", opened_locked.getTarget().getName());

    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(4));
    ASTSCTransition locked_closed = (ASTSCTransition) statechart.getSCStatechartElement(4);
    Assertions.assertEquals("Locked", locked_closed.getSource().getName());
    Assertions.assertEquals("Closed", locked_closed.getTarget().getName());

    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(5));
    ASTSCTransition closed_opened = (ASTSCTransition) statechart.getSCStatechartElement(5);
    Assertions.assertEquals("Closed", closed_opened.getSource().getName());
    Assertions.assertEquals("Opened", closed_opened.getTarget().getName());
  }

  @Test
  public void printGuardedTransitions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS,
      "D_GuardedTransitions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertInstanceOf(ASTArcStatechart.class, prettyAST.get().getArcComponentType().getBody().getArcElement(2));
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getArcComponentType().getBody().getArcElement(2);

    // Check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(3));
    ASTSCTransition opened_closed = (ASTSCTransition) statechart.getSCStatechartElement(3);
    Assertions.assertEquals("Opened", opened_closed.getSource().getName());
    Assertions.assertEquals("Closed", opened_closed.getTarget().getName());

    // Check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(4));
    ASTSCTransition closed_opened = (ASTSCTransition) statechart.getSCStatechartElement(4);
    Assertions.assertEquals("Closed", closed_opened.getSource().getName());
    Assertions.assertEquals("Opened", closed_opened.getTarget().getName());

    // Check guard
    Assertions.assertInstanceOf(ASTNameExpression.class, ((ASTTransitionBody) closed_opened.getSCTBody()).getPre());
    Assertions.assertEquals("open", ((ASTNameExpression) ((ASTTransitionBody) closed_opened.getSCTBody()).getPre()).getName());

    // Check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(5));
    ASTSCTransition closed_locked = (ASTSCTransition) statechart.getSCStatechartElement(5);
    Assertions.assertEquals("Closed", closed_locked.getSource().getName());
    Assertions.assertEquals("Locked", closed_locked.getTarget().getName());

    //check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(6));
    ASTSCTransition locked_closed = (ASTSCTransition) statechart.getSCStatechartElement(6);
    Assertions.assertEquals("Locked", locked_closed.getSource().getName());
    Assertions.assertEquals("Closed", locked_closed.getTarget().getName());

    // Check guard
    Assertions.assertInstanceOf(ASTEqualsExpression.class, ((ASTTransitionBody) locked_closed.getSCTBody()).getPre());
    Assertions.assertInstanceOf(ASTNameExpression.class, ((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getLeft());
    Assertions.assertEquals("unlock", ((ASTNameExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getLeft()).getName());
    Assertions.assertEquals("==", ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getOperator());
    Assertions.assertInstanceOf(ASTLiteralExpression.class, ((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getRight());
    Assertions.assertTrue(((ASTBooleanLiteral) ((ASTLiteralExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getRight()).getLiteral()).getValue());
  }

  @Test
  public void printTransitionsWithReactions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS,
      "E_TransitionsWithReactions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertInstanceOf(ASTArcStatechart.class, prettyAST.get().getArcComponentType().getBody().getArcElement(2));
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getArcComponentType().getBody().getArcElement(2);

    // Check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(3));
    ASTSCTransition opened_closed = (ASTSCTransition) statechart.getSCStatechartElement(3);
    Assertions.assertEquals("Opened", opened_closed.getSource().getName());
    Assertions.assertEquals("Closed", opened_closed.getTarget().getName());

    // Check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(4));
    ASTSCTransition closed_opened = (ASTSCTransition) statechart.getSCStatechartElement(4);
    Assertions.assertEquals("Closed", closed_opened.getSource().getName());
    Assertions.assertEquals("Opened", closed_opened.getTarget().getName());

    // Check guard
    Assertions.assertInstanceOf(ASTNameExpression.class, ((ASTTransitionBody) closed_opened.getSCTBody()).getPre());
    Assertions.assertEquals("open",
      ((ASTNameExpression) ((ASTTransitionBody) closed_opened.getSCTBody()).getPre()).getName());

    // Check reaction
    Assertions.assertInstanceOf(ASTMCJavaBlock.class, ((ASTTransitionBody) closed_opened.getSCTBody())
      .getTransitionAction().getMCStatement());
    ASTMCJavaBlock closed_opened_action = (ASTMCJavaBlock)
      ((ASTTransitionBody) closed_opened.getSCTBody()).getTransitionAction().getMCStatement();
    Assertions.assertInstanceOf(ASTExpressionStatement.class, closed_opened_action.getMCBlockStatement(0));
    Assertions.assertInstanceOf(ASTAssignmentExpression.class, ((ASTExpressionStatement) closed_opened_action
      .getMCBlockStatement(0)).getExpression());
    ASTAssignmentExpression closed_opened_expression = ((ASTAssignmentExpression) ((ASTExpressionStatement) closed_opened_action
      .getMCBlockStatement(0)).getExpression());
    Assertions.assertEquals("ringing", ((ASTNameExpression) closed_opened_expression.getLeft()).getName());
    Assertions.assertTrue(((ASTBooleanLiteral)
      ((ASTLiteralExpression) closed_opened_expression.getRight()).getLiteral()).getValue());

    // Check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(5));
    ASTSCTransition closed_locked = (ASTSCTransition) statechart.getSCStatechartElement(5);
    Assertions.assertEquals("Closed", closed_locked.getSource().getName());
    Assertions.assertEquals("Locked", closed_locked.getTarget().getName());

    // Check reaction
    Assertions.assertInstanceOf(ASTMCJavaBlock.class, ((ASTTransitionBody) closed_locked.getSCTBody())
      .getTransitionAction().getMCStatement());
    ASTMCJavaBlock closed_locked_action = (ASTMCJavaBlock) ((ASTTransitionBody) closed_locked.getSCTBody())
      .getTransitionAction().getMCStatement();
    Assertions.assertInstanceOf(ASTExpressionStatement.class, closed_locked_action.getMCBlockStatement(0));
    Assertions.assertInstanceOf(ASTCallExpression.class, ((ASTExpressionStatement) closed_locked_action.getMCBlockStatement(0))
      .getExpression());
    ASTCallExpression closed_locked_expression = ((ASTCallExpression) ((ASTExpressionStatement) closed_locked_action
      .getMCBlockStatement(0)).getExpression());
    Assertions.assertInstanceOf(ASTFieldAccessExpression.class, closed_locked_expression.getExpression());
    ASTFieldAccessExpression print = (ASTFieldAccessExpression) closed_locked_expression.getExpression();
    Assertions.assertEquals("println", print.getName());
    Assertions.assertInstanceOf(ASTFieldAccessExpression.class, print.getExpression());
    ASTFieldAccessExpression out = (ASTFieldAccessExpression) print.getExpression();
    Assertions.assertEquals("out", out.getName());
    Assertions.assertInstanceOf(ASTNameExpression.class, out.getExpression());
    ASTNameExpression system = (ASTNameExpression) out.getExpression();
    Assertions.assertEquals("System", system.getName());
    Assertions.assertInstanceOf(ASTLiteralExpression.class, closed_locked_expression.getArguments().getExpression(0));
    ASTLiteralExpression arguments = (ASTLiteralExpression) closed_locked_expression.getArguments().getExpression(0);
    Assertions.assertEquals("Door locked now.", ((ASTStringLiteral) arguments.getLiteral()).getSource());

    // Check transition
    Assertions.assertInstanceOf(ASTSCTransition.class, statechart.getSCStatechartElement(6));
    ASTSCTransition locked_closed = (ASTSCTransition) statechart.getSCStatechartElement(6);
    Assertions.assertEquals("Locked", locked_closed.getSource().getName());
    Assertions.assertEquals("Closed", locked_closed.getTarget().getName());

    // Check guard
    Assertions.assertInstanceOf(ASTEqualsExpression.class, ((ASTTransitionBody) locked_closed.getSCTBody()).getPre());
    Assertions.assertInstanceOf(ASTNameExpression.class, ((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getLeft());
    Assertions.assertEquals("unlock", ((ASTNameExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getLeft()).getName());
    Assertions.assertEquals("==",
      ((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getOperator());
    Assertions.assertInstanceOf(ASTLiteralExpression.class, ((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getRight());
    Assertions.assertTrue(((ASTBooleanLiteral) ((ASTLiteralExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getRight()).getLiteral()).getValue());
  }

  @Test
  public void printStateWithBody() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS,
      "F_StateWithBody.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertInstanceOf(ASTArcStatechart.class, prettyAST.get().getArcComponentType().getBody().getArcElement(2));
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getArcComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(0));
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());
    Assertions.assertFalse(closed.isPresentSCSBody());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(1));
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());
    Assertions.assertFalse(locked.isPresentSCSBody());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(2));
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());
  }

  @Test
  public void printActions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(TEST_RESOURCE, MODELS, "G_Actions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = MontiArcMill.prettyPrint(origAST.get(), true);

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertEquals("G_Actions", prettyAST.get().getArcComponentType().getName());
    Assertions.assertInstanceOf(ASTArcStatechart.class, prettyAST.get().getArcComponentType().getBody().getArcElement(2));
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getArcComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(0));
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());
    Assertions.assertFalse(closed.isPresentSCSBody());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(1));
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());
    Assertions.assertFalse(locked.isPresentSCSBody());

    Assertions.assertInstanceOf(ASTSCState.class, statechart.getSCStatechartElement(2));
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());
    Assertions.assertTrue(opened.isPresentSCSBody());
    Assertions.assertInstanceOf(ASTSCHierarchyBody.class, opened.getSCSBody());

    // Check actions
    ASTSCHierarchyBody opened_body = (ASTSCHierarchyBody) opened.getSCSBody();
    Assertions.assertInstanceOf(ASTSCEntryAction.class, opened_body.getSCStateElement(0));
    Assertions.assertInstanceOf(ASTMCJavaBlock.class, ((ASTTransitionAction) ((ASTSCEntryAction) opened_body.getSCStateElement(0)).getSCABody())
      .getMCStatement());
    ASTMCJavaBlock entryAction = (ASTMCJavaBlock) ((ASTTransitionAction) ((ASTSCEntryAction) opened_body.getSCStateElement(0))
      .getSCABody()).getMCStatement();
    Assertions.assertInstanceOf(ASTExpressionStatement.class, entryAction.getMCBlockStatement(0));
    Assertions.assertInstanceOf(ASTCallExpression.class, ((ASTExpressionStatement) entryAction.getMCBlockStatement(0))
      .getExpression());
    ASTCallExpression entry_expression = ((ASTCallExpression) ((ASTExpressionStatement) entryAction.getMCBlockStatement(0))
      .getExpression());
    Assertions.assertInstanceOf(ASTFieldAccessExpression.class, entry_expression.getExpression());
    ASTFieldAccessExpression println = (ASTFieldAccessExpression) entry_expression.getExpression();
    Assertions.assertEquals("println", println.getName());
    Assertions.assertInstanceOf(ASTFieldAccessExpression.class, println.getExpression());
    ASTFieldAccessExpression out = (ASTFieldAccessExpression) println.getExpression();
    Assertions.assertEquals("out", out.getName());
    Assertions.assertInstanceOf(ASTNameExpression.class, out.getExpression());
    ASTNameExpression system = (ASTNameExpression) out.getExpression();
    Assertions.assertEquals("System", system.getName());
    Assertions.assertInstanceOf(ASTLiteralExpression.class, entry_expression.getArguments().getExpression(0));
    ASTLiteralExpression arguments = (ASTLiteralExpression) entry_expression.getArguments().getExpression(0);
    Assertions.assertEquals("door opens", ((ASTStringLiteral) arguments.getLiteral()).getSource());

    Assertions.assertInstanceOf(ASTSCExitAction.class, opened_body.getSCStateElement(1));
    Assertions.assertInstanceOf(ASTMCJavaBlock.class, ((ASTTransitionAction) ((ASTSCExitAction) opened_body.getSCStateElement(1)).getSCABody())
      .getMCStatement());
    ASTMCJavaBlock exitAction = (ASTMCJavaBlock) ((ASTTransitionAction) ((ASTSCExitAction) opened_body.getSCStateElement(1))
      .getSCABody()).getMCStatement();
    Assertions.assertInstanceOf(ASTExpressionStatement.class, exitAction.getMCBlockStatement(0));
    Assertions.assertInstanceOf(ASTCallExpression.class, ((ASTExpressionStatement) exitAction.getMCBlockStatement(0))
      .getExpression());
    ASTCallExpression exit_expression = ((ASTCallExpression) ((ASTExpressionStatement) exitAction
      .getMCBlockStatement(0)).getExpression());
    Assertions.assertInstanceOf(ASTFieldAccessExpression.class, exit_expression.getExpression());
    println = (ASTFieldAccessExpression) exit_expression.getExpression();
    Assertions.assertEquals("println", println.getName());
    Assertions.assertInstanceOf(ASTFieldAccessExpression.class, println.getExpression());
    out = (ASTFieldAccessExpression) println.getExpression();
    Assertions.assertEquals("out", out.getName());
    Assertions.assertInstanceOf(ASTNameExpression.class, out.getExpression());
    system = (ASTNameExpression) out.getExpression();
    Assertions.assertEquals("System", system.getName());
    Assertions.assertInstanceOf(ASTLiteralExpression.class, exit_expression.getArguments().getExpression(0));
    arguments = (ASTLiteralExpression) exit_expression.getArguments().getExpression(0);
    Assertions.assertEquals("door closes", ((ASTStringLiteral) arguments.getLiteral()).getSource());
  }
}
