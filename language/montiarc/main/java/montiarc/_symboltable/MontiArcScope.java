/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import variablearc._symboltable.VariableArcVariationPoint;

import java.util.ArrayList;
import java.util.List;

public class MontiArcScope extends MontiArcScopeTOP {
  final ArrayList<VariableArcVariationPoint> variationPoints = new ArrayList<>();

  public MontiArcScope() {
    super();
  }

  public MontiArcScope(boolean shadowing) {
    super(shadowing);
  }

  public MontiArcScope(IMontiArcScope enclosingScope) {
    this(enclosingScope, false);
  }

  public MontiArcScope(IMontiArcScope enclosingScope, boolean shadowing) {
    super(enclosingScope, shadowing);
  }

  @Override
  public List<VariableArcVariationPoint> getRootVariationPoints() {
    return variationPoints;
  }
}
