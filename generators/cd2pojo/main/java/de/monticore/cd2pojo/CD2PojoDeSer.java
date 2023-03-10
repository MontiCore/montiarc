/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.cd4code._symboltable.CD4CodeDeSer;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.se_rwth.commons.Names;
import org.codehaus.commons.nullanalysis.NotNull;

public class CD2PojoDeSer extends CD4CodeDeSer {

  /**
   * Serializes a class diagram artifact scope into a String conforming to the
   * monticore symbol table conventions.
   */
  @Override
  public String serialize(@NotNull ICD4CodeArtifactScope toSerialize, @NotNull CD4CodeSymbols2Json s2j) {
    Preconditions.checkNotNull(toSerialize);
    Preconditions.checkNotNull(s2j);
    de.monticore.symboltable.serialization.JsonPrinter printer = s2j.getJsonPrinter();
    printer.member("generated-using", "www.MontiCore.de technology");
    if (toSerialize.isPresentName()) {
      printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, Names.getSimpleName(toSerialize.getName()));
    }
    if (!toSerialize.getPackageName().isBlank()) {
      printer.member(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE, toSerialize.getPackageName());
    }
    return printer.toString();
  }
}
