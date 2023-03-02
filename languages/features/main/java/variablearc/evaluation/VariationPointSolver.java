/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._symboltable.ComponentInstanceSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariationPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A class for filtering out {@code VariableArcVariationPoint}s with constant {@code false} conditions.
 */
public class VariationPointSolver {

  private final ComponentInstanceSymbol componentInstanceSymbol;
  private List<VariableArcVariationPoint> variationPointList;

  public VariationPointSolver(ComponentInstanceSymbol typeExprOfVariableComponent) {
    this.componentInstanceSymbol = typeExprOfVariableComponent;
  }

  public List<VariableArcVariationPoint> getVariationPoints() {
    if (this.variationPointList == null) {
      solve();
    }
    return this.variationPointList;
  }

  public void solve() {
    variationPointList = new ArrayList<>();
    List<VariableArcVariationPoint> nextVariationPoints =
      new ArrayList<>(
        ((IVariableArcScope) componentInstanceSymbol.getType().getTypeInfo().getSpannedScope()).getRootVariationPoints());

    if (nextVariationPoints.isEmpty()) return;

    ExpressionSolver solver = new ExpressionSolver(componentInstanceSymbol);
    while (nextVariationPoints.size() > 0) {
      VariableArcVariationPoint vp = nextVariationPoints.get(0);
      Optional<Boolean> res = solver.solve(vp.getCondition());
      if (res.orElse(true)) {
        nextVariationPoints.addAll(vp.getChildVariationPoints());
        variationPointList.add(vp);
      }
      nextVariationPoints.remove(0);
    }
    solver.close();
  }
}
