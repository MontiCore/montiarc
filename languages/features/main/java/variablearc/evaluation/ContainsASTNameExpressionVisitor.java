/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcVisitor2;

/**
 * A visitor that checks if the AST contains a {@code ASTNameExpression}.
 */
public class ContainsASTNameExpressionVisitor implements VariableArcVisitor2,
                                                         ExpressionsBasisVisitor2 {
  private boolean result;

  public ContainsASTNameExpressionVisitor(){
    this.result = false;
  }

  public boolean getResult() {
    return this.result;
  }

  @Override
  public void visit(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    this.result = true;
  }
}
