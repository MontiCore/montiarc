/* (c) https://github.com/MontiCore/monticore */
package variablearc.variability;

import arcbasis._symboltable.ComponentInstanceSymbol;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Z3Exception;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariantComponentInstanceSymbol;
import variablearc.evaluation.VariationPointSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * VariableArc variant calculator.
 */
public class VariableArcVariantCalculator implements IVariantCalculator {

  protected final IVariableArcComponentTypeSymbol componentTypeSymbol;

  public VariableArcVariantCalculator(@NotNull IVariableArcComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(componentTypeSymbol);
    this.componentTypeSymbol = componentTypeSymbol;
  }

  public List<VariableArcVariantComponentTypeSymbol> calculateVariants() {
    try {
      List<VariableArcVariantComponentTypeSymbol> variants = new ArrayList<>();
      VariationPointSolver vpSolver = new VariationPointSolver(componentTypeSymbol);

      if (componentTypeSymbol.getTypeInfo().isPresentParent() && componentTypeSymbol.getTypeInfo().getParent() != null &&
        componentTypeSymbol.getTypeInfo().getParent().getTypeInfo() instanceof IVariableArcComponentTypeSymbol) {
        for (VariableArcVariantComponentTypeSymbol parentVariant : ((IVariableArcComponentTypeSymbol) componentTypeSymbol.getTypeInfo().getParent().getTypeInfo()).getVariableArcVariants()) {
          calculateVariableArcVariants(variants, vpSolver, parentVariant);
        }
      } else {
        calculateVariableArcVariants(variants, vpSolver, null);
      }

      vpSolver.close();
      return variants;
    } catch (Z3Exception ignored) {
      return Collections.emptyList();
    }
  }

  protected void calculateVariableArcVariants(@NotNull List<VariableArcVariantComponentTypeSymbol> variants, @NotNull VariationPointSolver vpSolver, @Nullable VariableArcVariantComponentTypeSymbol parentVariant) throws Z3Exception {
    Preconditions.checkNotNull(vpSolver);
    // iterate over all possible variants of this component and expand with subcomponent variants
    for (Set<VariableArcVariationPoint> variationPoints : vpSolver.getCombinations(parentVariant)) {
      HashMap<ComponentInstanceSymbol, List<VariableArcVariantComponentTypeSymbol>> subComponentVariants = new HashMap<>();
      // filter out subcomponents not included in this variant
      List<ComponentInstanceSymbol> subcomponents =
        componentTypeSymbol.getTypeInfo().getSubComponents().stream()
          .filter(instance -> componentTypeSymbol.variationPointsContainSymbol(variationPoints, instance))
          .filter(ComponentInstanceSymbol::isPresentType) // for robustness
          .collect(
            Collectors.toList());

      if (subcomponents.isEmpty()) {
        variants.add(new VariableArcVariantComponentTypeSymbol(componentTypeSymbol, variationPoints,
          vpSolver.getConditionsForVariationPoints(variationPoints),
          parentVariant == null ? null : componentTypeSymbol.getTypeInfo().getParent().deepClone(parentVariant)));
      } else {
        // We need to recalculate the subcomponent variants to see which are still possible in this variant
        for (ComponentInstanceSymbol instance : subcomponents) {
          IVariableArcComponentTypeSymbol typeSymbol = (IVariableArcComponentTypeSymbol) instance.getType().getTypeInfo();
          subComponentVariants.put(instance, vpSolver.getSubComponentVariants(typeSymbol,
            instance.getName(), variationPoints, parentVariant));
        }

        // Expand variants by possible subcomponent variants
        expandCombinations(subComponentVariants).forEach(
          e -> variants.add(new VariableArcVariantComponentTypeSymbol(componentTypeSymbol, variationPoints,
            vpSolver.getConditionsForVariationPoints(variationPoints),
            parentVariant == null ? null : componentTypeSymbol.getTypeInfo().getParent().deepClone(parentVariant), e))
        );
      }
    }
  }

  /**
   * Converts a map instance -> variant list to list of map instance -> variant.
   * I.e. a list of all possible instance variant combinations.
   *
   * @param subComponentVariants maps an instance to all possible variants
   * @return a list of all possible instance variant combinations
   */
  protected List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> expandCombinations(
    @NotNull HashMap<ComponentInstanceSymbol, List<VariableArcVariantComponentTypeSymbol>> subComponentVariants
  ) {
    Preconditions.checkNotNull(subComponentVariants);
    // Base case #1: no subcomponents
    if (subComponentVariants.isEmpty()) return Collections.emptyList();


    ComponentInstanceSymbol instance = subComponentVariants.keySet().stream().findFirst().get();
    List<VariableArcVariantComponentTypeSymbol> variants = subComponentVariants.remove(instance);
    if (subComponentVariants.isEmpty()) {
      // Base case #2: One subcomponent
      List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> res = new ArrayList<>();
      for (VariableArcVariantComponentTypeSymbol variant : variants) {
        HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol> pre = new HashMap<>();
        pre.put(instance, new VariantComponentInstanceSymbol(instance, instance.getType().deepClone(variant)));
        res.add(pre);
      }
      return res;
    } else {
      // More than 1 subcomponent
      List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> prev =
        expandCombinations(subComponentVariants);
      List<HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol>> res = new ArrayList<>();
      for (VariableArcVariantComponentTypeSymbol variant : variants) {
        for (HashMap<ComponentInstanceSymbol, VariantComponentInstanceSymbol> pre : prev) {
          pre = new HashMap<>(pre);
          pre.put(instance, new VariantComponentInstanceSymbol(instance,
            instance.getType().deepClone(variant)));
          res.add(pre);
        }
      }
      return res;
    }
  }
}
