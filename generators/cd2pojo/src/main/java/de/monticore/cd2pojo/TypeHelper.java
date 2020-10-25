/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;

public class TypeHelper {
  
  private String _package;
  
  public TypeHelper(String _package) {
    this._package = _package;
  }
  
  public String printType(CDTypeSymbol type) {
    return _package + "." + type.getName();
  }
}
