/* (c) https://github.com/MontiCore/monticore */
package montiarc.util.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import montiarc.util._visitor.IArcDelegatorVisitor;
import org.codehaus.commons.nullanalysis.NotNull;

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
  protected TypeCheckResult typeCheckResult;

  protected AbstractArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
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

  protected TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  protected void setTypeCheckResult(@NotNull TypeCheckResult typeCheckResult) {
    Preconditions.checkArgument(typeCheckResult != null);
    this.typeCheckResult = typeCheckResult;
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    return this.getTypeCheckResult().isPresentCurrentResult() ? Optional.of(this.getTypeCheckResult().getCurrentResult()) :
      Optional.empty();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTExpression exp) {
    Preconditions.checkArgument(exp != null);
    Preconditions.checkArgument(exp.getEnclosingScope() != null);
    this.reset();
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
    this.getTypeCheckResult().setCurrentResultAbsent();
  }

  @Override
  public void init() {
    this.initDeriveSymTypeOfLiterals();
    this.initDeriveSymTypeOfExpression();
  }

  protected void initDeriveSymTypeOfLiterals() {
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression() {
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setExpressionsBasisVisitor(deriveSymTypeOfExpression);
  }
}