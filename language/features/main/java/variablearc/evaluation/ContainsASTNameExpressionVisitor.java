/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import variablearc._visitor.VariableArcVisitor2;

/**
 * A visitor that checks if the AST contains a {@code ASTNameExpression}.
 */
public class ContainsASTNameExpressionVisitor implements VariableArcVisitor2, ExpressionsBasisVisitor2 {
  private boolean result;

  public ContainsASTNameExpressionVisitor(){
    result = false;
  }

  public boolean getResult() {
    return result;
  }

  @Override
  public void visit(ASTNameExpression node) {
    result = true;
  }
}
