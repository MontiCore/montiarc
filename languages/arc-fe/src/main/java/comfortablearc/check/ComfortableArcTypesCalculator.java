/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis.check.AbstractArcTypesCalculator;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcDelegatorVisitor;
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
    this(typeCheckResult, ComfortableArcMill.comfortableArcDelegatorVisitorBuilder().build());
  }

  protected ComfortableArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
                                          @NotNull ComfortableArcDelegatorVisitor calculationDelegator) {
    super(typeCheckResult, calculationDelegator);
  }

  @Override
  protected ComfortableArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof ComfortableArcDelegatorVisitor);
    return (ComfortableArcDelegatorVisitor) super.getCalculationDelegator();
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