/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cdbasis._symboltable.CDBasisSymbols2Json;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbolDeSer;

public class CD2PojoTypeSymbolDeSer extends CDTypeSymbolDeSer {

  /**
   * Serializes a class diagram type symbol into a String conforming to the
   * monticore symbol table conventions.
   */
  @Override
  public String serialize(CDTypeSymbol toSerialize, CDBasisSymbols2Json s2j) {
    de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();
    p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

    // serialize symbol-rule attributes
    serializeIsDerived(toSerialize.isIsDerived(), s2j);
    serializeSuperTypes(toSerialize.getSuperTypesList(), s2j);
    serializeIsClass(toSerialize.isIsClass(), s2j);
    serializeIsInterface(toSerialize.isIsInterface(), s2j);
    serializeIsEnum(toSerialize.isIsEnum(), s2j);
    serializeIsAbstract(toSerialize.isIsAbstract(), s2j);
    serializeIsPrivate(toSerialize.isIsPrivate(), s2j);
    serializeIsProtected(toSerialize.isIsProtected(), s2j);
    serializeIsPublic(toSerialize.isIsPublic(), s2j);
    serializeIsStatic(toSerialize.isIsStatic(), s2j);
    serializeIsFinal(toSerialize.isIsFinal(), s2j);

    // serialize spanned scope
    if (toSerialize.getSpannedScope().isExportingSymbols()
      && toSerialize.getSpannedScope().getSymbolsSize() > 0) {
      toSerialize.getSpannedScope().accept(s2j.getTraverser());
    }
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());

    serializeAddons(toSerialize, s2j);
    p.endObject();

    return p.toString();
  }
}
