/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._visitor.ArcBasisDelegatorVisitor;
import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcBasis.
 */
public class ArcBasisTypesCalculator
  extends AbstractArcTypesCalculator {

  public ArcBasisTypesCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, new ArcBasisDelegatorVisitor());
  }

  protected ArcBasisTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
                                    @NotNull ArcBasisDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected ArcBasisDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof ArcBasisDelegatorVisitor);
    return (ArcBasisDelegatorVisitor) super.getCalculationDelegator();
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
    this.getCalculationDelegator().setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression() {
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setExpressionsBasisVisitor(deriveSymTypeOfExpression);
  }
}