/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.variability.VariableArcVariantCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class VariableArcComponentTypeSymbol extends ComponentTypeSymbol implements IVariableArcComponentTypeSymbol {

  protected ExpressionSet localConstraints;
  protected ExpressionSet constraints;
  protected List<VariableArcVariantComponentTypeSymbol> variants;
  protected List<VariableArcVariationPoint> variationPoints;

  /**
   * @param name the name of this component type.
   */
  protected VariableArcComponentTypeSymbol(String name) {
    super(name);
    variationPoints = new ArrayList<>();
    localConstraints = new ExpressionSet();
  }

  @Override
  public ComponentTypeSymbol getTypeInfo() {
    return this;
  }

  @Override
  public List<VariableArcVariantComponentTypeSymbol> getVariableArcVariants() {
    if (variants == null) {
      variants = Collections.emptyList();
      VariableArcVariantCalculator calculator = new VariableArcVariantCalculator(this);
      variants = calculator.calculateVariants();
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
