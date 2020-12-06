/* (c) https://github.com/MontiCore/monticore */
package montiarc.util.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.LastResult;
import de.monticore.types.check.SymTypeExpression;
import montiarc.util._visitor.IArcDelegatorVisitor;
import org.codehaus.commons.nullanalysis.NotNull;
import de.monticore.types.check.ITypesCalculator;

import java.util.Optional;

/**
 * Abstract implementation of a visitor that calculates a
 * {@link SymTypeExpression} (type) for expressions in language components of
 * the MontiArc language family. Can be extended for other types of expressions
 * by providing a corresponding delegator ({@link IArcDelegatorVisitor}),
 * types calculators {@link ITypesCalculator}, and initializing these for
 * delegation in {@link this#init()}.
 */
public abstract class AbstractArcTypesCalculator implements IArcTypesCalculator {

  protected IArcDelegatorVisitor calculationDelegator;
  protected LastResult typeCheckResult;

  protected AbstractArcTypesCalculator(@NotNull LastResult typeCheckResult,
                                       @NotNull IArcDelegatorVisitor calculationDelegator) {
    Preconditions.checkArgument(typeCheckResult != null);
    Preconditions.checkArgument(calculationDelegator != null);
    this.typeCheckResult = typeCheckResult;
    this.calculationDelegator = calculationDelegator;
    this.init();
  }

  protected IArcDelegatorVisitor getCalculationDelegator() {
    return this.calculationDelegator;
  }

  protected LastResult getTypeCheckResult() {
    return typeCheckResult;
  }

  protected void setTypeCheckResult(@NotNull LastResult typeCheckResult) {
    Preconditions.checkArgument(typeCheckResult != null);
    this.typeCheckResult = typeCheckResult;
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    return this.getTypeCheckResult().isPresentLast() ? Optional.of(this.getTypeCheckResult().getLast()) :
      Optional.empty();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTExpression exp) {
    Preconditions.checkArgument(exp != null);
    Preconditions.checkArgument(exp.getEnclosingScope() != null);
    this.reset();
    this.setScopeExpr(exp.getEnclosingScope());
    exp.accept(this.getCalculationDelegator());
    return this.getResult();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTLiteral lit) {
    Preconditions.checkArgument(lit != null);
    this.reset();
    lit.accept(this.getCalculationDelegator());
    return this.getResult();
  }

  @Override
  public void reset() {
    this.getTypeCheckResult().setLastAbsent();
  }

  @Override
  public void init() {
    this.initDeriveSymTypeOfLiterals();
    this.initDeriveSymTypeOfExpression();
  }

  protected void initDeriveSymTypeOfLiterals() {
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression() {
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setLastResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setExpressionsBasisVisitor(deriveSymTypeOfExpression);
  }

  /**
   * Help method to set the required scope in a {@link DeriveSymTypeOfExpression}
   * visitor. Needed, because the implementation in MC6.1.0 is broken.
   * TODO: Remove once fixed in MontiCore.
   */
  protected void setScopeExpr(@NotNull IExpressionsBasisScope scope) {
    Preconditions.checkArgument(scope != null);
    Preconditions.checkState(this.getCalculationDelegator().getExpressionsBasisVisitor().isPresent());
    Preconditions.checkState(this.getCalculationDelegator().getExpressionsBasisVisitor().get() instanceof DeriveSymTypeOfExpression);
    ((DeriveSymTypeOfExpression) this.getCalculationDelegator().getExpressionsBasisVisitor().get()).setScope(scope);
  }
}