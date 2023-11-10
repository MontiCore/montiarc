/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
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

  default ExpressionSet getConstraints(@NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(visited);
    if (!visited.contains(getTypeInfo())) {
      visited.add(getTypeInfo());
      ExpressionSet conditions = new ComponentConverter().convert(this, visited);
      for (CompKindExpression parent : getTypeInfo().getSuperComponentsList()) {
        conditions.add(((IVariableArcComponentTypeSymbol) parent.getTypeInfo()).getConstraints(visited));
      }
      return conditions;
    }
    return new ExpressionSet();
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
