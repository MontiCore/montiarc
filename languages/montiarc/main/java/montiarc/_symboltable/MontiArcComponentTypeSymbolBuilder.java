/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ArcComponentTypeSymbol;
import arcbasis._symboltable.ArcComponentTypeSymbolBuilder;

public class MontiArcComponentTypeSymbolBuilder extends ArcComponentTypeSymbolBuilder {

  @Override
  public ArcComponentTypeSymbol build() {
    return doBuild(new MontiArcComponentTypeSymbol(this.name));
  }
}
