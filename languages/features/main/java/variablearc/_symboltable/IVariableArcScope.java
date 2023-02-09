/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.*;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IVariableArcScope extends IVariableArcScopeTOP {

  /**
   * Adds {@code VariableArcVariationPoint} to this scope.
   * The variation point should not depend on other variation points as these should be added to the dependent variation points directly.
   *
   * @param variationPoint The variation point which is added.
   */
  default void add(@NotNull VariableArcVariationPoint variationPoint) {
    Preconditions.checkNotNull(variationPoint);
    if (variationPoint.dependsOn.isEmpty())
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

   @Override
  default List<ArcFeatureSymbol> resolveArcFeatureMany(boolean foundSymbols, String name,
                                                       AccessModifier modifier,
                                                       Predicate<ArcFeatureSymbol> predicate) {
    List<ArcFeatureSymbol> symbols = IVariableArcScopeTOP.super.resolveArcFeatureMany(foundSymbols, name, modifier, predicate);
    symbols.addAll(resolveArcFeatureOfParentMany(foundSymbols || symbols.size() > 0, name, modifier, predicate));
    return symbols;
  }

  default List<ArcFeatureSymbol> resolveArcFeatureOfParentMany(boolean foundSymbols, String name,
                                                              AccessModifier modifier,
                                                              Predicate<ArcFeatureSymbol> predicate) {
    if (!foundSymbols && this.isPresentSpanningSymbol()) {
      Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(this.getSpanningSymbol());
      if (component.isPresent() && component.get().isPresentParent()) {
        return ((IVariableArcScope) component.get().getParent().getTypeInfo().getSpannedScope())
            .resolveArcFeatureMany(false, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }

  @Override
  default List<ArcFeatureSymbol> continueArcFeatureWithEnclosingScope(boolean foundSymbols, String name,
                                                                                    AccessModifier modifier,
                                                                                    Predicate<ArcFeatureSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      if (isPresentSpanningSymbol() && new InstanceVisitor().asComponent(this.getSpanningSymbol()).isPresent()) {
        return getEnclosingScope().resolveArcFeatureManyEnclosing(foundSymbols, name, modifier, predicate);
      } else {
        return getEnclosingScope().resolveArcFeatureMany(foundSymbols, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }

  default List<ArcFeatureSymbol> resolveArcFeatureManyEnclosing(boolean foundSymbols, String name,
                                                                              AccessModifier modifier,
                                                                              Predicate<ArcFeatureSymbol> predicate) {
    return continueArcFeatureWithEnclosingScope(foundSymbols, name, modifier, predicate);
  }

  @Override
  default List<VariableSymbol> resolveAdaptedVariableLocallyMany(boolean foundSymbols,
                                                                 String name,
                                                                 AccessModifier modifier,
                                                                 Predicate<VariableSymbol> predicate) {

    List<VariableSymbol> adapters = IVariableArcScopeTOP.super.resolveAdaptedVariableLocallyMany(foundSymbols, name, modifier, predicate);

    List<ArcFeatureSymbol> arcFeatures = resolveArcFeatureLocallyMany(foundSymbols, name, AccessModifier.ALL_INCLUSION, x -> true);

    for (ArcFeatureSymbol feature : arcFeatures) {
      // instantiate the adapter
      VariableSymbol adapter = new ArcFeature2VariableAdapter(feature);

      // filter by modifier and predicate
      if (modifier.includes(adapter.getAccessModifier()) && predicate.test(adapter)) {

        // add the adapter to the result
        adapters.add(adapter);

        // add the adapter to the scope
        this.add(adapter);
      }
    }
    return adapters;
  }
}
