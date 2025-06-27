/* (c) https://github.com/MontiCore/monticore */
package variablearc._auxiliary;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolBuilder;
import variablearc._symboltable.VariableArcComponentTypeSymbolBuilder;

public class CompSymbolsMillForVariableArc extends CompSymbolsMillForVariableArcTOP {

  @Override
  protected ComponentTypeSymbolBuilder _componentTypeSymbolBuilder() {
    return new VariableArcComponentTypeSymbolBuilder();
  }
}
