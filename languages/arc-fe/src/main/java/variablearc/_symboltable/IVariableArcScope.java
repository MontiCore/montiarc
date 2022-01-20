/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface IVariableArcScope extends IVariableArcScopeTOP {

  /**
   * Adds {@code VariableArcVariationPoint} to this scope.
   * The variation point should not depend on other variation points as these should be added to the dependent variation points directly.
   *
   * @param variationPoint The variation point which is added.
   */
  default void add(@NotNull VariableArcVariationPoint variationPoint) {
    Preconditions.checkNotNull(variationPoint);
    if (!variationPoint.dependsOn.isPresent())
      this.getRootVariationPoints().add(variationPoint);
  }

  List<VariableArcVariationPoint> getRootVariationPoints();

  default List<VariableArcVariationPoint> getAllVariationPoints() {
    List<VariableArcVariationPoint> variationPoints = new ArrayList<>(getRootVariationPoints());
    List<VariableArcVariationPoint> visitedVariationPoints = new ArrayList<>();
    while (variationPoints.size() > 0) {
      VariableArcVariationPoint variationPoint = variationPoints.get(0);
      visitedVariationPoints.add(variationPoint);
      variationPoints.addAll(variationPoint.getChildVariationPoints());
      variationPoints.remove(0);
    }
    return visitedVariationPoints;
  }

  /**
   * Checks if a {@code ISymbol} is on root level or contained inside a variation point.
   *
   * @param symbol Symbol that gets checked.
   * @return if the {@code symbol} is in root.
   */
  default boolean isRootSymbol(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    List<VariableArcVariationPoint> variationPoints = new ArrayList<>(getRootVariationPoints());
    while (!variationPoints.isEmpty()) {
      if (variationPoints.get(0).containsSymbol(symbol))
        return false;
      variationPoints.addAll(variationPoints.get(0).getChildVariationPoints());
      variationPoints.remove(0);
    }
    return true;
  }

  /**
   * Checks if a {@code ISymbol} is contained inside the specified subtrees of variation points.
   * Traverses the tree up, starting at the nodes given in {@code variationPoints}.
   *
   * @param variationPoints variation points to start from.
   * @param symbol Symbol that gets checked.
   * @return if the {@code symbol} is in root.
   */
  default boolean variationPointsContainsSymbol(@NotNull List<VariableArcVariationPoint> variationPoints,
                                                @NotNull ISymbol symbol) {
    Preconditions.checkNotNull(variationPoints);
    Preconditions.checkNotNull(symbol);

    variationPoints = new ArrayList<>(variationPoints);
    List<VariableArcVariationPoint> visitedVariationPoints = new ArrayList<>();

    while (variationPoints.size() > 0) {
      VariableArcVariationPoint variationPoint = variationPoints.get(0);
      if (variationPoint.containsSymbol(symbol))
        return true;

      visitedVariationPoints.add(variationPoint);
      if (variationPoint.getDependsOn().isPresent()
        && !visitedVariationPoints.contains(variationPoint.getDependsOn().get())
        && !variationPoints.contains(variationPoint.getDependsOn().get())) {
        variationPoints.add(variationPoint.getDependsOn().get());
      }

      variationPoints.remove(variationPoint);
    }
    return this.isRootSymbol(symbol);
  }

  default List<PortSymbol> getLocalPortSymbols(@NotNull List<VariableArcVariationPoint> variationPoints) {
    Preconditions.checkNotNull(variationPoints);
    return getLocalPortSymbols().stream().filter(p -> variationPointsContainsSymbol(variationPoints, p))
      .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Returns a List of {@code PortSymbol} that have the given {@code name}. If {@code variationPoints} is not complete
   * and some conditions are not evaluated, this will return less ports than there might actually be in the final
   * variation instance. It only returns confirmed ports.
   *
   * @param name            Name of the port that we're trying to find.
   * @param variationPoints List of variation points that get included in the search.
   * @return List of Port symbols with given {@code name}.
   */
  default List<PortSymbol> resolvePortMany(@NotNull String name, @NotNull List<VariableArcVariationPoint> variationPoints) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(variationPoints);
    return resolvePortMany(name, (PortSymbol p) -> this.variationPointsContainsSymbol(variationPoints, p));
  }

  /**
   * Returns a {@code PortSymbol} that has the given {@code name}. If {@code variationPoints} is not complete
   * and some conditions are not evaluated, this will return less ports than there might actually be in the final
   * variation instance. It only returns confirmed ports.
   *
   * Throws an error if more than one port symbol is found
   *
   * @param name            Name of the port that we're trying to find.
   * @param variationPoints List of variation points that get included in the search.
   * @return Port symbol with given {@code name}.
   */
  default Optional<PortSymbol> resolvePort(@NotNull String name, @NotNull List<VariableArcVariationPoint> variationPoints) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(variationPoints);
    return getResolvedOrThrowException(resolvePortMany(name, variationPoints));
  }
}
