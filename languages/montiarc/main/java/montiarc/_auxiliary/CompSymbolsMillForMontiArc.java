/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolBuilder;
import montiarc._symboltable.MontiArcComponentTypeSymbolBuilder;

public class CompSymbolsMillForMontiArc extends CompSymbolsMillForMontiArcTOP {

  @Override
  protected ComponentTypeSymbolBuilder _componentTypeSymbolBuilder() {
    return new MontiArcComponentTypeSymbolBuilder();
  }
}
