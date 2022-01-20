/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.check.AbstractArcDeriveType;
import com.google.common.base.Preconditions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in VariableArc.
 */
public class VariableArcDeriveType extends AbstractArcDeriveType {
  public VariableArcDeriveType(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, VariableArcMill.traverser());
  }

  protected VariableArcDeriveType(@NotNull TypeCheckResult typeCheckResult, @NotNull VariableArcTraverser typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected VariableArcTraverser getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof VariableArcTraverser);
    return (VariableArcTraverser) super.getCalculationDelegator();
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
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpressionWithPortsAndFeatures();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setExpressionsBasisHandler(deriveSymTypeOfExpression);
    this.getCalculationDelegator().add4ExpressionsBasis(deriveSymTypeOfExpression);
  }
}
