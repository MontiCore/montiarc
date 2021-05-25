/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.AbstractArcDerive;
import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.*;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in MontiArc.
 */
public class MontiArcDerive
  extends AbstractArcDerive {

  public MontiArcDerive(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, MontiArcMill.traverser());
  }

  protected MontiArcDerive(@NotNull TypeCheckResult typeCheckResult,
                           @NotNull MontiArcTraverser typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected MontiArcTraverser getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof MontiArcTraverser);
    return (MontiArcTraverser) super.getCalculationDelegator();
  }

  @Override
  public void init() {
    this.initDeriveSymTypeOfLiterals();
    this.initDeriveSymTypeOfExpression();
    this.initDeriveSymTypeOfMCCommonLiterals();
    this.initDeriveSymTypeOfCommonExpressions();
    this.initDeriveSymTypeOfAssignmentExpressions();
  }
  
  protected void initDeriveSymTypeOfLiterals() {
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().add4MCLiteralsBasis(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression() {
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().add4ExpressionsBasis(deriveSymTypeOfExpression);
    this.getCalculationDelegator().setExpressionsBasisHandler(deriveSymTypeOfExpression);
  }

  protected void initDeriveSymTypeOfMCCommonLiterals() {
    DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
  }

  protected void initDeriveSymTypeOfCommonExpressions() {
    DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new ArcDeriveSymTypeOfCommonExpression();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    this.getCalculationDelegator().setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
  }

  protected void initDeriveSymTypeOfAssignmentExpressions() {
    DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions =
      new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().add4AssignmentExpressions(deriveSymTypeOfAssignmentExpressions);
    this.getCalculationDelegator().setAssignmentExpressionsHandler(deriveSymTypeOfAssignmentExpressions);
  }
}