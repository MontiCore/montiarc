/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolBuilder;

public class MontiArcComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilder {

  @Override
  public ComponentTypeSymbol build() {
    return doBuild(new MontiArcComponentTypeSymbol(this.name));
  }
}
