/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.AbstractArcTypesCalculator;
import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.types.check.*;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcDelegatorVisitor;
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
                                    @NotNull MontiArcDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected MontiArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof MontiArcDelegatorVisitor);
    return (MontiArcDelegatorVisitor) super.getCalculationDelegator();
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
    this.initDeriveSymTypeOfLiterals();
    this.initDeriveSymTypeOfExpression();
    this.initDeriveSymTypeOfMCCommonLiterals();
    this.initDeriveSymTypeOfCommonExpressions();
    this.initDeriveSymTypeOfAssignmentExpressions();
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

  protected void initDeriveSymTypeOfMCCommonLiterals() {
    DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(this.getTypeCheckResult());
    this.getCalculationDelegator().setMCCommonLiteralsVisitor(deriveSymTypeOfMCCommonLiterals);
  }

  protected void initDeriveSymTypeOfCommonExpressions() {
    DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new ArcDeriveSymTypeOfCommonExpression();
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