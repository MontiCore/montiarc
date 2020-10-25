/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arccore.ArcCoreMill;
import arccore._visitor.IArcCoreDelegatorVisitor;
import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.AbstractArcTypesCalculator;
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
                                   @NotNull IArcCoreDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IArcCoreDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IArcCoreDelegatorVisitor);
    return (IArcCoreDelegatorVisitor) super.getCalculationDelegator();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTSignedLiteral lit) {
    Preconditions.checkArgument(lit != null);
    throw new UnsupportedOperationException();
  }
}