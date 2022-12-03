/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cdbasis._symboltable.CDBasisSymbols2Json;
import de.monticore.cdbasis._symboltable.CDPackageSymbol;
import de.monticore.cdbasis._symboltable.CDPackageSymbolDeSer;

public class CD2PojoPackageSymbolDeSer extends CDPackageSymbolDeSer {

  /**
   * Serializes a class diagram package symbol into a String conforming to the
   * monticore symbol table conventions.
   */
  @Override
  public String serialize(CDPackageSymbol toSerialize, CDBasisSymbols2Json s2j) {

    de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();

    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());

    serializeAddons(toSerialize, s2j);
    p.endObject();

    return p.toString();
  }
}
