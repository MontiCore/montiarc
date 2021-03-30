/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.types.check.*;
import montiarc.util.check.IArcDerive;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Abstract implementation of a visitor that calculates a
 * {@link SymTypeExpression} (type) for expressions in language components of
 * the MontiArc language family. Can be extended for other types of expressions
 * by providing a corresponding traverser ({@link ArcBasisTraverser}),
 * types calculators {@link IDerive}, and initializing these for
 * delegation in {@link this#init()}.
 */
public abstract class AbstractArcDerive implements IArcDerive {

  protected ArcBasisTraverser calculationDelegator;
  protected TypeCheckResult typeCheckResult;

  protected AbstractArcDerive(@NotNull TypeCheckResult typeCheckResult,
                              @NotNull ArcBasisTraverser calculationDelegator) {
    Preconditions.checkArgument(typeCheckResult != null);
    Preconditions.checkArgument(calculationDelegator != null);
    this.typeCheckResult = typeCheckResult;
    this.calculationDelegator = calculationDelegator;
    this.init();
  }

  protected ArcBasisTraverser getCalculationDelegator() {
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
  public void reset() {
    this.getTypeCheckResult().setCurrentResultAbsent();
  }
  
  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return getCalculationDelegator();
  }
}