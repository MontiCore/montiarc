/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation.util;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.compsymbols._ast.ASTSubcomponentArgument;
import montiarc._symboltable.IMontiArcScope;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

public class ASTExpressionSetEnclosingScope implements ExpressionsBasisVisitor2 {

  protected IMontiArcScope scope;

  public ASTExpressionSetEnclosingScope(IMontiArcScope scope) {
    this.scope = scope;
  }

  public void setEnclosingScope(ASTExpression expression) {
    VariableArcTraverser traverser = VariableArcMill.inheritanceTraverser();
    traverser.add4ExpressionsBasis(this);
    expression.accept(traverser);
  }

  public void setEnclosingScope(ASTSubcomponentArgument argument) {
    VariableArcTraverser traverser = VariableArcMill.inheritanceTraverser();
    traverser.add4ExpressionsBasis(this);
    argument.accept(traverser);
  }

  @Override
  public void visit(ASTExpression node) {
    node.setEnclosingScope(scope);
  }
}
