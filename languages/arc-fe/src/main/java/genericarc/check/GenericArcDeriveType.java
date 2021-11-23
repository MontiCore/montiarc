/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.check.AbstractArcDeriveType;
import arcbasis.check.DeriveSymTypeOfExpressionWithPorts;
import com.google.common.base.Preconditions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in GenericArc.
 */
public class GenericArcDeriveType extends AbstractArcDeriveType {

  public GenericArcDeriveType(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, GenericArcMill.traverser());
  }

  protected GenericArcDeriveType(@NotNull TypeCheckResult typeCheckResult,
                                 @NotNull GenericArcTraverser typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected GenericArcTraverser getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof GenericArcTraverser);
    return (GenericArcTraverser) super.getCalculationDelegator();
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