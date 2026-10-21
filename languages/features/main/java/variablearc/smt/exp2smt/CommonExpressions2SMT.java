/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
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
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.types.check.CompKindExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;
import variablearc.smt.VariableArcDeriveSMTSort;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommonExpressions2SMT implements CommonExpressionsHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected CommonExpressionsTraverser traverser;

  public CommonExpressions2SMT(@NotNull IDeriveSMTExpr deriveSMTExpr) {
    Preconditions.checkNotNull(deriveSMTExpr);
    this.deriveSMTExpr = deriveSMTExpr;
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
    return this.deriveSMTExpr.getResult();
  }

  protected Context getContext() {
    return this.deriveSMTExpr.getContext();
  }

  protected IDeriveSMTSort getExpr2Sort() {
    return this.deriveSMTExpr.getSortDerive();
  }

  @Override
  public void handle(@NotNull ASTCallExpression node) {
    Preconditions.checkNotNull(node);
    this.deriveSMTExpr.setUnknown(node);
  }

  @Override
  public void handle(@NotNull ASTFieldAccessExpression node) {
    Preconditions.checkNotNull(node);
    getResult().clear();

    IVariableArcScope scope = (IVariableArcScope) node.getEnclosingScope();
    if (node.getExpression() instanceof ASTNameExpression nameExpression) {
      // Handle only features of subcomponents of form x.f; their parameters are not accessible
      String name = nameExpression.getName();
      Optional<SubcomponentSymbol> instanceSymbol = this.resolveSubcomponent(scope, name);
      if (instanceSymbol.isPresent() && instanceSymbol.get().isTypePresent()) {
        ComponentTypeSymbol type = instanceSymbol.get().getType().getTypeInfo();
        if (!((IVariableArcScope) type.getSpannedScope()).resolveArcFeatureMany(node.getName()).isEmpty()) {
          this.getResult().setValue(this.deriveSMTExpr.variable(name + "." + node.getName(), this.getContext().getBoolSort()));
        }
      }
    }
    if (getResult().getValue().isEmpty()) {
      // Handle all other field access expressions of form a.X...X.b.c
      Optional<Sort> sort = this.getExpr2Sort().toSort(node);
      if (sort.isPresent()) {
        if (sort.get() instanceof EnumSort<?> enumSort && VariableArcDeriveSMTSort.indexOf(enumSort, node.getName()) >= 0) {
          // Handle enum constants
          this.getResult().setValue(enumSort.getConst(VariableArcDeriveSMTSort.indexOf(enumSort, node.getName())));
        } else {
          this.deriveSMTExpr.setUnknown(node, sort.get());
        }
      }
    }
  }

  /**
   * @return the subcomponent of the name visible in the scope, including
   * those inherited by the enclosing component type
   */
  protected Optional<SubcomponentSymbol> resolveSubcomponent(@NotNull IVariableArcScope scope, @NotNull String name) {
    Optional<SubcomponentSymbol> local = scope.resolveSubcomponentMany(name).stream().findFirst();
    if (local.isPresent()) {
      return local;
    }
    for (IScope current = scope; current != null; current = current.getEnclosingScope()) {
      if (current.isPresentSpanningSymbol() && current.getSpanningSymbol() instanceof ComponentTypeSymbol component) {
        return this.resolveInheritedSubcomponent(component, name, new HashSet<>());
      }
    }
    return Optional.empty();
  }

  protected Optional<SubcomponentSymbol> resolveInheritedSubcomponent(@NotNull ComponentTypeSymbol component,
                                                                      @NotNull String name,
                                                                      @NotNull Set<String> visited) {
    if (!visited.add(component.getFullName())) {
      return Optional.empty();
    }
    for (CompKindExpression superComponent : component.getSuperComponentsList()) {
      ComponentTypeSymbol type = superComponent.getTypeInfo();
      Optional<SubcomponentSymbol> found = type.getSubcomponents(name)
        .or(() -> this.resolveInheritedSubcomponent(type, name, visited));
      if (found.isPresent()) {
        return found;
      }
    }
    return Optional.empty();
  }

  @Override
  public void handle(@NotNull ASTMinusPrefixExpression node) {
    Preconditions.checkNotNull(node);
    traverse(node);
    if (this.getResult().getValueAsArith().isPresent()) {
      this.getResult()
        .setValue(this.getContext().mkMul(this.getContext().mkInt(-1), this.getResult().getValueAsArith().get()));
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  @Override
  public void handle(@NotNull ASTBooleanNotExpression node) {
    Preconditions.checkNotNull(node);
    traverse(node);
    if (this.getResult().getValueAsBool().isPresent()) {
      this.getResult().setValue(this.getContext().mkNot(this.getResult().getValueAsBool().get()));
    } else {
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
    }
  }

  @Override
  public void handle(@NotNull ASTLogicalNotExpression node) {
    Preconditions.checkNotNull(node);
    traverse(node);
    if (this.getResult().getValueAsBool().isPresent()) {
      this.getResult().setValue(this.getContext().mkNot(this.getResult().getValueAsBool().get()));
    } else {
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  @Override
  public void handle(@NotNull ASTDivideExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<ArithExpr<?>> left = this.getResult().getValueAsArith();

    node.getRight().accept(getTraverser());
    Optional<ArithExpr<?>> right = this.getResult().getValueAsArith();

    if (left.isPresent() && right.isPresent() && left.get() instanceof IntExpr dividend
      && right.get() instanceof IntExpr divisor) {
      this.getResult().setValue(this.truncatedDivision(dividend, divisor));
    } else if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkDiv(left.get(), right.get()));
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  /**
   * @return the quotient rounded towards zero, as in Java
   */
  protected IntExpr truncatedDivision(@NotNull IntExpr dividend, @NotNull IntExpr divisor) {
    Context context = this.getContext();
    IntExpr quotient = (IntExpr) context.mkDiv(this.abs(dividend), this.abs(divisor));
    BoolExpr sameSign = context.mkEq(context.mkGe(dividend, context.mkInt(0)), context.mkGe(divisor, context.mkInt(0)));
    return (IntExpr) context.mkITE(sameSign, quotient, context.mkUnaryMinus(quotient));
  }

  protected IntExpr abs(@NotNull IntExpr value) {
    Context context = this.getContext();
    return (IntExpr) context.mkITE(context.mkGe(value, context.mkInt(0)), value, context.mkUnaryMinus(value));
  }

  @Override
  public void handle(@NotNull ASTModuloExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkSub(left.get(),
        this.getContext().mkMul(right.get(), this.truncatedDivision(left.get(), right.get()))));
    } else {
      this.deriveSMTExpr.setUnknown(node);
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
      this.deriveSMTExpr.setUnknown(node);
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
      this.deriveSMTExpr.setUnknown(node);
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node, this.getContext().getBoolSort());
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
      this.deriveSMTExpr.setUnknown(node);
    }
  }
}
