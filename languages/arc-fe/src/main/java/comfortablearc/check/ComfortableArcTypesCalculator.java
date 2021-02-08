/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis.check.AbstractArcTypesCalculator;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcDelegatorVisitor;
import comfortablearc._visitor.ComfortableArcTraverser;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ComfortableArc.
 */
public class ComfortableArcTypesCalculator
  extends AbstractArcTypesCalculator {

  public ComfortableArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, ComfortableArcMill.traverser());
  }

  protected ComfortableArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
                                          @NotNull ComfortableArcTraverser calculationDelegator) {
    super(typeCheckResult, calculationDelegator);
  }

  @Override
  protected ComfortableArcTraverser getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof ComfortableArcTraverser);
    return (ComfortableArcTraverser) super.getCalculationDelegator();
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