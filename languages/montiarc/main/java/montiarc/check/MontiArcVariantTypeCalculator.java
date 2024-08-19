/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.VariableArcVariantWithinScopeBasicSymbolsResolver;

/**
 * A variant aware visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in MontiArc.
 */
public class MontiArcVariantTypeCalculator extends MontiArcTypeCalculator {

  public MontiArcVariantTypeCalculator(@NotNull ComponentTypeSymbol variant) {
    this(init(new TypeCalculator3(MontiArcMill.traverser(), new Type4Ast(), new InferenceContext4Ast()), variant));
  }

  protected MontiArcVariantTypeCalculator(@NotNull TypeCalculator3 t) {
    super(t);
  }

  protected static TypeCalculator3 init(@NotNull TypeCalculator3 t, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(variant);
    MontiArcTypeCalculator.init(t);
    MontiArcTraverser traverser = (MontiArcTraverser) t.getTypeTraverser();
    traverser.getExpressionsBasisVisitorList().clear();
    initExpressionBasisTypeVisitor(t, variant);
    traverser.getCommonExpressionsVisitorList().clear();
    initCommonExpressionsTypeVisitor(t, variant);
    // initialize the global delegate
    new MapBasedTypeCheck3(t.getTypeTraverser(), t.getType4Ast(), t.getCtx4Ast())
        .setThisAsDelegate();
    return t;
  }

  public static void initExpressionBasisTypeVisitor(@NotNull TypeCalculator3 t, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(variant);
    ExpressionsBasisTraverser traverser = (ExpressionsBasisTraverser) t.getTypeTraverser();
    ExpressionBasisCTTIVisitor visitor = new ExpressionBasisCTTIVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    visitor.setWithinScopeResolver(new VariableArcVariantWithinScopeBasicSymbolsResolver(variant));
    traverser.add4ExpressionsBasis(visitor);
    traverser.setExpressionsBasisHandler(visitor);
  }

  public static void initCommonExpressionsTypeVisitor(@NotNull TypeCalculator3 t, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(variant);
    CommonExpressionsTraverser traverser = (CommonExpressionsTraverser) t.getTypeTraverser();
    CommonExpressionsCTTIVisitor visitor = new CommonExpressionsCTTIVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    visitor.setWithinTypeBasicSymbolsResolver(new MAOOWithinTypeBasicSymbolsResolver());
    visitor.setWithinScopeResolver(new VariableArcVariantWithinScopeBasicSymbolsResolver(variant));
    traverser.add4CommonExpressions(visitor);
    traverser.setCommonExpressionsHandler(visitor);
  }
}
