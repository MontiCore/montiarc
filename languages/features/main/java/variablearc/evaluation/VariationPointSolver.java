/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.check.TypeExprOfVariableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A class for filtering out {@code VariableArcVariationPoint}s with constant {@code false} conditions.
 */
public class VariationPointSolver {
  private final TypeExprOfVariableComponent typeExprOfVariableComponent;
  private List<VariableArcVariationPoint> variationPointList;
  private boolean isIncomplete;

  public VariationPointSolver(TypeExprOfVariableComponent typeExprOfVariableComponent) {
    this.isIncomplete = true;
    this.typeExprOfVariableComponent = typeExprOfVariableComponent;
  }

  public boolean getIsIncomplete() {
    if (this.variationPointList == null)
      solve();
    return this.isIncomplete;
  }

  public List<VariableArcVariationPoint> getVariationPoints() {
    if (this.variationPointList == null)
      solve();
    return this.variationPointList;
  }

  public List<VariableArcVariationPoint> solve() {
    variationPointList = new ArrayList<>();
    List<VariableArcVariationPoint> nextVariationPoints = new ArrayList<>(((IVariableArcScope) typeExprOfVariableComponent.getTypeInfo().getSpannedScope()).getRootVariationPoints());
    isIncomplete = true;
    while (nextVariationPoints.size() > 0) {
      VariableArcVariationPoint vp = nextVariationPoints.get(0);
      Optional<Boolean> res = ExpressionSolver.solve(vp.getCondition(), typeExprOfVariableComponent, VariableArcMill.fullPrettyPrinter()::prettyprint);
      if (res.orElse(true)) {
        nextVariationPoints.addAll(vp.getChildVariationPoints());
        variationPointList.add(vp);
      }
      nextVariationPoints.remove(0);
    }
    return variationPointList;
  }
}
