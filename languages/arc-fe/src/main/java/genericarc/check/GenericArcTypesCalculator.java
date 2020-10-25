/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import genericarc.GenericArcMill;
import genericarc._visitor.IGenericArcDelegatorVisitor;
import montiarc.util.check.AbstractArcTypesCalculator;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in GenericArc.
 */
public class GenericArcTypesCalculator
  extends AbstractArcTypesCalculator {

  public GenericArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, GenericArcMill.genericArcDelegatorVisitorBuilder().build());
  }

  protected GenericArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
                                      @NotNull IGenericArcDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IGenericArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IGenericArcDelegatorVisitor);
    return (IGenericArcDelegatorVisitor) super.getCalculationDelegator();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTSignedLiteral lit) {
    Preconditions.checkArgument(lit != null);
    throw new UnsupportedOperationException();
  }
}