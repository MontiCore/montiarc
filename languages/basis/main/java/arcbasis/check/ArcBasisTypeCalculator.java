/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcBasis.
 */
public class ArcBasisTypeCalculator extends AbstractArcTypeCalculator {

  public ArcBasisTypeCalculator() {
    this(init(new TypeCalculator3(ArcBasisMill.traverser(), new Type4Ast(), new InferenceContext4Ast())));
  }
  
  protected ArcBasisTypeCalculator(@NotNull TypeCalculator3 tc) {
    super(tc);
  }

  public static TypeCalculator3 init(@NotNull TypeCalculator3 tc) {
    Preconditions.checkNotNull(tc);
    initExpressionBasisTypeVisitor(tc);
    initMCBasicTypesTypeVisitor(tc);
    // initialize the global delegate
    new MapBasedTypeCheck3(tc.getTypeTraverser(), tc.getType4Ast(), tc.getCtx4Ast())
        .setThisAsDelegate();
    return tc;
  }

  public static void initExpressionBasisTypeVisitor(@NotNull TypeCalculator3 tc) {
    Preconditions.checkNotNull(tc);
    ExpressionsBasisTraverser traverser = (ExpressionsBasisTraverser) tc.getTypeTraverser();
    ExpressionBasisTypeVisitor visitor = new ExpressionBasisTypeVisitor();
    visitor.setType4Ast(tc.getType4Ast());
    visitor.setContext4Ast(tc.getCtx4Ast());
    visitor.setWithinScopeResolver(new ArcBasisWithinScopeBasicSymbolsResolver());
    traverser.add4ExpressionsBasis(visitor);
  }

  public static void initMCBasicTypesTypeVisitor(@NotNull TypeCalculator3 tc) {
    Preconditions.checkNotNull(tc);
    MCBasicTypesTraverser traverser = (MCBasicTypesTraverser) tc.getTypeTraverser();
    MCBasicTypesTypeVisitor visitor = new MCBasicTypesTypeVisitor();
    visitor.setType4Ast(tc.getType4Ast());
    visitor.setContext4Ast(tc.getCtx4Ast());
    visitor.setWithinScopeResolver(new ArcBasisWithinScopeBasicSymbolsResolver());
    visitor.setWithinTypeResolver(new ArcBasisWithinTypeBasicSymbolsResolver());
    traverser.add4MCBasicTypes(visitor);
  }
}
