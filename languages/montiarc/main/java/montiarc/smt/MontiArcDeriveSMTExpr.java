/* (c) https://github.com/MontiCore/monticore */
package montiarc.smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.visitor.ITraverser;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.smt.VariableArcDeriveSMTSort;
import variablearc.smt.exp2smt.AssignmentExpressions2SMT;
import variablearc.smt.exp2smt.BitExpressions2SMT;
import variablearc.smt.exp2smt.CommonExpressions2SMT;
import variablearc.smt.exp2smt.Expr2SMTResult;
import variablearc.smt.exp2smt.ExpressionsBasis2SMT;
import variablearc.smt.exp2smt.IDeriveSMTExpr;
import variablearc.smt.exp2smt.IDeriveSMTSort;
import variablearc.smt.exp2smt.MCCommonLiterals2SMT;

import java.util.IdentityHashMap;
import java.util.Map;

public class MontiArcDeriveSMTExpr implements IDeriveSMTExpr {

  protected final MontiArcTraverser traverser;
  protected final Expr2SMTResult result;
  protected final Context context;
  protected final VariableArcDeriveSMTSort sortDerive;
  protected final Map<ASTExpression, String> unknownNames = new IdentityHashMap<>();

  public MontiArcDeriveSMTExpr(@NotNull Context context) {
    Preconditions.checkNotNull(context);
    this.context = context;
    this.result = new Expr2SMTResult();
    this.sortDerive = new VariableArcDeriveSMTSort(context);
    this.traverser = MontiArcMill.traverser();

    this.traverser.setExpressionsBasisHandler(new ExpressionsBasis2SMT(this));
    this.traverser.setMCCommonLiteralsHandler(new MCCommonLiterals2SMT(this));
    this.traverser.setCommonExpressionsHandler(new CommonExpressions2SMT(this));
    this.traverser.setBitExpressionsHandler(new BitExpressions2SMT(this));
    this.traverser.setAssignmentExpressionsHandler(new AssignmentExpressions2SMT(this));
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
  public String unknownName(@NotNull ASTExpression node) {
    Preconditions.checkNotNull(node);
    return this.unknownNames.computeIfAbsent(node, n -> UNKNOWN_MARKER + this.unknownNames.size() + "@"
      + n.get_SourcePositionStart() + "-" + n.get_SourcePositionEnd());
  }
}
