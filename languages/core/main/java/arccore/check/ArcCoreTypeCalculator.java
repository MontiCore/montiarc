/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisTraverser;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcCore.
 */
public class ArcCoreTypeCalculator extends AbstractArcTypeCalculator {

  public ArcCoreTypeCalculator() {
    this(new TypeCheckResult());
  }

  public ArcCoreTypeCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, ArcCoreMill.traverser());
  }

  protected ArcCoreTypeCalculator(@NotNull TypeCheckResult typeCheckResult,
                                  @NotNull ArcCoreTraverser traverser) {
    super(typeCheckResult, traverser);
    this.init(traverser);
  }

  protected void init(@NotNull ArcCoreTraverser traverser) {
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
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
  }

  protected void initSynthesizeSymTypeFromMCBasicTypes(@NotNull MCBasicTypesTraverser traverser){
    Preconditions.checkNotNull(traverser);
    SynthesizeSymTypeFromMCBasicTypes mCBasicTypesVisitor = new SynthesizeSymTypeFromMCBasicTypes();
    mCBasicTypesVisitor.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCBasicTypes(mCBasicTypesVisitor);
    traverser.setMCBasicTypesHandler(mCBasicTypesVisitor);
  }
}