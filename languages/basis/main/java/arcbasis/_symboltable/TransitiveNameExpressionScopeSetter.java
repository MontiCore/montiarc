/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Sets the scope as enclosing scope of the provided node and all child nodes by traversing the ast structure using an inheritance visitor.
 */
public class TransitiveNameExpressionScopeSetter implements ExpressionsBasisVisitor2 {
  protected final IArcBasisScope scope;

  public TransitiveNameExpressionScopeSetter(@NotNull IArcBasisScope scope) {
    Preconditions.checkNotNull(scope);
    this.scope = scope;
  }

  public void set(@NotNull ASTExpression expression) {
    Preconditions.checkNotNull(expression);
    ArcBasisTraverser traverser = ArcBasisMill.inheritanceTraverser();
    traverser.add4ExpressionsBasis(this);
    expression.accept(traverser);
  }

  @Override
  public void visit(ASTExpression node) {
    node.setEnclosingScope(scope);
  }
}
