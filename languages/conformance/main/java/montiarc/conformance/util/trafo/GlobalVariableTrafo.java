/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util.trafo;

import arcbasis._ast.ASTArcField;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._visitor.SCBasisVisitor2;
import de.monticore.sctransitions4code.SCTransitions4CodeMill;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import java.util.*;
import java.util.stream.Collectors;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;

public class GlobalVariableTrafo {
  /**
   * Transform transitions and set the value of the global variables to be the same as before the
   * transition was executed when these are not explicitly set. This transformation must be executed
   * before building the symbol table.
   */
  public static void transform(ASTMACompilationUnit aut) {
    Map<String, ASTExpressionStatement> actions = new HashMap<>();
    getGlobalVarNames(aut).forEach(name -> actions.put(name, buildAssignmentStatement(name, name)));
    if (!actions.isEmpty()) {
      MontiArcTraverser traverser = MontiArcMill.traverser();
      traverser.add4SCBasis(new AddActionsToTransitions(actions));
      aut.accept(traverser);
    }
  }

  private static ASTExpressionStatement buildAssignmentStatement(String left, String right) {
    return MCCommonStatementsMill.expressionStatementBuilder()
        .setExpression(
            AssignmentExpressionsMill.assignmentExpressionBuilder()
                .setLeft(ExpressionsBasisMill.nameExpressionBuilder().setName(left).build())
                .setRight(ExpressionsBasisMill.nameExpressionBuilder().setName(right).build())
                .build())
        .build();
  }

  private static ASTTransitionAction buildTransitionAction(List<ASTExpressionStatement> actions) {
    return SCTransitions4CodeMill.transitionActionBuilder()
        .setMCBlockStatement(
            MCCommonStatementsMill.mCJavaBlockBuilder()
                .setMCBlockStatementsList(new ArrayList<>(actions))
                .build())
        .build();
  }

  private static List<String> getGlobalVarNames(ASTMACompilationUnit ma) {
    return ma.getComponentType().getFields().stream()
        .map(ASTArcField::getName)
        .collect(Collectors.toList());
  }

  public static class AddActionsToTransitions implements SCBasisVisitor2 {
    private final Map<String, ASTExpressionStatement> actions;

    public AddActionsToTransitions(Map<String, ASTExpressionStatement> actions) {
      this.actions = actions;
    }

    @Override
    public void visit(ASTSCTransition node) {

      ASTTransitionBody body = (ASTTransitionBody) node.getSCTBody();

      if (body.isPresentTransitionAction()) {
        if (body.getTransitionAction().getMCBlockStatement() instanceof ASTMCJavaBlock) {
          ASTMCJavaBlock actionBody =
              (ASTMCJavaBlock) body.getTransitionAction().getMCBlockStatement();

          MontiArcTraverser traverser = MontiArcMill.traverser();
          AssignmentsFilter filter = new AssignmentsFilter(new HashMap<>(actions));
          traverser.add4AssignmentExpressions(filter);
          actionBody.accept(traverser);

          actionBody.addAllMCBlockStatements(filter.getActions());
        }
      } else {
        body.setTransitionAction(buildTransitionAction(new ArrayList<>(actions.values())));
      }
    }

    public static class AssignmentsFilter implements AssignmentExpressionsVisitor2 {
      private final Map<String, ASTExpressionStatement> actions;

      public AssignmentsFilter(Map<String, ASTExpressionStatement> actions) {
        this.actions = actions;
      }

      @Override
      public void visit(ASTAssignmentExpression node) {
        if (node.getLeft() instanceof ASTNameExpression) {
          ASTNameExpression left = (ASTNameExpression) node.getLeft();
          actions.remove(left.getName());
        }
      }

      public Set<ASTExpressionStatement> getActions() {
        return new HashSet<>(actions.values());
      }
    }
  }
}
