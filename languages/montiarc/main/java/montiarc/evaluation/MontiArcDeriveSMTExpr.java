/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.visitor.ITraverser;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.exp2smt.*;
import variablearc.evaluation.VariableArcDeriveSMTSort;

import java.util.Optional;

public class MontiArcDeriveSMTExpr implements IDeriveSMTExpr {

  protected final MontiArcTraverser traverser;
  protected final Expr2SMTResult result;
  protected final Context context;
  protected final VariableArcDeriveSMTSort sortDerive;
  protected String prefix;

  public MontiArcDeriveSMTExpr(@NotNull Context context) {
    Preconditions.checkNotNull(context);
    this.context = context;
    this.result = new Expr2SMTResult();
    this.sortDerive = new VariableArcDeriveSMTSort(new MontiArcTypeCalculator());
    this.traverser = MontiArcMill.traverser();

    this.traverser.setExpressionsBasisHandler(new ExpressionsBasis2SMT(this));
    this.traverser.setMCCommonLiteralsHandler(new MCCommonLiterals2SMT(this));
    this.traverser.setCommonExpressionsHandler(new CommonExpressions2SMT(this));
    this.traverser.setBitExpressionsHandler(new BitExpressions2SMT(this));
  }

  @Override
  public Context getContext() {
    return this.context;
  }

  @Override
  public IDeriveSMTSort getSortDerive() {
    return this.sortDerive;
  }

  @Override
  public Expr2SMTResult getResult() {
    return this.result;
  }

  @Override
  public ITraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public String getPrefix() {
    return prefix;
  }

  @Override
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
