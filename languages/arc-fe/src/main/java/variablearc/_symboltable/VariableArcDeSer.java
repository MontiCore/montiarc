/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcFullPrettyPrinter;

import java.util.Optional;

public class VariableArcDeSer extends VariableArcDeSerTOP {
  public static final String JsonAddonVariationPoint = "variationPoints";
  private final VariableArcVariationPointDeSer variableArcVariationPointDeSer;

  public VariableArcDeSer() {
    super();
    VariableArcFullPrettyPrinter prettyPrinter = new VariableArcFullPrettyPrinter();
    variableArcVariationPointDeSer = new VariableArcVariationPointDeSer((s -> Optional.empty()),
      prettyPrinter::prettyprint, this::deserializeSymbol);
  }

  @Override
  public void deserializeAddons(@NotNull IVariableArcScope scope, @NotNull JsonObject scopeJson) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(scopeJson);

    if (!scopeJson.getArrayMemberOpt(JsonAddonVariationPoint).isPresent())
      return;

    for (JsonElement variationPointsJson : scopeJson.getArrayMemberOpt(JsonAddonVariationPoint).get()) {
      scope.add(variableArcVariationPointDeSer.deserialize(variationPointsJson.getAsJsonObject(), scope));
    }
  }

  @Override
  public void serializeAddons(@NotNull IVariableArcScope toSerialize, @NotNull VariableArcSymbols2Json s2j) {
    Preconditions.checkNotNull(toSerialize);
    Preconditions.checkNotNull(s2j);

    s2j.getJsonPrinter().beginArray(JsonAddonVariationPoint);
    for (VariableArcVariationPoint variationPoint : toSerialize.getRootVariationPoints()) {
      variableArcVariationPointDeSer.serialize(variationPoint, s2j.getJsonPrinter(), s2j.getTraverser());
    }
    s2j.getJsonPrinter().endArray();
  }

  ISymbol deserializeSymbol(@NotNull IScope _scope, @NotNull JsonObject symbol) {
    Preconditions.checkNotNull(_scope);
    Preconditions.checkNotNull(symbol);
    /* partial copy of generated _symboltable.serialization.scopeDeSer.DeserializeSymbols */
    /* keep it up to date with all possible symbols IN VARIATION POINTS */
    IVariableArcScope scope = (IVariableArcScope) _scope;
    String kind = JsonDeSers.getKind(symbol);
    ISymbolDeSer deSer = VariableArcMill.globalScope().getSymbolDeSer(kind);
    if (null == deSer) {
      Log.warn("0xA1234xx80311 No DeSer found to deserialize symbol of kind `" + kind + "`. The following will be " +
        "ignored: " + symbol);
      return null;
    }

    if ("arcbasis._symboltable.ComponentInstanceSymbol".equals(kind) || ("arcbasis._symboltable" +
      ".ComponentInstanceSymbol").equals(deSer.getSerializedKind())) {
      ComponentInstanceSymbol s0 = (ComponentInstanceSymbol) deSer.deserialize(symbol);
      scope.add(s0);
      return s0;
    } else if ("arcbasis._symboltable.ComponentTypeSymbol".equals(kind) || ("arcbasis._symboltable" +
      ".ComponentTypeSymbol").equals(deSer.getSerializedKind())) {
      ComponentTypeSymbol s1 = (ComponentTypeSymbol) deSer.deserialize(symbol);
      scope.add(s1);
      scope.addSubScope(s1.getSpannedScope());
      return s1;
    } else if ("arcbasis._symboltable.PortSymbol".equals(kind) || "arcbasis._symboltable.PortSymbol".equals(deSer.getSerializedKind())) {
      PortSymbol s2 = (PortSymbol) deSer.deserialize(symbol);
      scope.add(s2);
      return s2;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.DiagramSymbol").equals(deSer.getSerializedKind())) {
      DiagramSymbol s3 = (DiagramSymbol) deSer.deserialize(symbol);
      scope.add(s3);
      return s3;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.FunctionSymbol").equals(deSer.getSerializedKind())) {
      FunctionSymbol s4 = (FunctionSymbol) deSer.deserialize(symbol);
      scope.add(s4);
      scope.addSubScope(s4.getSpannedScope());
      return s4;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.TypeSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.TypeSymbol").equals(deSer.getSerializedKind())) {
      TypeSymbol s5 = (TypeSymbol) deSer.deserialize(symbol);
      scope.add(s5);
      scope.addSubScope(s5.getSpannedScope());
      return s5;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.TypeVarSymbol").equals(deSer.getSerializedKind())) {
      TypeVarSymbol s6 = (TypeVarSymbol) deSer.deserialize(symbol);
      scope.add(s6);
      return s6;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.VariableSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.VariableSymbol").equals(deSer.getSerializedKind())) {
      VariableSymbol s7 = (VariableSymbol) deSer.deserialize(symbol);
      scope.add(s7);
      return s7;
    } else if ("de.monticore.symbols.oosymbols._symboltable.FieldSymbol".equals(kind) || ("de.monticore.symbols" +
      ".oosymbols._symboltable.FieldSymbol").equals(deSer.getSerializedKind())) {
      FieldSymbol s8 = (FieldSymbol) deSer.deserialize(symbol);
      scope.add(s8);
      return s8;
    } else if ("de.monticore.symbols.oosymbols._symboltable.MethodSymbol".equals(kind) || ("de.monticore.symbols" +
      ".oosymbols._symboltable.MethodSymbol").equals(deSer.getSerializedKind())) {
      MethodSymbol s9 = (MethodSymbol) deSer.deserialize(symbol);
      scope.add(s9);
      return s9;
    } else if ("de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol".equals(kind) || ("de.monticore.symbols" +
      ".oosymbols._symboltable.OOTypeSymbol").equals(deSer.getSerializedKind())) {
      OOTypeSymbol s10 = (OOTypeSymbol) deSer.deserialize(symbol);
      scope.add(s10);
      scope.addSubScope(s10.getSpannedScope());
      return s10;
    } else if ("variablearc._symboltable.ArcFeatureSymbol".equals(kind) || ("variablearc._symboltable" +
      ".ArcFeatureSymbol").equals(deSer.getSerializedKind())) {
      ArcFeatureSymbol s11 = (ArcFeatureSymbol) deSer.deserialize(symbol);
      scope.add(s11);
      return s11;
    } else {
      Log.warn("0xA1634xx80311 Unable to integrate deserialization with DeSer for kind `" + kind + "`. The following " +
        "will be ignored: " + symbol);
      return null;
    }
  }
}
