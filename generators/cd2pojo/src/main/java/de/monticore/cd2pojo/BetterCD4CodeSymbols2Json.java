/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.symboltable.serialization.JsonPrinter;

public class BetterCD4CodeSymbols2Json extends CD4CodeSymbols2Json {
  
  public BetterCD4CodeSymbols2Json(JsonPrinter printer) {
    super(printer);
  }
  
  @Override
  public void printKindHierarchy() {  }
}