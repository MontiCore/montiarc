/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import java.util.ArrayList;
import java.util.List;

public class VariableArcScope extends VariableArcScopeTOP {
  final ArrayList<VariableArcVariationPoint> variationPoints = new ArrayList<>();

  public VariableArcScope() {
    super();
  }

  public VariableArcScope(boolean shadowing) {
    super(shadowing);
  }

  public VariableArcScope(IVariableArcScope enclosingScope) {
    this(enclosingScope, false);
  }

  public VariableArcScope(IVariableArcScope enclosingScope, boolean shadowing) {
    super(enclosingScope, shadowing);
  }

  @Override
  public List<VariableArcVariationPoint> getRootVariationPoints() {
    return variationPoints;
  }
}
