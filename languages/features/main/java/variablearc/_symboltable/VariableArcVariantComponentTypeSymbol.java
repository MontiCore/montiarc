/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariableArcVariantComponentType;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.expressions.Expression;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a configured component type variant. Excludes all symbols not found in this specific variant.
 */
public class VariableArcVariantComponentTypeSymbol extends VariantComponentTypeSymbol {

  protected IVariableArcComponentTypeSymbol typeSymbol;
  protected Set<VariableArcVariationPoint> includedVariationPoints;
  protected ExpressionSet conditions;
  protected Map<SubcomponentSymbol, VariantSubcomponentSymbol> subcomponentMap;

  protected Map<ArcFeatureSymbol, Boolean> featureSymbolBooleanMap;

  /**
   * Creates a new component variant type symbol.
   *
   * @param typeSymbol      The original type symbol this variant is derived of.
   * @param variationPoints The variation points included in this variant (is a subset of all parent variation points).
   * @param subcomponentMap A mapping of component instance symbol to their configured instance variants.
   */
  public VariableArcVariantComponentTypeSymbol(@NotNull IVariableArcComponentTypeSymbol typeSymbol,
                                               @NotNull Set<VariableArcVariationPoint> variationPoints,
                                               @NotNull ExpressionSet conditions,
                                               @NotNull List<CompKindExpression> superComponents,
                                               @NotNull Map<SubcomponentSymbol, VariantSubcomponentSymbol> subcomponentMap,
                                               @NotNull Map<ArcFeatureSymbol, Boolean> featureSymbolBooleanMap) {
    super(typeSymbol.getTypeInfo());

    this.typeSymbol = typeSymbol;
    this.includedVariationPoints = variationPoints;
    this.conditions = conditions;
    this.superComponents = superComponents;
    this.subcomponentMap = subcomponentMap;
    this.featureSymbolBooleanMap = featureSymbolBooleanMap;

    if (this.typeSymbol.getTypeInfo().isPresentAstNode()) {
      // Shadow the AST structure
      this.setAstNode(new ASTVariableArcVariantComponentType(this.typeSymbol.getTypeInfo().getAstNode(), this));
    } else {
      this.setAstNodeAbsent();
    }
  }

  public IVariableArcComponentTypeSymbol getTypeSymbol() {
    return typeSymbol;
  }

  @Override
  public List<SubcomponentSymbol> getSubcomponents() {
    return super.getSubcomponents().stream()
      .map(e -> Optional.ofNullable((SubcomponentSymbol) subcomponentMap.get(e)).orElse(e))
      .collect(Collectors.toList());
  }

  /**
   * @return All conditions that need to hold for this variant to be selected (including subcomponent conditions)
   */
  public ExpressionSet getConditions() {
    ExpressionSet conditions = this.conditions.copy().add(typeSymbol.getConstraints());
    for (VariantSubcomponentSymbol instanceSymbol : subcomponentMap.values()) {
      // Adds the required conditions of subcomponents to this component
      conditions.add(((VariableArcVariantComponentTypeSymbol) instanceSymbol.getType().getTypeInfo()).getConditions()
        .copyAddPrefix(instanceSymbol.getName()));
    }
    return conditions;
  }

  /**
   * @return Local conditions that need to hold for this variant to be selected (excluding subcomponent conditions and constraints)
   */
  public ExpressionSet getLocalConditions() {
    return conditions;
  }

  @Override
  public boolean containsSymbol(@NotNull ISymbol symbol) {
    if (symbol instanceof VariantPortSymbol) {
      symbol = ((VariantPortSymbol) symbol).getOriginal();
    }
    if (symbol instanceof Port2VariableAdapter) {
      symbol = ((Port2VariableAdapter) symbol).getAdaptee();
    }
    ISymbol finalSymbol = symbol;
    return typeSymbol.variationPointsContainSymbol(includedVariationPoints, symbol) ||
      !isEmptySuperComponents() && getSuperComponentsList().stream().anyMatch(parent -> ((VariableArcVariantComponentTypeSymbol) parent.getTypeInfo()).containsSymbol(finalSymbol) &&
        !((VariableArcVariantComponentTypeSymbol) parent.getTypeInfo()).isRootSymbol(finalSymbol));
  }

  public boolean isRootSymbol(ISymbol symbol) {
    return typeSymbol.isRootSymbol(symbol);
  }

  public Set<VariableArcVariationPoint> getIncludedVariationPoints() {
    return includedVariationPoints;
  }

  @Override
  public String toString() {
    return "Variant (" + getIncludedVariationPoints().stream().map(VariableArcVariationPoint::getCondition).map(Expression::print).reduce((a, b) -> a + ", " + b).orElse("") + ")";
  }

  public Map<ArcFeatureSymbol, Boolean> getFeatureSymbolBooleanMap() {
    return featureSymbolBooleanMap;
  }
}
