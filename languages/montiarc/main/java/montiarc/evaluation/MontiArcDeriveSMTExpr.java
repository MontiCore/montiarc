/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import de.monticore.visitor.ITraverser;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.VariableArcDeriveSMTSort;
import variablearc.evaluation.exp2smt.BitExpressions2SMT;
import variablearc.evaluation.exp2smt.CommonExpressions2SMT;
import variablearc.evaluation.exp2smt.Expr2SMTResult;
import variablearc.evaluation.exp2smt.ExpressionsBasis2SMT;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;
import variablearc.evaluation.exp2smt.IDeriveSMTSort;
import variablearc.evaluation.exp2smt.MCCommonLiterals2SMT;

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
    this.prefix = "";

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
  public void setPrefix(@NotNull String prefix) {
    Preconditions.checkNotNull(prefix);
    this.prefix = prefix;
  }
}
