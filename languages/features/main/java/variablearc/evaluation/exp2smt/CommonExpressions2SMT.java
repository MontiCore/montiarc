/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Sort;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.expressions.commonexpressions._ast.ASTDivideExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusPrefixExpression;
import de.monticore.expressions.commonexpressions._ast.ASTModuloExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMultExpression;
import de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class CommonExpressions2SMT implements CommonExpressionsHandler {

  Expr2SMTResult result;
  Context context;
  CommonExpressionsTraverser traverser;
  IDeriveSMTSort expr2Sort;

  public CommonExpressions2SMT(@NotNull Expr2SMTResult result,
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
  public void setTraverser(@NotNull CommonExpressionsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public CommonExpressionsTraverser getTraverser() {
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
  public void handle(@NotNull ASTCallExpression node) {
    Preconditions.checkNotNull(node);
    this.getResult().clear();
  }

  @Override
  public void handle(@NotNull ASTFieldAccessExpression node) {
    Preconditions.checkNotNull(node);
    Optional<Sort> derivedSort = this.getExpr2Sort().toSort(this.getContext(), node);
    if (derivedSort.isPresent()) {
      this.getResult().setValue(
        this.getContext().mkConst(node.getName(), derivedSort.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTMinusPrefixExpression node) {
    Preconditions.checkNotNull(node);
    traverse(node);
    if (this.getResult().getValueAsArith().isPresent()) {
      this.getResult().setValue(this.getContext().mkMul(this.getContext().mkInt(-1), this.getResult().getValueAsArith().get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTBooleanNotExpression node) {
    Preconditions.checkNotNull(node);
    traverse(node);
    if (this.getResult().getValueAsBool().isPresent()) {
      this.getResult().setValue(this.getContext().mkNot(this.getResult().getValueAsBool().get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTLogicalNotExpression node) {
    Preconditions.checkNotNull(node);
    traverse(node);
    if (this.getResult().getValueAsBool().isPresent()) {
      this.getResult().setValue(this.getContext().mkNot(this.getResult().getValueAsBool().get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTMultExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkMul(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTDivideExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkDiv(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTModuloExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkMod(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTPlusExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkAdd(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTMinusExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkSub(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTLessEqualExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkLe(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTGreaterEqualExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkGe(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTLessThanExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkLt(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTGreaterThanExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkGt(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTEqualsExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<Expr<?>> left = this.getResult().getValue();

    node.getRight().accept(getTraverser());
    Optional<Expr<?>> right = this.getResult().getValue();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkEq(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTNotEqualsExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<Expr<?>> left = this.getResult().getValue();

    node.getRight().accept(getTraverser());
    Optional<Expr<?>> right = this.getResult().getValue();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkNot(this.getContext().mkEq(left.get(), right.get())));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTBooleanAndOpExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<BoolExpr> left = this.getResult().getValueAsBool();

    node.getRight().accept(getTraverser());
    Optional<BoolExpr> right = this.getResult().getValueAsBool();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkAnd(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTBooleanOrOpExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<BoolExpr> left = this.getResult().getValueAsBool();

    node.getRight().accept(getTraverser());
    Optional<BoolExpr> right = this.getResult().getValueAsBool();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkOr(left.get(), right.get()));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTConditionalExpression node) {
    Preconditions.checkNotNull(node);
    node.getCondition().accept(getTraverser());
    Optional<BoolExpr> condition = this.getResult().getValueAsBool();
    node.getTrueExpression().accept(getTraverser());
    Optional<Expr<?>> trueExpr = this.getResult().getValue();
    node.getFalseExpression().accept(getTraverser());
    Optional<Expr<?>> falseExpr = this.getResult().getValue();
    if (condition.isPresent() && trueExpr.isPresent() && falseExpr.isPresent()) {
      this.getResult().setValue(this.getContext().mkITE(condition.get(), trueExpr.get(), falseExpr.get()));
    } else {
      this.getResult().clear();
    }
  }
}
