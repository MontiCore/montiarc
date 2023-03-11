/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation.util;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;
import variablearc._visitor.VariableArcTraverser;

public class ASTExpressionSetEnclosingScope implements ExpressionsBasisVisitor2 {

  protected IVariableArcScope scope;

  public ASTExpressionSetEnclosingScope(IVariableArcScope scope) {
    this.scope = scope;
  }

  public void setEnclosingScope(ASTExpression expression) {
    VariableArcTraverser traverser = VariableArcMill.inheritanceTraverser();
    traverser.add4ExpressionsBasis(this);
    expression.accept(traverser);
  }

  @Override
  public void visit(ASTExpression node) {
    node.setEnclosingScope(scope);
  }
}
