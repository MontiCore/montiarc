/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.AbstractArcTypesCalculator;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.IComfortableArcDelegatorVisitor;
import org.codehaus.commons.nullanalysis.NotNull;

import de.monticore.types.check.SymTypeExpression;

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
                                          @NotNull IComfortableArcDelegatorVisitor calculationDelegator) {
    super(typeCheckResult, calculationDelegator);
  }

  @Override
  protected IComfortableArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IComfortableArcDelegatorVisitor);
    return (IComfortableArcDelegatorVisitor) super.getCalculationDelegator();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTSignedLiteral lit) {
    Preconditions.checkArgument(lit != null);
    throw new UnsupportedOperationException();
  }
}