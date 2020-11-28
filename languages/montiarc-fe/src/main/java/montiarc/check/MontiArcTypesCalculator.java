/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types.check.*;
import montiarc.MontiArcMill;
import montiarc._visitor.IMontiArcDelegatorVisitor;
import montiarc.util.check.AbstractArcTypesCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import de.monticore.types.check.SymTypeExpression;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in MontiArc.
 */
public class MontiArcTypesCalculator
  extends AbstractArcTypesCalculator {

  public MontiArcTypesCalculator(@NotNull LastResult typeCheckResult) {
    this(typeCheckResult, MontiArcMill.montiArcDelegatorVisitorBuilder().build());
  }

  protected MontiArcTypesCalculator(@NotNull LastResult typeCheckResult,
                                    @NotNull IMontiArcDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IMontiArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IMontiArcDelegatorVisitor);
    return (IMontiArcDelegatorVisitor) super.getCalculationDelegator();
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
    deriveSymTypeOfMCCommonLiterals.setResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setMCCommonLiteralsVisitor(deriveSymTypeOfMCCommonLiterals);
  }

  protected void initDeriveSymTypeOfCommonExpressions() {
    DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setLastResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);
  }

  protected void initDeriveSymTypeOfAssignmentExpressions() {
    DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions =
      new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setLastResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setAssignmentExpressionsVisitor(deriveSymTypeOfAssignmentExpressions);
  }

  @Override
  protected void setScopeExpr(@NotNull IExpressionsBasisScope scope) {
    Preconditions.checkArgument(scope != null);
    Preconditions.checkState(this.getCalculationDelegator().getCommonExpressionsVisitor().isPresent());
    Preconditions.checkState(this.getCalculationDelegator().getCommonExpressionsVisitor().get() instanceof DeriveSymTypeOfCommonExpressions);
    Preconditions.checkState(this.getCalculationDelegator().getAssignmentExpressionsVisitor().isPresent());
    Preconditions.checkState(this.getCalculationDelegator().getAssignmentExpressionsVisitor().get() instanceof DeriveSymTypeOfAssignmentExpressions);
    super.setScopeExpr(scope);
    ((DeriveSymTypeOfCommonExpressions) this.getCalculationDelegator().getCommonExpressionsVisitor().get()).setScope(scope);
    ((DeriveSymTypeOfAssignmentExpressions) this.getCalculationDelegator().getAssignmentExpressionsVisitor().get()).setScope(scope);
  }
}