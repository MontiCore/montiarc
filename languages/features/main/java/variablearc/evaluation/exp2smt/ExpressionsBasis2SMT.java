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

  Expr2SMTResult result;
  Context context;
  IDeriveSMTSort expr2Sort;
  ExpressionsBasisTraverser traverser;

  public ExpressionsBasis2SMT(@NotNull Expr2SMTResult result,
                              @NotNull Context context,
                              @NotNull IDeriveSMTSort expr2Sort) {
    Preconditions.checkNotNull(result);
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(expr2Sort);
    this.result = result;
    this.context = context;
    this.expr2Sort = expr2Sort;
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
    return this.result;
  }

  protected Context getContext() {
    return this.context;
  }

  protected IDeriveSMTSort getExpr2Sort() {
    return this.expr2Sort;
  }

  @Override
  public void handle(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    Optional<Sort> sort = this.getExpr2Sort().toSort(this.getContext(), node);
    if (sort.isPresent()) {
      this.getResult().setValue(this.getContext().mkConst(node.getName(), sort.get()));
    } else {
      this.getResult().clear();
    }
  }
}
