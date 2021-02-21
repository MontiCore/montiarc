/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MontiArc4BasicSymbolsDeSer extends BasicSymbolsDeSer {
  
  @Override
  public void deserializeAddons(@NotNull IBasicSymbolsScope scope, @NotNull JsonObject scopeJson) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(scopeJson);
    this.doDeserializeAddons(scope, scopeJson);
  }
  
  @Override
  public void deserializeAddons(@NotNull IBasicSymbolsArtifactScope scope, @NotNull JsonObject scopeJson) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(scopeJson);
    this.doDeserializeAddons(scope, scopeJson);
  }
  
  protected void doDeserializeAddons(@NotNull IBasicSymbolsScope scope, @NotNull JsonObject scopeJson) {
    List<JsonElement> markedForRemoval = new ArrayList<>();
    for (JsonElement e : scopeJson.getArrayMember(JsonDeSers.SYMBOLS)) {
      if (e.isJsonObject() && JsonDeSers.getKind(e.getAsJsonObject())
        .equals("de.monticore.cdbasis._symboltable.CDTypeSymbol")) {
        scope.add(((OOTypeSymbolDeSer) MontiArcMill.globalScope()
          .getSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol"))
          .deserialize(e.getAsJsonObject()));
        markedForRemoval.add(e);
      } else if (e.isJsonObject() && JsonDeSers.getKind(e.getAsJsonObject())
        .equals("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol")) {
        scope.add(((MethodSymbolDeSer) MontiArcMill.globalScope()
          .getSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol"))
          .deserialize(e.getAsJsonObject()));
        markedForRemoval.add(e);
      } else if (e.isJsonObject() && JsonDeSers.getKind(e.getAsJsonObject())
        .equals("de.monticore.symbols.oosymbols._symboltable.FieldSymbol")) {
        scope.add(((FieldSymbolDeSer) MontiArcMill.globalScope()
          .getSymbolDeSer("de.monticore.symbols.oosymbols._symboltable.FieldSymbol"))
          .deserialize(e.getAsJsonObject()));
        markedForRemoval.add(e);
      }
    }
    scopeJson.getArrayMember(JsonDeSers.SYMBOLS).removeAll(markedForRemoval);
  }
}