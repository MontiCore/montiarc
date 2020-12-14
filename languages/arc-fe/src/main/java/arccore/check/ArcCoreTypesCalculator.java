/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis.check.AbstractArcTypesCalculator;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreDelegatorVisitor;
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
 * in ArcCore.
 */
public class ArcCoreTypesCalculator
  extends AbstractArcTypesCalculator {

  public ArcCoreTypesCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, ArcCoreMill.arcCoreDelegatorVisitorBuilder().build());
  }

  protected ArcCoreTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
                                   @NotNull ArcCoreDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected ArcCoreDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof ArcCoreDelegatorVisitor);
    return (ArcCoreDelegatorVisitor) super.getCalculationDelegator();
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