package de.monticore.cd2pojo;

import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

public class TypeHelper {
  
  private String _package;
  
  public TypeHelper(String _package) {
    this._package = _package;
  }
  
  public String printType(CDTypeSymbol type) {
    return _package + "." + type.getName();
  }
}
