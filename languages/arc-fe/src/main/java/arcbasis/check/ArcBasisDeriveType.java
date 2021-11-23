/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcBasis.
 */
public class ArcBasisDeriveType
  extends AbstractArcDeriveType {

  public ArcBasisDeriveType(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, ArcBasisMill.traverser());
  }

  protected ArcBasisDeriveType(@NotNull TypeCheckResult typeCheckResult,
                               @NotNull ArcBasisTraverser typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected ArcBasisTraverser getCalculationDelegator() {
    Preconditions.checkNotNull(super.getCalculationDelegator());
    return super.getCalculationDelegator();
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
    this.getCalculationDelegator().setExpressionsBasisHandler(deriveSymTypeOfExpression);
    this.getCalculationDelegator().add4ExpressionsBasis(deriveSymTypeOfExpression);
  }
}