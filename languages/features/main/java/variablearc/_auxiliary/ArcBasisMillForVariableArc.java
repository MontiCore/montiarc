/* (c) https://github.com/MontiCore/monticore */
package variablearc._auxiliary;

import arcbasis._symboltable.ArcComponentTypeSymbolBuilder;
import variablearc._symboltable.VariableArcComponentTypeSymbolBuilder;

public class ArcBasisMillForVariableArc extends ArcBasisMillForVariableArcTOP {

  @Override
  protected ArcComponentTypeSymbolBuilder _arcComponentTypeSymbolBuilder() {
    return new VariableArcComponentTypeSymbolBuilder();
  }
}
