/* (c) https://github.com/MontiCore/monticore */
package montiarc.visitor;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTStateBody;
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
import de.monticore.scbasis._ast.ASTSCEmptyBody;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class ArcAutomatonPrettyPrinterTest extends AbstractTest {

  private static final String MODELS = "montiarc/parser/statecharts/valid";

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
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS, file).toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().deepEquals(origAST.get()));
  }

  @Test
  public void printEmptyStateChart() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS,
      "A_EmptyStateChart.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().getComponentType().getBody().getArcElement(2) instanceof ASTArcStatechart);
    Assertions.assertEquals(0, ((ASTArcStatechart) prettyAST.get().getComponentType().getBody()
      .getArcElement(2)).getSCStatechartElementList().size());
  }

  @Test
  public void printJustSomeStates() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS,
      "B_JustSomeStates.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().getComponentType().getBody().getArcElement(2) instanceof ASTArcStatechart);
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertTrue(statechart.getSCStatechartElement(0) instanceof ASTSCState);
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());

    Assertions.assertTrue(statechart.getSCStatechartElement(1) instanceof ASTSCState);
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());

    Assertions.assertTrue(statechart.getSCStatechartElement(2) instanceof ASTSCState);
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());
  }

  @Test
  public void printStatesAndTransitions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS,
      "C_StatesAndTransitions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().getComponentType().getBody().getArcElement(2) instanceof ASTArcStatechart);
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertTrue(statechart.getSCStatechartElement(0) instanceof ASTSCState);
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());

    Assertions.assertTrue(statechart.getSCStatechartElement(1) instanceof ASTSCState);
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());

    Assertions.assertTrue(statechart.getSCStatechartElement(2) instanceof ASTSCState);
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());

    // Check transitions
    Assertions.assertTrue(statechart.getSCStatechartElement(3) instanceof ASTSCTransition);
    ASTSCTransition opened_locked = (ASTSCTransition) statechart.getSCStatechartElement(3);
    Assertions.assertEquals("Opened", opened_locked.getSourceName());
    Assertions.assertEquals("Locked", opened_locked.getTargetName());

    Assertions.assertTrue(statechart.getSCStatechartElement(4) instanceof ASTSCTransition);
    ASTSCTransition locked_closed = (ASTSCTransition) statechart.getSCStatechartElement(4);
    Assertions.assertEquals("Locked", locked_closed.getSourceName());
    Assertions.assertEquals("Closed", locked_closed.getTargetName());

    Assertions.assertTrue(statechart.getSCStatechartElement(5) instanceof ASTSCTransition);
    ASTSCTransition closed_opened = (ASTSCTransition) statechart.getSCStatechartElement(5);
    Assertions.assertEquals("Closed", closed_opened.getSourceName());
    Assertions.assertEquals("Opened", closed_opened.getTargetName());
  }

  @Test
  public void printGuardedTransitions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS,
      "D_GuardedTransitions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().getComponentType().getBody().getArcElement(2) instanceof ASTArcStatechart);
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getComponentType().getBody().getArcElement(2);

    // Check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(3) instanceof ASTSCTransition);
    ASTSCTransition opened_closed = (ASTSCTransition) statechart.getSCStatechartElement(3);
    Assertions.assertEquals("Opened", opened_closed.getSourceName());
    Assertions.assertEquals("Closed", opened_closed.getTargetName());

    // Check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(4) instanceof ASTSCTransition);
    ASTSCTransition closed_opened = (ASTSCTransition) statechart.getSCStatechartElement(4);
    Assertions.assertEquals("Closed", closed_opened.getSourceName());
    Assertions.assertEquals("Opened", closed_opened.getTargetName());

    // Check guard
    Assertions.assertTrue(((ASTTransitionBody) closed_opened.getSCTBody()).getPre() instanceof ASTNameExpression);
    Assertions.assertEquals("open", ((ASTNameExpression) ((ASTTransitionBody) closed_opened.getSCTBody()).getPre()).getName());

    // Check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(5) instanceof ASTSCTransition);
    ASTSCTransition closed_locked = (ASTSCTransition) statechart.getSCStatechartElement(5);
    Assertions.assertEquals("Closed", closed_locked.getSourceName());
    Assertions.assertEquals("Locked", closed_locked.getTargetName());

    //check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(6) instanceof ASTSCTransition);
    ASTSCTransition locked_closed = (ASTSCTransition) statechart.getSCStatechartElement(6);
    Assertions.assertEquals("Locked", locked_closed.getSourceName());
    Assertions.assertEquals("Closed", locked_closed.getTargetName());

    // Check guard
    Assertions.assertTrue(((ASTTransitionBody) locked_closed.getSCTBody()).getPre() instanceof ASTEqualsExpression);
    Assertions.assertTrue(((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getLeft() instanceof ASTNameExpression);
    Assertions.assertEquals("unlock", ((ASTNameExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getLeft()).getName());
    Assertions.assertEquals("==", ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getOperator());
    Assertions.assertTrue(((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getRight() instanceof ASTLiteralExpression);
    Assertions.assertTrue(((ASTBooleanLiteral) ((ASTLiteralExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getRight()).getLiteral()).getValue());
  }

  @Test
  public void printTransitionsWithReactions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS,
      "E_TransitionsWithReactions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().getComponentType().getBody().getArcElement(2) instanceof ASTArcStatechart);
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getComponentType().getBody().getArcElement(2);

    // Check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(3) instanceof ASTSCTransition);
    ASTSCTransition opened_closed = (ASTSCTransition) statechart.getSCStatechartElement(3);
    Assertions.assertEquals("Opened", opened_closed.getSourceName());
    Assertions.assertEquals("Closed", opened_closed.getTargetName());

    // Check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(4) instanceof ASTSCTransition);
    ASTSCTransition closed_opened = (ASTSCTransition) statechart.getSCStatechartElement(4);
    Assertions.assertEquals("Closed", closed_opened.getSourceName());
    Assertions.assertEquals("Opened", closed_opened.getTargetName());

    // Check guard
    Assertions.assertTrue(((ASTTransitionBody) closed_opened.getSCTBody()).getPre() instanceof ASTNameExpression);
    Assertions.assertEquals("open",
      ((ASTNameExpression) ((ASTTransitionBody) closed_opened.getSCTBody()).getPre()).getName());

    // Check reaction
    Assertions.assertTrue(((ASTTransitionBody) closed_opened.getSCTBody())
      .getTransitionAction().getMCBlockStatement() instanceof ASTMCJavaBlock);
    ASTMCJavaBlock closed_opened_action = (ASTMCJavaBlock)
      ((ASTTransitionBody) closed_opened.getSCTBody()).getTransitionAction().getMCBlockStatement();
    Assertions.assertTrue(closed_opened_action.getMCBlockStatement(0) instanceof ASTExpressionStatement);
    Assertions.assertTrue(((ASTExpressionStatement) closed_opened_action
      .getMCBlockStatement(0)).getExpression() instanceof ASTAssignmentExpression);
    ASTAssignmentExpression closed_opened_expression = ((ASTAssignmentExpression) ((ASTExpressionStatement) closed_opened_action
      .getMCBlockStatement(0)).getExpression());
    Assertions.assertEquals("ringing", ((ASTNameExpression) closed_opened_expression.getLeft()).getName());
    Assertions.assertTrue(((ASTBooleanLiteral)
      ((ASTLiteralExpression) closed_opened_expression.getRight()).getLiteral()).getValue());

    // Check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(5) instanceof ASTSCTransition);
    ASTSCTransition closed_locked = (ASTSCTransition) statechart.getSCStatechartElement(5);
    Assertions.assertEquals("Closed", closed_locked.getSourceName());
    Assertions.assertEquals("Locked", closed_locked.getTargetName());

    // Check reaction
    Assertions.assertTrue(((ASTTransitionBody) closed_locked.getSCTBody())
      .getTransitionAction().getMCBlockStatement() instanceof ASTMCJavaBlock);
    ASTMCJavaBlock closed_locked_action = (ASTMCJavaBlock) ((ASTTransitionBody) closed_locked.getSCTBody())
      .getTransitionAction().getMCBlockStatement();
    Assertions.assertTrue(closed_locked_action.getMCBlockStatement(0) instanceof ASTExpressionStatement);
    Assertions.assertTrue(((ASTExpressionStatement) closed_locked_action.getMCBlockStatement(0))
      .getExpression() instanceof ASTCallExpression);
    ASTCallExpression closed_locked_expression = ((ASTCallExpression) ((ASTExpressionStatement) closed_locked_action
      .getMCBlockStatement(0)).getExpression());
    Assertions.assertTrue(closed_locked_expression.getExpression() instanceof ASTFieldAccessExpression);
    ASTFieldAccessExpression print = (ASTFieldAccessExpression) closed_locked_expression.getExpression();
    Assertions.assertEquals("println", print.getName());
    Assertions.assertTrue(print.getExpression() instanceof ASTFieldAccessExpression);
    ASTFieldAccessExpression out = (ASTFieldAccessExpression) print.getExpression();
    Assertions.assertEquals("out", out.getName());
    Assertions.assertTrue(out.getExpression() instanceof ASTNameExpression);
    ASTNameExpression system = (ASTNameExpression) out.getExpression();
    Assertions.assertEquals("System", system.getName());
    Assertions.assertTrue(closed_locked_expression.getArguments().getExpression(0) instanceof ASTLiteralExpression);
    ASTLiteralExpression arguments = (ASTLiteralExpression) closed_locked_expression.getArguments().getExpression(0);
    Assertions.assertEquals("Door locked now.", ((ASTStringLiteral) arguments.getLiteral()).getSource());

    // Check transition
    Assertions.assertTrue(statechart.getSCStatechartElement(6) instanceof ASTSCTransition);
    ASTSCTransition locked_closed = (ASTSCTransition) statechart.getSCStatechartElement(6);
    Assertions.assertEquals("Locked", locked_closed.getSourceName());
    Assertions.assertEquals("Closed", locked_closed.getTargetName());

    // Check guard
    Assertions.assertTrue(((ASTTransitionBody) locked_closed.getSCTBody()).getPre() instanceof ASTEqualsExpression);
    Assertions.assertTrue(((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getLeft() instanceof ASTNameExpression);
    Assertions.assertEquals("unlock", ((ASTNameExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getLeft()).getName());
    Assertions.assertEquals("==",
      ((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getOperator());
    Assertions.assertTrue(((ASTEqualsExpression) ((ASTTransitionBody) locked_closed.getSCTBody())
      .getPre()).getRight() instanceof ASTLiteralExpression);
    Assertions.assertTrue(((ASTBooleanLiteral) ((ASTLiteralExpression) ((ASTEqualsExpression)
      ((ASTTransitionBody) locked_closed.getSCTBody()).getPre()).getRight()).getLiteral()).getValue());
  }

  @Test
  public void printStateWithBody() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS,
      "F_StateWithBody.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertTrue(prettyAST.get().getComponentType().getBody().getArcElement(2) instanceof ASTArcStatechart);
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertTrue(statechart.getSCStatechartElement(0) instanceof ASTSCState);
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());
    Assertions.assertTrue(closed.getSCSBody() instanceof ASTSCEmptyBody);

    Assertions.assertTrue(statechart.getSCStatechartElement(1) instanceof ASTSCState);
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());
    Assertions.assertTrue(locked.getSCSBody() instanceof ASTSCEmptyBody);

    Assertions.assertTrue(statechart.getSCStatechartElement(2) instanceof ASTSCState);
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());
    Assertions.assertTrue(opened.getSCSBody() instanceof ASTStateBody);
  }

  @Test
  public void printActions() throws IOException {
    // Given
    MontiArcParser parser = MontiArcMill.parser();
    MontiArcFullPrettyPrinter prettyPrinterDelegator = new MontiArcFullPrettyPrinter();
    Optional<ASTMACompilationUnit> origAST = parser.parse(Paths.get(RELATIVE_MODEL_PATH, MODELS, "G_Actions.arc").toString());
    Preconditions.checkState(origAST.isPresent());

    // When
    String prettyOut = prettyPrinterDelegator.prettyprint(origAST.get());

    // Then
    Optional<ASTMACompilationUnit> prettyAST = parser.parse_String(prettyOut);
    Assertions.assertTrue(prettyAST.isPresent());
    Assertions.assertEquals("G_Actions", prettyAST.get().getComponentType().getName());
    Assertions.assertTrue(prettyAST.get().getComponentType().getBody().getArcElement(2) instanceof ASTArcStatechart);
    ASTArcStatechart statechart = (ASTArcStatechart) prettyAST.get().getComponentType().getBody().getArcElement(2);

    // Check states
    Assertions.assertTrue(statechart.getSCStatechartElement(0) instanceof ASTSCState);
    ASTSCState closed = (ASTSCState) statechart.getSCStatechartElement(0);
    Assertions.assertEquals("Closed", closed.getName());
    Assertions.assertTrue(closed.getSCModifier().isInitial());
    Assertions.assertFalse(closed.getSCModifier().isFinal());
    Assertions.assertTrue(closed.getSCSBody() instanceof ASTSCEmptyBody);

    Assertions.assertTrue(statechart.getSCStatechartElement(1) instanceof ASTSCState);
    ASTSCState locked = (ASTSCState) statechart.getSCStatechartElement(1);
    Assertions.assertEquals("Locked", locked.getName());
    Assertions.assertFalse(locked.getSCModifier().isInitial());
    Assertions.assertFalse(locked.getSCModifier().isFinal());
    Assertions.assertTrue(locked.getSCSBody() instanceof ASTSCEmptyBody);

    Assertions.assertTrue(statechart.getSCStatechartElement(2) instanceof ASTSCState);
    ASTSCState opened = (ASTSCState) statechart.getSCStatechartElement(2);
    Assertions.assertEquals("Opened", opened.getName());
    Assertions.assertFalse(opened.getSCModifier().isInitial());
    Assertions.assertFalse(opened.getSCModifier().isFinal());
    Assertions.assertTrue(opened.getSCSBody() instanceof ASTStateBody);

    // Check actions
    ASTStateBody opened_body = (ASTStateBody) opened.getSCSBody();
    Assertions.assertTrue(opened_body.getSCStateElement(0) instanceof ASTSCEntryAction);
    Assertions.assertTrue(((ASTTransitionAction) ((ASTSCEntryAction) opened_body.getSCStateElement(0)).getSCABody())
      .getMCBlockStatement() instanceof ASTMCJavaBlock);
    ASTMCJavaBlock entryAction = (ASTMCJavaBlock) ((ASTTransitionAction) ((ASTSCEntryAction) opened_body.getSCStateElement(0))
      .getSCABody()).getMCBlockStatement();
    Assertions.assertTrue(entryAction.getMCBlockStatement(0) instanceof ASTExpressionStatement);
    Assertions.assertTrue(((ASTExpressionStatement) entryAction.getMCBlockStatement(0))
      .getExpression() instanceof ASTCallExpression);
    ASTCallExpression entry_expression = ((ASTCallExpression) ((ASTExpressionStatement) entryAction.getMCBlockStatement(0))
      .getExpression());
    Assertions.assertTrue(entry_expression.getExpression() instanceof ASTFieldAccessExpression);
    ASTFieldAccessExpression println = (ASTFieldAccessExpression) entry_expression.getExpression();
    Assertions.assertEquals("println", println.getName());
    Assertions.assertTrue(println.getExpression() instanceof ASTFieldAccessExpression);
    ASTFieldAccessExpression out = (ASTFieldAccessExpression) println.getExpression();
    Assertions.assertEquals("out", out.getName());
    Assertions.assertTrue(out.getExpression() instanceof ASTNameExpression);
    ASTNameExpression system = (ASTNameExpression) out.getExpression();
    Assertions.assertEquals("System", system.getName());
    Assertions.assertTrue(entry_expression.getArguments().getExpression(0) instanceof ASTLiteralExpression);
    ASTLiteralExpression arguments = (ASTLiteralExpression) entry_expression.getArguments().getExpression(0);
    Assertions.assertEquals("door opens", ((ASTStringLiteral) arguments.getLiteral()).getSource());

    Assertions.assertTrue(opened_body.getSCStateElement(1) instanceof ASTSCExitAction);
    Assertions.assertTrue(((ASTTransitionAction) ((ASTSCExitAction) opened_body.getSCStateElement(1)).getSCABody())
      .getMCBlockStatement() instanceof ASTMCJavaBlock);
    ASTMCJavaBlock exitAction = (ASTMCJavaBlock) ((ASTTransitionAction) ((ASTSCExitAction) opened_body.getSCStateElement(1))
      .getSCABody()).getMCBlockStatement();
    Assertions.assertTrue(exitAction.getMCBlockStatement(0) instanceof ASTExpressionStatement);
    Assertions.assertTrue(((ASTExpressionStatement) exitAction.getMCBlockStatement(0))
      .getExpression() instanceof ASTCallExpression);
    ASTCallExpression exit_expression = ((ASTCallExpression) ((ASTExpressionStatement) exitAction
      .getMCBlockStatement(0)).getExpression());
    Assertions.assertTrue(exit_expression.getExpression() instanceof ASTFieldAccessExpression);
    println = (ASTFieldAccessExpression) exit_expression.getExpression();
    Assertions.assertEquals("println", println.getName());
    Assertions.assertTrue(println.getExpression() instanceof ASTFieldAccessExpression);
    out = (ASTFieldAccessExpression) println.getExpression();
    Assertions.assertEquals("out", out.getName());
    Assertions.assertTrue(out.getExpression() instanceof ASTNameExpression);
    system = (ASTNameExpression) out.getExpression();
    Assertions.assertEquals("System", system.getName());
    Assertions.assertTrue(exit_expression.getArguments().getExpression(0) instanceof ASTLiteralExpression);
    arguments = (ASTLiteralExpression) exit_expression.getArguments().getExpression(0);
    Assertions.assertEquals("door closes", ((ASTStringLiteral) arguments.getLiteral()).getSource());
  }
}
