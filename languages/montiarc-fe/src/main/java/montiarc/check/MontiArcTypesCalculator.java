/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.*;
import montiarc.MontiArcMill;
import montiarc._visitor.IMontiArcDelegatorVisitor;
import montiarc.util.check.AbstractArcTypesCalculator;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in MontiArc.
 */
public class MontiArcTypesCalculator
  extends AbstractArcTypesCalculator {

  public MontiArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, MontiArcMill.montiArcDelegatorVisitorBuilder().build());
  }

  protected MontiArcTypesCalculator(@NotNull TypeCheckResult typeCheckResult,
                                    @NotNull IMontiArcDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IMontiArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IMontiArcDelegatorVisitor);
    return (IMontiArcDelegatorVisitor) super.getCalculationDelegator();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(@NotNull ASTSignedLiteral lit) {
    Preconditions.checkArgument(lit != null);
    Preconditions.checkArgument(lit.getEnclosingScope() != null);
    this.reset();
    lit.accept(this.getCalculationDelegator());
    return this.getResult();
  }

  @Override
  public void init() {
    super.init();
    this.initDeriveSymTypeOfMCCommonLiterals();
    this.initDeriveSymTypeOfCommonExpressions();
    this.initDeriveSymTypeOfAssignmentExpressions();
  }

  protected void initDeriveSymTypeOfMCCommonLiterals() {
    DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setMCCommonLiteralsVisitor(deriveSymTypeOfMCCommonLiterals);
  }

  protected void initDeriveSymTypeOfCommonExpressions() {
    DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);
  }

  protected void initDeriveSymTypeOfAssignmentExpressions() {
    DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions =
      new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setAssignmentExpressionsVisitor(deriveSymTypeOfAssignmentExpressions);
  }
}