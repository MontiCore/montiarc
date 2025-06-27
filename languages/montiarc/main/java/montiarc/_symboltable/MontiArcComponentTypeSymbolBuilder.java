/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolBuilder;

public class MontiArcComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilder {

  @Override
  public ComponentTypeSymbol build() {
    return doBuild(new MontiArcComponentTypeSymbol(this.name));
  }
}
