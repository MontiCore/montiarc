/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisTraverser;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ComfortableArc.
 */
public class ComfortableArcTypeCalculator extends AbstractArcTypeCalculator {

  public ComfortableArcTypeCalculator() {
    this(new TypeCheckResult());
  }

  public ComfortableArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, ComfortableArcMill.traverser());
  }

  protected ComfortableArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult,
                                         @NotNull ComfortableArcTraverser traverser) {
    super(typeCheckResult, traverser);
    this.init(traverser);
  }

  protected void init(@NotNull ComfortableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.initDeriveSymTypeOfLiterals(traverser);
    this.initDeriveSymTypeOfExpression(traverser);
    this.initSynthesizeSymTypeFromMCBasicTypes(traverser);
  }

  protected void initDeriveSymTypeOfLiterals(@NotNull MCLiteralsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
  }

  protected void initSynthesizeSymTypeFromMCBasicTypes(@NotNull MCBasicTypesTraverser traverser){
    Preconditions.checkNotNull(traverser);
    SynthesizeSymTypeFromMCBasicTypes mCBasicTypesVisitor = new SynthesizeSymTypeFromMCBasicTypes();
    mCBasicTypesVisitor.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCBasicTypes(mCBasicTypesVisitor);
    traverser.setMCBasicTypesHandler(mCBasicTypesVisitor);
  }
}