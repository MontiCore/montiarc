/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._symboltable.ArcComponentTypeSymbolBuilder;
import montiarc._symboltable.MontiArcComponentTypeSymbolBuilder;

public class ArcBasisMillForMontiArc extends ArcBasisMillForMontiArcTOP {

  @Override
  protected ArcComponentTypeSymbolBuilder _arcComponentTypeSymbolBuilder() {
    return new MontiArcComponentTypeSymbolBuilder();
  }
}