/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.enumerations.Z3_lbool;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbolBuilder;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class for determining all possible combinations of variation points.
 * Collects all if-statement conditions and passes them to the expression solver.
 */
public class VariationPointSolver {

  protected static final boolean INCLUDE_INCONVERTIBLE_VARIATIONS = true;
  protected final IVariableArcComponentTypeSymbol origin;
  protected final ExpressionSolver expressionSolver;

  public VariationPointSolver(@NotNull IVariableArcComponentTypeSymbol origin) {
    Preconditions.checkNotNull(origin);

    this.origin = origin;
    this.expressionSolver = new ExpressionSolver();
  }

  /**
   * Get all variation point combinations for the origin component
   *
   * @return Set of immutable sets of variation points
   */
  public Set<VariableArcVariantComponentTypeSymbolBuilder> getCombinations(@Nullable VariableArcVariantComponentTypeSymbol parentVariant) {

    Set<VariableArcVariantComponentTypeSymbolBuilder> res = new HashSet<>();
    for (Set<VariableArcVariationPoint> includedVPs : Sets.powerSet(ImmutableSet.copyOf(origin.getAllVariationPoints()))) {
      ExpressionSet expressions = getConditionsForVariationPoints(includedVPs);
      expressions.add(origin.getConstraints());
      if (parentVariant != null) {
        expressions.add(parentVariant.getConditions());
      }

      Optional<Solver> smtSolver = expressionSolver.getSolver(expressions);
      if (smtSolver.map(Solver::check).map(status -> status == Status.SATISFIABLE || (INCLUDE_INCONVERTIBLE_VARIATIONS && status == Status.UNKNOWN)).orElse(INCLUDE_INCONVERTIBLE_VARIATIONS)) {
        HashMap<ArcFeatureSymbol, Boolean> featureSymbolBooleanMap = new HashMap<>();
        Optional<Model> model = smtSolver.map(Solver::getModel);
        for (ArcFeatureSymbol feature : ((IVariableArcScope) origin.getTypeInfo().getSpannedScope()).getLocalArcFeatureSymbols()) {
          featureSymbolBooleanMap.put(feature, model.map(m -> m.getConstInterp(expressionSolver.getContext().mkBoolConst(feature.getName()))).map(v -> v.getBoolValue() == Z3_lbool.Z3_L_TRUE).orElse(true));
        }

        res.add(new VariableArcVariantComponentTypeSymbolBuilder()
          .setTypeSymbol(origin)
          .setIncludedVariationPoints(includedVPs)
          .setConditions(getConditionsForVariationPoints(includedVPs))
          .setFeatureSymbolBooleanMap(featureSymbolBooleanMap)
        );
      }
    }
    return res;
  }

  /**
   * Get all variation point combinations for a subcomponent.
   * The {@param type} has to be a subcomponent of the origin component.
   *
   * @param type                The type of the subcomponent.
   * @param prefix              the "path" to the subcomponent (i.e. child1.comp).
   * @param originConfiguration provided configuration for the origin component.
   * @return List of immutable sets of variation points.
   */
  public List<VariableArcVariantComponentTypeSymbol> getSubComponentVariants(@NotNull IVariableArcComponentTypeSymbol type,
                                                                             @NotNull String prefix,
                                                                             @NotNull Set<VariableArcVariationPoint> originConfiguration,
                                                                             @Nullable VariableArcVariantComponentTypeSymbol originParentVariant) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(originConfiguration);

    List<VariableArcVariantComponentTypeSymbol> res =
      new ArrayList<>(type.getVariableArcVariants());
    res.removeIf(
      variant -> {
        ExpressionSet expressions = variant.getConditions().copyAddPrefix(prefix);
        if (expressions.isEmpty()) return false;
        expressions.add(getConditionsForVariationPoints(originConfiguration));
        expressions.add(origin.getConstraints());
        if (originParentVariant != null) expressions.add(originParentVariant.getConditions());
        return !expressionSolver.solve(expressions).orElse(INCLUDE_INCONVERTIBLE_VARIATIONS);
      });
    return res;
  }

  /**
   * Get all AST Expressions that have to hold for a specific variant.
   * This includes the included variation point conditions as well as the negated excluded variations points.
   *
   * @param includedVPs The selected variationPoints
   * @return The expression set that has to hold for only includedVP to be active
   */
  public ExpressionSet getConditionsForVariationPoints(@NotNull Collection<VariableArcVariationPoint> includedVPs) {
    Preconditions.checkNotNull(includedVPs);

    Set<VariableArcVariationPoint> excludedVPs = new HashSet<>(origin.getAllVariationPoints());
    excludedVPs.removeAll(includedVPs);
    return new ExpressionSet(getConditionsCombined(includedVPs), getConditions(excludedVPs));
  }


  protected List<Expression> getConditionsCombined(@NotNull Collection<VariableArcVariationPoint> variationPoints) {
    return variationPoints
      .stream()
      .flatMap(vp -> vp.getAllConditions().stream())
      .collect(Collectors.toList());
  }

  protected List<List<Expression>> getConditions(@NotNull Collection<VariableArcVariationPoint> variationPoints) {
    return variationPoints
      .stream()
      .map(vp ->
        new ArrayList<>(vp.getAllConditions())
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
