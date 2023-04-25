/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariableComponentTypeSymbol;
import variablearc._symboltable.VariantComponentTypeSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class for determining all possible combinations of variation points.
 * Collects all if-statement conditions and passes them to the expression solver.
 */
public class VariationPointSolver {

  protected static final boolean INCLUDE_INCONVERTIBLE_VARIATIONS = true;
  protected final VariableComponentTypeSymbol origin;
  protected final ExpressionSolver expressionSolver;

  public VariationPointSolver(@NotNull VariableComponentTypeSymbol origin) {
    Preconditions.checkNotNull(origin);

    this.origin = origin;
    this.expressionSolver = new ExpressionSolver(origin);
  }

  /**
   * Get all variation point combinations for the origin component
   *
   * @return Set of immutable sets of variation points
   */
  public Set<Set<VariableArcVariationPoint>> getCombinations() {

    Set<Set<VariableArcVariationPoint>> res =
      new HashSet<>(Sets.powerSet(ImmutableSet.copyOf(origin.getAllVariationPoints())));
    res.removeIf(
      includedVPs -> !expressionSolver.solve(getConditionsForVariationPoints(origin, includedVPs,
          expressionSolver.defaultPrefix))
        .orElse(INCLUDE_INCONVERTIBLE_VARIATIONS));
    return res;
  }

  /**
   * Get all variation point combinations for a subcomponent.
   * The {@param type} has to be a subcomponent of the origin component.
   *
   * @param type                The type of the subcomponent.
   * @param prefix              the "path" to the subcomponent (i.e. child1.comp).
   * @param originConfiguration provided configuration for the origin component.
   * @return Set of immutable sets of variation points.
   */
  public List<VariantComponentTypeSymbol> getSubComponentVariants(@NotNull VariableComponentTypeSymbol type,
                                                                  @NotNull String prefix,
                                                                  @NotNull Set<VariableArcVariationPoint> originConfiguration) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(originConfiguration);

    List<VariantComponentTypeSymbol> res =
      new ArrayList<>(type.getVariants());
    res.removeIf(
      variant -> {
        ExpressionSet expressions = variant.getConditions().copyWithAddPrefix(prefix);
        expressions.add(getConditionsForVariationPoints(origin, originConfiguration, null));
        return !expressionSolver.solve(expressions).orElse(INCLUDE_INCONVERTIBLE_VARIATIONS);
      });
    return res;
  }

  /**
   * Get all AST Expressions that have to hold for a specific variant.
   * This includes the included variation point conditions as well as the negated excluded variations points.
   *
   * @param type        The type of the component
   * @param includedVPs The selected variationPoints
   * @return The expression set that has to hold for only includedVP to be active
   */
  public ExpressionSet getConditionsForVariationPoints(@NotNull VariableComponentTypeSymbol type,
                                                       @NotNull Collection<VariableArcVariationPoint> includedVPs) {
    return getConditionsForVariationPoints(type, includedVPs, null);
  }

  /**
   * Get all AST Expressions that have to hold for a specific variant.
   * This includes the included variation point conditions as well as the negated excluded variations points.
   *
   * @param type        The type of the component
   * @param includedVPs The selected variationPoints
   * @return The expression set that has to hold for only includedVP to be active
   */
  public ExpressionSet getConditionsForVariationPoints(@NotNull VariableComponentTypeSymbol type,
                                                       @NotNull Collection<VariableArcVariationPoint> includedVPs,
                                                       @Nullable String prefix) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(includedVPs);

    Set<VariableArcVariationPoint> excludedVPs = new HashSet<>(type.getAllVariationPoints());
    excludedVPs.removeAll(includedVPs);
    return new ExpressionSet(getConditionsCombined(includedVPs, prefix), getConditions(excludedVPs, prefix));
  }


  protected List<Expression> getConditionsCombined(@NotNull Collection<VariableArcVariationPoint> variationPoints,
                                                   @Nullable String prefix) {
    return variationPoints
      .stream()
      .flatMap(vp -> vp.getAllConditions().stream()).map(e -> e.copyWithPrefix(prefix))
      .collect(Collectors.toList());
  }

  protected List<List<Expression>> getConditions(@NotNull Collection<VariableArcVariationPoint> variationPoints,
                                                 @Nullable String prefix) {
    return variationPoints
      .stream()
      .map(vp ->
        vp.getAllConditions()
          .stream()
          .map(e -> e.copyWithPrefix(prefix))
          .collect(Collectors.toList())
      )
      .collect(Collectors.toList());
  }

  /**
   * Disposes of the solver
   */
  public void close() {
    expressionSolver.close();
  }
}
