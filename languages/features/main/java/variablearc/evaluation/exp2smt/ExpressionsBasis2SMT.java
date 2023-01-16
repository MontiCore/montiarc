/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;


public class ExpressionsBasis2SMT implements ExpressionsBasisHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected ExpressionsBasisTraverser traverser;

  public ExpressionsBasis2SMT(@NotNull IDeriveSMTExpr deriveSMTExpr) {
    Preconditions.checkNotNull(deriveSMTExpr);
    this.deriveSMTExpr = deriveSMTExpr;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return this.traverser;
  }

  protected Expr2SMTResult getResult() {
    return this.deriveSMTExpr.getResult();
  }

  protected Context getContext() {
    return this.deriveSMTExpr.getContext();
  }

  protected IDeriveSMTSort getExpr2Sort() {
    return this.deriveSMTExpr.getSortDerive();
  }

  protected String getPrefix() {
    return this.deriveSMTExpr.getPrefix();
  }

  @Override
  public void handle(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    Optional<Sort> sort = this.getExpr2Sort().toSort(this.getContext(), node);
    if (sort.isPresent()) {
      this.getResult().setValue(this.getContext().mkConst(getPrefix() + "." + node.getName(), sort.get()));
    } else {
      this.getResult().clear();
    }
  }
}
