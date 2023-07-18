/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcBasis.
 */
public class ArcBasisTypeCalculator extends AbstractArcTypeCalculator {

  public ArcBasisTypeCalculator() {
    this(init(ArcBasisMill.traverser()));
  }
  
  protected ArcBasisTypeCalculator(@NotNull ArcBasisTraverser traverse) {
    super(traverse);
  }

  public static ArcBasisTraverser init(@NotNull ArcBasisTraverser traverse) {
    Preconditions.checkNotNull(traverse);
    initExpressionBasisTypeVisitor(traverse);
    initMCBasicTypesTypeVisitor(traverse);
    return traverse;
  }

  public static void initExpressionBasisTypeVisitor(@NotNull ExpressionsBasisTraverser traverse) {
    Preconditions.checkNotNull(traverse);
    ExpressionBasisTypeVisitor visitor = new ExpressionBasisTypeVisitor();
    traverse.add4ExpressionsBasis(visitor);
  }

  public static void initMCBasicTypesTypeVisitor(@NotNull MCBasicTypesTraverser traverse) {
    Preconditions.checkNotNull(traverse);
    MCBasicTypesTypeVisitor visitor = new MCBasicTypesTypeVisitor();
    traverse.add4MCBasicTypes(visitor);
  }
}
