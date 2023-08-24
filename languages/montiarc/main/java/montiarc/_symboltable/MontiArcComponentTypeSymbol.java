/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import variablearc._symboltable.ArcFeature2VariableAdapter;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.variability.VariableArcVariantCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MontiArcComponentTypeSymbol extends ComponentTypeSymbol implements IVariableArcComponentTypeSymbol {

  protected ExpressionSet localConstraints;
  protected ExpressionSet constraints;
  protected List<VariableArcVariantComponentTypeSymbol> variants;
  protected List<VariableArcVariationPoint> variationPoints;

  /**
   * @param name the name of this component type.
   */
  protected MontiArcComponentTypeSymbol(String name) {
    super(name);
    variationPoints = new ArrayList<>();
    localConstraints = new ExpressionSet();
  }

  /**
   * Extension point to implement different variant generators.
   * @return Variants of this component
   */
  public List<? extends ComponentTypeSymbol> getVariants() {
    return getVariableArcVariants();
  }

  @Override
  public ComponentTypeSymbol getTypeInfo() {
    return this;
  }

  @Override
  public List<VariableArcVariantComponentTypeSymbol> getVariableArcVariants() {
    if (variants == null) {
      variants = Collections.emptyList();
      variants = new VariableArcVariantCalculator(this).calculateVariants();
    }
    return variants;
  }

  @Override
  public void add(VariableArcVariationPoint variationPoint) {
    variationPoints.add(variationPoint);
  }

  @Override
  public ExpressionSet getConstraints() {
    if (constraints == null) {
      constraints = getConstraints(new HashSet<>());
    }
    return constraints;
  }

  @Override
  public ExpressionSet getLocalConstraints() {
    return localConstraints;
  }

  @Override
  public void setLocalConstraints(ExpressionSet constraints) {
    this.localConstraints = constraints;
  }

  @Override
  public List<VariableArcVariationPoint> getAllVariationPoints() {
    return variationPoints;
  }

  @Override
  public List<VariableSymbol> getFields() {
    return super.getFields().stream()
      .filter(f -> !(f instanceof ArcFeature2VariableAdapter))
      .collect(Collectors.toList());
  }
}
