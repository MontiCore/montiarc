/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._visitor.ArcBasisDelegatorVisitor;
import arcbasis._visitor.IArcBasisDelegatorVisitor;
import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.AbstractArcTypesCalculator;
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
                                    @NotNull IArcBasisDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IArcBasisDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IArcBasisDelegatorVisitor);
    return (IArcBasisDelegatorVisitor) super.getCalculationDelegator();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTSignedLiteral lit) {
    Preconditions.checkArgument(lit != null);
    throw new UnsupportedOperationException();
  }
}