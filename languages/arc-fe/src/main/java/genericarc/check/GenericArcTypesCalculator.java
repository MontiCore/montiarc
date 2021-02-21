/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.check.AbstractArcTypesCalculator;
import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in GenericArc.
 */
public class GenericArcTypesCalculator
  extends AbstractArcTypesCalculator {

  public GenericArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, GenericArcMill.traverser());
  }

  protected GenericArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
                                      @NotNull GenericArcTraverser typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected GenericArcTraverser getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof GenericArcTraverser);
    return (GenericArcTraverser) super.getCalculationDelegator();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTSignedLiteral lit) {
    Preconditions.checkArgument(lit != null);
    throw new UnsupportedOperationException();
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
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setExpressionsBasisHandler(deriveSymTypeOfExpression);
    this.getCalculationDelegator().add4ExpressionsBasis(deriveSymTypeOfExpression);
  }
}