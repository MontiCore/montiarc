/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.Port2VariableAdapter;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Z3Exception;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.evaluation.ComponentConverter;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.VariationPointSolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A variable version of the {@link ComponentTypeSymbol}
 */
public class VariableComponentTypeSymbol extends ComponentTypeSymbol {

  protected List<VariableArcVariationPoint> variationPoints;
  protected List<VariantComponentTypeSymbol> variants;
  protected ExpressionSet conditions;

  protected ExpressionSet constraints;

  /**
   * @param name the name of this component type.
   */
  protected VariableComponentTypeSymbol(@NotNull String name) {
    super(name);
    this.variationPoints = new ArrayList<>();
    this.constraints = new ExpressionSet();
  }

  protected void setVariants(@NotNull List<VariantComponentTypeSymbol> variants) {
    Preconditions.checkNotNull(variants);
    this.variants = variants;
  }

  /**
   * @return a list of all possible variants this component can be
   */
  public List<VariantComponentTypeSymbol> getVariants() {
    if (variants == null) {
      try {
        calculateVariants();
      } catch (Z3Exception ignored) {
        variants = Collections.emptyList();
      }
    }
    return variants;
  }

  protected void calculateVariants() throws Z3Exception {
    variants = new ArrayList<>();
    VariationPointSolver vpSolver = new VariationPointSolver(this);

    if (isPresentParent() && this.getParent().getTypeInfo() != null &&
      this.getParent().getTypeInfo() instanceof VariableComponentTypeSymbol) {
      for (VariantComponentTypeSymbol parentVariant : ((VariableComponentTypeSymbol) this.getParent()
        .getTypeInfo()).getVariants()) {
        calculateVariants(vpSolver, parentVariant);
      }
    } else {
      calculateVariants(vpSolver, null);
    }

    vpSolver.close();
  }

  protected void calculateVariants(@NotNull VariationPointSolver vpSolver, @Nullable VariantComponentTypeSymbol parentVariant) {
    Preconditions.checkNotNull(vpSolver);
    // iterate over all possible variants of this component and expand with subcomponent variants
    for (Set<VariableArcVariationPoint> variationPoints : vpSolver.getCombinations(parentVariant)) {
      HashMap<ComponentInstanceSymbol, List<VariantComponentTypeSymbol>> subComponentVariants = new HashMap<>();
      // filter out subcomponents not included in this variant
      List<ComponentInstanceSymbol> subcomponents =
        getSubComponents().stream()
          .filter(instance -> variationPointsContainSymbol(variationPoints, instance))
          .filter(ComponentInstanceSymbol::isPresentType) // for robustness
          .collect(
            Collectors.toList());

      if (subcomponents.isEmpty()) {
        variants.add(new VariantComponentTypeSymbol(this, variationPoints,
          vpSolver.getConditionsForVariationPoints(variationPoints),
          parentVariant == null ? null : getParent().deepClone(parentVariant)));
      } else {
        // We need to recalculate the subcomponent variants to see which are still possible in this variant
        for (ComponentInstanceSymbol instance : subcomponents) {
          VariableComponentTypeSymbol typeSymbol = (VariableComponentTypeSymbol) instance.getType().getTypeInfo();
          subComponentVariants.put(instance, vpSolver.getSubComponentVariants(typeSymbol,
            instance.getName(), variationPoints, parentVariant));
        }

        // Expand variants by possible subcomponent variants
        expandCombinations(subComponentVariants).forEach(
          e -> variants.add(new VariantComponentTypeSymbol(this, variationPoints,
            vpSolver.getConditionsForVariationPoints(variationPoints),
            parentVariant == null ? null : getParent().deepClone(parentVariant), e))
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
        pre.put(instance, new VariantComponentInstanceSymbol(instance, instance.getType().deepClone(variant)));
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
            instance.getType().deepClone(variant)));
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

  /**
   * @return All conditions (i.e. constraints) that need to hold
   */
  public ExpressionSet getConditions() {
    if (conditions == null) {
      return getConditions(new HashSet<>());
    }
    return conditions;
  }

  public ExpressionSet getConditions(@NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(visited);
    if (conditions == null) {
      conditions = new ComponentConverter().convert(this, visited);
      if (isPresentParent() && this.getParent().getTypeInfo() != null &&
        this.getParent().getTypeInfo() instanceof VariableComponentTypeSymbol) {
        conditions.add(((VariableComponentTypeSymbol) this.getParent().getTypeInfo()).getConditions(visited));
      }
    }
    return conditions;
  }

  public ExpressionSet getConstraints() {
    return constraints;
  }

  public void setConstraints(@NotNull ExpressionSet constraints) {
    Preconditions.checkNotNull(constraints);
    this.constraints = constraints;
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
    return getAllVariationPoints().stream().noneMatch(vp -> vp.containsSymbol(symbol)) && (!isPresentParent() || ((VariableComponentTypeSymbol) getParent().getTypeInfo()).isRootSymbol(symbol));
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

  @Override
  public List<VariableSymbol> getFields() {
    return super.getFields().stream()
      .filter(f -> !(f instanceof ArcFeature2VariableAdapter))
      .collect(Collectors.toList());
  }
}
