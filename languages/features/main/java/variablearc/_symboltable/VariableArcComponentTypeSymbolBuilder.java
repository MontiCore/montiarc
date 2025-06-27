/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolBuilder;

public class VariableArcComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilder {

  @Override
  public ComponentTypeSymbol build() {
    return doBuild(new VariableArcComponentTypeSymbol(this.name));
  }
}
