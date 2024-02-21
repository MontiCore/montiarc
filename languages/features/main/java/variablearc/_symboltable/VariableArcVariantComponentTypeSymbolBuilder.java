/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import variablearc.evaluation.ExpressionSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariableArcVariantComponentTypeSymbolBuilder {

  protected VariableArcVariantComponentTypeSymbolBuilder realBuilder;

  protected IVariableArcComponentTypeSymbol typeSymbol;
  protected Set<VariableArcVariationPoint> includedVariationPoints;
  protected ExpressionSet conditions;
  protected Map<SubcomponentSymbol, VariantSubcomponentSymbol> subcomponentMap;

  protected Map<ArcFeatureSymbol, Boolean> featureSymbolBooleanMap;

  protected List<CompKindExpression> superComponents;

  public VariableArcVariantComponentTypeSymbolBuilder() {
    this.realBuilder = this;
    this.superComponents = Collections.emptyList();
    this.subcomponentMap = Collections.emptyMap();
    this.featureSymbolBooleanMap = Collections.emptyMap();
  }

  public VariableArcVariantComponentTypeSymbolBuilder setTypeSymbol(IVariableArcComponentTypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
    return this.realBuilder;
  }

  public VariableArcVariantComponentTypeSymbolBuilder setIncludedVariationPoints(Set<VariableArcVariationPoint> includedVariationPoints) {
    this.includedVariationPoints = includedVariationPoints;
    return this.realBuilder;
  }

  public VariableArcVariantComponentTypeSymbolBuilder setConditions(ExpressionSet conditions) {
    this.conditions = conditions;
    return this.realBuilder;
  }

  public VariableArcVariantComponentTypeSymbolBuilder setSubcomponentMap(Map<SubcomponentSymbol, VariantSubcomponentSymbol> subcomponentMap) {
    this.subcomponentMap = subcomponentMap;
    return this.realBuilder;
  }

  public VariableArcVariantComponentTypeSymbolBuilder setFeatureSymbolBooleanMap(Map<ArcFeatureSymbol, Boolean> featureSymbolBooleanMap) {
    this.featureSymbolBooleanMap = featureSymbolBooleanMap;
    return this.realBuilder;
  }

  public VariableArcVariantComponentTypeSymbolBuilder setSuperComponents(List<CompKindExpression> superComponents) {
    this.superComponents = superComponents;
    return this.realBuilder;
  }

  public IVariableArcComponentTypeSymbol getTypeSymbol() {
    return typeSymbol;
  }

  public Set<VariableArcVariationPoint> getIncludedVariationPoints() {
    return includedVariationPoints;
  }

  public ExpressionSet getConditions() {
    return conditions;
  }

  public Map<SubcomponentSymbol, VariantSubcomponentSymbol> getSubcomponentMap() {
    return subcomponentMap;
  }

  public Map<ArcFeatureSymbol, Boolean> getFeatureSymbolBooleanMap() {
    return featureSymbolBooleanMap;
  }

  public List<CompKindExpression> getSuperComponents() {
    return superComponents;
  }

  public VariableArcVariantComponentTypeSymbol build() {
    return new VariableArcVariantComponentTypeSymbol(this.typeSymbol, this.includedVariationPoints, this.conditions.copy(), this.superComponents, this.subcomponentMap, this.featureSymbolBooleanMap);
  }
}
