/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.DeriveSymTypeOfExpressionWithPorts;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisTraverser;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in GenericArc.
 */
public class GenericArcTypeCalculator extends AbstractArcTypeCalculator {

  public GenericArcTypeCalculator() {
    this(new TypeCheckResult());
  }

  public GenericArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, GenericArcMill.traverser());
  }

  protected GenericArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult,
                                     @NotNull GenericArcTraverser traverser) {
    super(typeCheckResult, traverser);
    this.init(traverser);
  }

  protected void init(@NotNull GenericArcTraverser traverser) {
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
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpressionWithPorts();
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