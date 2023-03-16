/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.VariationPointSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A variable version of the {@link ComponentTypeSymbol}
 */
public class VariableComponentTypeSymbol extends ComponentTypeSymbol {

  protected List<VariableArcVariationPoint> variationPoints;
  protected List<VariantComponentTypeSymbol> variants;

  /**
   * @param name the name of this component type.
   */
  protected VariableComponentTypeSymbol(String name) {
    super(name);
    this.variationPoints = new ArrayList<>();
  }

  /**
   * @return a list of all possible variants this component can be
   */
  public List<VariantComponentTypeSymbol> getVariants() {
    if (variants == null) {
      calculateVariants();
    }
    return variants;
  }

  protected void calculateVariants() {
    variants = new ArrayList<>();
    VariationPointSolver vpSolver = new VariationPointSolver(this);

    // iterate over all possible variants of this component and expend with subcomponent variants
    for (Set<VariableArcVariationPoint> variationPoints : vpSolver.getCombinations()) {
      HashMap<ComponentInstanceSymbol, List<VariantComponentTypeSymbol>> subComponentVariants = new HashMap<>();
      // filter out subcomponents not included in this variant
      List<ComponentInstanceSymbol> subcomponents =
        getSubComponents().stream()
          .filter(instance -> variationPointsContainSymbol(variationPoints, instance))
          .filter(ComponentInstanceSymbol::isPresentType) // for robustness
          .collect(
            Collectors.toList());

      if (subcomponents.isEmpty()) {
        variants.add(new VariantComponentTypeSymbol(this, variationPoints));
      } else {
        // We need to recalculate the subcomponent variants to see which are still possible in this variant
        for (ComponentInstanceSymbol instance : subcomponents) {
          VariableComponentTypeSymbol typeSymbol = (VariableComponentTypeSymbol) instance.getType().getTypeInfo();
          subComponentVariants.put(instance, vpSolver.getCombinations(typeSymbol,
              instance.getName(), variationPoints).stream().map(l -> new VariantComponentTypeSymbol(typeSymbol, l))
            .collect(Collectors.toList()));
        }

        // Expand variants by possible subcomponent variants
        expandCombinations(subComponentVariants).forEach(
          e -> variants.add(new VariantComponentTypeSymbol(this, variationPoints, e))
        );
      }
    }

    vpSolver.close();
  }

  /**
   * Converts a map instance -> variant list to list of map instance -> variant.
   * I.e. a list of all possible instance variant combinations.
   *
   * @param subComponentVariants maps an instance to all possible variants
   * @return a list of all possible instance variant combinations
   */
  protected List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> expandCombinations(
    @NotNull HashMap<ComponentInstanceSymbol, List<VariantComponentTypeSymbol>> subComponentVariants
  ) {
    Preconditions.checkNotNull(subComponentVariants);
    // Base case #1: no subcomponents
    if (subComponentVariants.size() == 0) return Collections.emptyList();


    ComponentInstanceSymbol instance = subComponentVariants.keySet().stream().findFirst().get();
    List<VariantComponentTypeSymbol> variants = subComponentVariants.remove(instance);
    if (subComponentVariants.size() == 0) {
      // Base case #2: One subcomponent
      List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> res = new ArrayList<>();
      for (VariantComponentTypeSymbol variant : variants) {
        HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol> pre = new HashMap<>();
        pre.put(instance, new VariantComponentInstanceSymbol(instance,
          new VariantCompTypeExpression(variant)));
        res.add(pre);
      }
      return res;
    } else {
      // More than 1 subcomponent
      List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> prev =
        expandCombinations(subComponentVariants);
      List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> res = new ArrayList<>();
      for (VariantComponentTypeSymbol variant : variants) {
        for (HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol> pre : prev) {
          pre = new HashMap<>(pre);
          pre.put(instance, new VariantComponentInstanceSymbol(instance,
            new VariantCompTypeExpression(variant)));
          res.add(pre);
        }
      }
      return res;
    }
  }

  /**
   * Adds {@code VariableArcVariationPoint} to this component.
   *
   * @param variationPoint The variation point which is added.
   */
  public void add(@NotNull VariableArcVariationPoint variationPoint) {
    Preconditions.checkNotNull(variationPoint);
    variationPoints.add(variationPoint);
  }

  public List<VariableArcVariationPoint> getAllVariationPoints() {
    return variationPoints;
  }

  /**
   * Checks if a {@code ISymbol} is not contained inside a variation point.
   *
   * @param symbol Symbol that gets checked.
   * @return if the {@code symbol} is in root.
   */
  public boolean isRootSymbol(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return getAllVariationPoints().stream().noneMatch(vp -> vp.containsSymbol(symbol));
  }

  /**
   * Checks if a {@code ISymbol} is contained inside the specified subtrees of variation points.
   *
   * @param variationPoints variation points to include.
   * @param symbol          Symbol that gets checked.
   * @return if the {@code symbol} is in contained in the subtree or a root symbol.
   */
  protected boolean variationPointsContainSymbol(@NotNull Set<VariableArcVariationPoint> variationPoints,
                                                 @NotNull ISymbol symbol) {
    Preconditions.checkNotNull(variationPoints);
    Preconditions.checkNotNull(symbol);

    return variationPoints.stream().anyMatch(vp -> vp.containsSymbol(symbol)) || isRootSymbol(symbol);
  }
}
