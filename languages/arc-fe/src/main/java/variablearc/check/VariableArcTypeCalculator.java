/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisTraverser;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in VariableArc.
 */
public class VariableArcTypeCalculator extends AbstractArcTypeCalculator {

  public VariableArcTypeCalculator() {
    this(new TypeCheckResult());
  }

  public VariableArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, VariableArcMill.traverser());
  }

  protected VariableArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult,
                                      @NotNull VariableArcTraverser traverser) {
    super(typeCheckResult, traverser);
    this.init(traverser);
  }

  protected void init(@NotNull VariableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.initDeriveSymTypeOfLiterals(traverser);
    this.initDeriveSymTypeOfExpression(traverser);
  }

  protected void initDeriveSymTypeOfLiterals(@NotNull MCLiteralsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpressionWithPortsAndFeatures();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
  }
}
