/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.ComponentConverter;
import variablearc.evaluation.ExpressionSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A variable version of the {@link ComponentTypeSymbol}
 */
public interface IVariableArcComponentTypeSymbol {

  ComponentTypeSymbol getTypeInfo();

  /**
   * @return a list of all possible variants this component can be
   */
  List<VariableArcVariantComponentTypeSymbol> getVariableArcVariants();

  /**
   * Adds {@code VariableArcVariationPoint} to this component.
   *
   * @param variationPoint The variation point which is added.
   */
  void add(@NotNull VariableArcVariationPoint variationPoint);

  /**
   * @return All constraints that need to hold
   */
  ExpressionSet getConstraints();

  /**
   * Computes the constraints of this component, including those of its parents
   * and (prefixed) subcomponents.
   *
   * @param visited The components on the path from the root to this component,
   *                used to detect cycles. Unchanged after the call returns, such
   *                that multiple subcomponents of the same type are all converted.
   * @return All constraints that need to hold, or no constraints if this
   * component is part of a cycle
   */
  default ExpressionSet getConstraints(@NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(visited);
    if (visited.contains(getTypeInfo())) {
      return new ExpressionSet();
    }
    visited.add(getTypeInfo());

    ExpressionSet conditions = new ComponentConverter().convert(this, visited);
    visited.remove(getTypeInfo());
    return conditions;
  }

  /**
   * @return Local constraints (as in defined by this component) that need to hold
   */
  ExpressionSet getLocalConstraints();

  void setLocalConstraints(@NotNull ExpressionSet constraints);

  List<VariableArcVariationPoint> getAllVariationPoints();

  /**
   * Checks if a {@code ISymbol} is not contained inside a variation point.
   *
   * @param symbol Symbol that gets checked.
   * @return if the {@code symbol} is in root.
   */
  default boolean isRootSymbol(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return getAllVariationPoints().stream().noneMatch(vp -> vp.containsSymbol(symbol))
      && (getTypeInfo().isEmptySuperComponents()
      || getTypeInfo().getSuperComponentsList().stream().allMatch(parent -> ((IVariableArcComponentTypeSymbol) parent.getTypeInfo()).isRootSymbol(symbol)));
  }

  /**
   * Checks if a {@code ISymbol} is contained inside the specified subtrees of variation points.
   *
   * @param variationPoints variation points to include.
   * @param symbol          Symbol that gets checked.
   * @return if the {@code symbol} is in contained in the subtree or a root symbol.
   */
  default boolean variationPointsContainSymbol(@NotNull Set<VariableArcVariationPoint> variationPoints,
                                               @NotNull ISymbol symbol) {
    Preconditions.checkNotNull(variationPoints);
    Preconditions.checkNotNull(symbol);

    return variationPoints.stream().anyMatch(vp -> vp.containsSymbol(symbol)) || isRootSymbol(symbol);
  }
}
