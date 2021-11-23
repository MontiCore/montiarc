/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis.check.AbstractArcDeriveType;
import arcbasis.check.DeriveSymTypeOfExpressionWithPorts;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import com.google.common.base.Preconditions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcCore.
 */
public class ArcCoreDeriveType extends AbstractArcDeriveType {

  public ArcCoreDeriveType(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, ArcCoreMill.traverser());
  }

  protected ArcCoreDeriveType(@NotNull TypeCheckResult typeCheckResult,
                              @NotNull ArcCoreTraverser typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected ArcCoreTraverser getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof ArcCoreTraverser);
    return (ArcCoreTraverser) super.getCalculationDelegator();
  }

  @Override
  public void init() {
    this.initDeriveSymTypeOfLiterals();
    this.initDeriveSymTypeOfExpression();
  }

  protected void initDeriveSymTypeOfLiterals() {
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().add4MCLiteralsBasis(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression() {
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpressionWithPorts();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().add4ExpressionsBasis(deriveSymTypeOfExpression);
    this.getCalculationDelegator().setExpressionsBasisHandler(deriveSymTypeOfExpression);
  }
}