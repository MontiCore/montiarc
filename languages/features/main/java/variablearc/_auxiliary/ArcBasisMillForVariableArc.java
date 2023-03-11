/* (c) https://github.com/MontiCore/monticore */
package variablearc._auxiliary;

import arcbasis._symboltable.ComponentTypeSymbolBuilder;
import variablearc._symboltable.VariableArcComponentTypeSymbolBuilder;

public class ArcBasisMillForVariableArc extends ArcBasisMillForVariableArcTOP {

  @Override
  protected ComponentTypeSymbolBuilder _componentTypeSymbolBuilder() {
    return new VariableArcComponentTypeSymbolBuilder();
  }
}
