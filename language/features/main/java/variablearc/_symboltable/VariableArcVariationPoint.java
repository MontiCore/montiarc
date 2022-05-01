/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ISymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a variable point in a Component. Variations are structured as a tree.
 * The {@code condition} has to be fulfilled for the {@code symbols} to be added to the component.
 */
public class VariableArcVariationPoint {

  protected ASTExpression condition;

  protected Optional<VariableArcVariationPoint> dependsOn;

  protected List<ISymbol> symbols;

  protected List<VariableArcVariationPoint> childVariationPoints;

  public VariableArcVariationPoint(ASTExpression condition) {
    this(condition, Optional.empty());
  }

  public VariableArcVariationPoint(ASTExpression condition, Optional<VariableArcVariationPoint> dependsOn) {
    this.condition = condition;
    this.dependsOn = dependsOn;
    this.symbols = new ArrayList<>();
    this.childVariationPoints = new ArrayList<>();
    dependsOn.ifPresent(variationPoint -> variationPoint.addChild(this));
  }

  public Optional<VariableArcVariationPoint> getDependsOn() {
    return dependsOn;
  }

  public List<VariableArcVariationPoint> getChildVariationPoints() {
    return childVariationPoints;
  }

  public ASTExpression getCondition() {
    return condition;
  }

  public boolean containsSymbol(ISymbol symbol) {
    return symbols.contains(symbol);
  }

  public void addChild(VariableArcVariationPoint variationPoint) {
    this.childVariationPoints.add(variationPoint);
  }

  public void add(ISymbol symbol) {
    this.symbols.add(symbol);
  }

  public boolean deepEquals(Object o) {
    VariableArcVariationPoint variationPoint;
    if ((o instanceof VariableArcVariationPoint)) {
      variationPoint = (VariableArcVariationPoint) o;
    } else {
      return false;
    }

    if (this.condition != variationPoint.condition && (this.condition == null
      || !this.condition.deepEquals(variationPoint.condition)))
      return false;
    if (!this.symbols.equals(variationPoint.symbols))
      return false;
    if (this.getChildVariationPoints().size() != variationPoint.getChildVariationPoints().size())
      return false;
    for (int i = 0; i < this.getChildVariationPoints().size(); i++) {
      int finalI = i;
      if (variationPoint.getChildVariationPoints().stream().noneMatch((e)
        -> this.getChildVariationPoints().get(finalI).deepEquals(e)))
        return false;
    }

    return true;
  }
}
