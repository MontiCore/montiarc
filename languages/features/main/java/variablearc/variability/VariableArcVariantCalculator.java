/* (c) https://github.com/MontiCore/monticore */
package variablearc.variability;

import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Z3Exception;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbolBuilder;
import variablearc._symboltable.VariantSubcomponentSymbol;
import variablearc.evaluation.VariationPointSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

      if (!componentTypeSymbol.getTypeInfo().isEmptySuperComponents() && componentTypeSymbol.getTypeInfo().getSuperComponents(0) != null &&
        componentTypeSymbol.getTypeInfo().getSuperComponents(0).getTypeInfo() instanceof IVariableArcComponentTypeSymbol) {
        for (VariableArcVariantComponentTypeSymbol parentVariant : ((IVariableArcComponentTypeSymbol) componentTypeSymbol.getTypeInfo().getSuperComponents(0).getTypeInfo()).getVariableArcVariants()) {
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
    for (VariableArcVariantComponentTypeSymbolBuilder variantBuilder : vpSolver.getCombinations(parentVariant)) {
      HashMap<SubcomponentSymbol, List<VariableArcVariantComponentTypeSymbol>> subComponentVariants = new HashMap<>();
      // filter out subcomponents not included in this variant
      List<SubcomponentSymbol> subcomponents =
        componentTypeSymbol.getTypeInfo().getSubcomponents().stream()
          .filter(instance -> componentTypeSymbol.variationPointsContainSymbol(variantBuilder.getIncludedVariationPoints(), instance))
          .filter(SubcomponentSymbol::isTypePresent) // for robustness
          .collect(
            Collectors.toList());

      if (subcomponents.isEmpty()) {
        variants.add(variantBuilder
          .setSuperComponents(parentVariant == null ? Collections.emptyList() : Collections.singletonList(componentTypeSymbol.getTypeInfo().getSuperComponents(0).deepClone(parentVariant)))
          .build()
        );
      } else {
        // We need to recalculate the subcomponent variants to see which are still possible in this variant
        for (SubcomponentSymbol instance : subcomponents) {
          ComponentSymbol subcomponentType = instance.getType().getTypeInfo();
          if (subcomponentType instanceof ComponentTypeSymbolSurrogate) {
            subcomponentType = ((ComponentTypeSymbolSurrogate) subcomponentType).lazyLoadDelegate();
          }
          if (subcomponentType instanceof IVariableArcComponentTypeSymbol) {
            IVariableArcComponentTypeSymbol typeSymbol = (IVariableArcComponentTypeSymbol) subcomponentType;
            subComponentVariants.put(instance, vpSolver.getSubComponentVariants(typeSymbol,
              instance.getName(), variantBuilder.getIncludedVariationPoints(), parentVariant));
          }
        }

        // Expand variants by possible subcomponent variants
        expandCombinations(subComponentVariants).forEach(
          e -> variants.add(variantBuilder
            .setSuperComponents(parentVariant == null ? Collections.emptyList() : Collections.singletonList(componentTypeSymbol.getTypeInfo().getSuperComponents(0).deepClone(parentVariant)))
            .setSubcomponentMap(e)
            .build()
          )
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
  protected List<HashMap<SubcomponentSymbol, VariantSubcomponentSymbol>> expandCombinations(
    @NotNull HashMap<SubcomponentSymbol, List<VariableArcVariantComponentTypeSymbol>> subComponentVariants
  ) {
    Preconditions.checkNotNull(subComponentVariants);
    // Base case #1: no subcomponents
    if (subComponentVariants.isEmpty()) return Collections.emptyList();


    SubcomponentSymbol instance = subComponentVariants.keySet().stream().findFirst().get();
    List<VariableArcVariantComponentTypeSymbol> variants = subComponentVariants.remove(instance);
    if (subComponentVariants.isEmpty()) {
      // Base case #2: One subcomponent
      List<HashMap<SubcomponentSymbol, VariantSubcomponentSymbol>> res = new ArrayList<>();
      for (VariableArcVariantComponentTypeSymbol variant : variants) {
        HashMap<SubcomponentSymbol, VariantSubcomponentSymbol> pre = new HashMap<>();
        pre.put(instance, new VariantSubcomponentSymbol(instance, instance.getType().deepClone(variant)));
        res.add(pre);
      }
      return res;
    } else {
      // More than 1 subcomponent
      List<HashMap<SubcomponentSymbol, VariantSubcomponentSymbol>> prev =
        expandCombinations(subComponentVariants);
      List<HashMap<SubcomponentSymbol, VariantSubcomponentSymbol>> res = new ArrayList<>();
      for (VariableArcVariantComponentTypeSymbol variant : variants) {
        for (HashMap<SubcomponentSymbol, VariantSubcomponentSymbol> pre : prev) {
          pre = new HashMap<>(pre);
          pre.put(instance, new VariantSubcomponentSymbol(instance,
            instance.getType().deepClone(variant)));
          res.add(pre);
        }
      }
      return res;
    }
  }
}
