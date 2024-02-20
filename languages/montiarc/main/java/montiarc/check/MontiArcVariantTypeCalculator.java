/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
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
    this(init(MontiArcMill.traverser(), variant));
  }

  protected MontiArcVariantTypeCalculator(@NotNull MontiArcTraverser t) {
    super(t);
  }

  protected static MontiArcTraverser init(@NotNull MontiArcTraverser t, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(variant);
    MontiArcTypeCalculator.init(t);
    t.getExpressionsBasisVisitorList().clear();
    initExpressionBasisTypeVisitor(t, variant);
    t.getCommonExpressionsVisitorList().clear();
    initCommonExpressionsTypeVisitor(t, variant);
    return t;
  }

  public static void initExpressionBasisTypeVisitor(@NotNull ExpressionsBasisTraverser traverse, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(traverse);
    Preconditions.checkNotNull(variant);
    ExpressionBasisTypeVisitor visitor = new ExpressionBasisTypeVisitor();
    visitor.setWithinScopeResolver(new VariableArcVariantWithinScopeBasicSymbolsResolver(variant));
    traverse.add4ExpressionsBasis(visitor);
  }

  public static void initCommonExpressionsTypeVisitor(@NotNull CommonExpressionsTraverser t, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(variant);
    MACommonExpressionsTypeVisitor visitor = new MACommonExpressionsTypeVisitor();
    visitor.setWithinTypeBasicSymbolsResolver(new MAOOWithinTypeBasicSymbolsResolver());
    visitor.setWithinScopeResolver(new VariableArcVariantWithinScopeBasicSymbolsResolver(variant));
    t.add4CommonExpressions(visitor);
    t.setCommonExpressionsHandler(visitor);
  }
}
