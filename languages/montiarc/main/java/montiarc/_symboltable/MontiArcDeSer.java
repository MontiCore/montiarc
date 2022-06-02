/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.scbasis._symboltable.SCStateSymbol;
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
import montiarc.MontiArcMill;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariableArcVariationPointDeSer;

public class MontiArcDeSer extends MontiArcDeSerTOP {

  protected static final String JSON_ADDON_VARIATION_POINT = "variationPoints";

  protected final VariableArcVariationPointDeSer variableArcVariationPointDeSer;

  public MontiArcDeSer() {
    super();
    MontiArcParser parser = new MontiArcParser();
    MontiArcFullPrettyPrinter prettyPrinter = new MontiArcFullPrettyPrinter();
    variableArcVariationPointDeSer = new VariableArcVariationPointDeSer(parser::parse_StringExpression,
      prettyPrinter::prettyprint, this::deserializeSymbol);
  }

  @Override
  public void deserializeAddons(IMontiArcScope scope, JsonObject scopeJson) {
    if (!scopeJson.getArrayMemberOpt(JSON_ADDON_VARIATION_POINT).isPresent())
      return;

    for (JsonElement variationPointsJson : scopeJson.getArrayMemberOpt(JSON_ADDON_VARIATION_POINT).get()) {
      scope.add(variableArcVariationPointDeSer.deserialize(variationPointsJson.getAsJsonObject(), scope));
    }
  }

  @Override
  public void serializeAddons(IMontiArcScope toSerialize, MontiArcSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(JSON_ADDON_VARIATION_POINT);
    for (VariableArcVariationPoint variationPoint : toSerialize.getRootVariationPoints()) {
      variableArcVariationPointDeSer.serialize(variationPoint, s2j.getJsonPrinter(), s2j.getTraverser());
    }
    s2j.getJsonPrinter().endArray();
  }

  ISymbol deserializeSymbol(IScope _scope, de.monticore.symboltable.serialization.json.JsonObject symbol) {
    /* partial copy of generated _symboltable.serialization.scopeDeSer.DeserializeSymbols */
    /* keep it up to date with all possible symbols IN VARIATION POINTS */
    IMontiArcScope scope = (IMontiArcScope) _scope;
    String kind = JsonDeSers.getKind(symbol);
    ISymbolDeSer deSer = MontiArcMill.globalScope().getSymbolDeSer(kind);
    if (null == deSer) {
      Log.warn("0xA1234xx39635 No DeSer found to deserialize symbol of kind `" + kind + "`. The following will be " +
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
    } else if ("de.monticore.scbasis._symboltable.SCStateSymbol".equals(kind) || ("de.monticore.scbasis._symboltable" +
      ".SCStateSymbol").equals(deSer.getSerializedKind())) {
      SCStateSymbol s3 = (SCStateSymbol) deSer.deserialize(symbol);
      scope.add(s3);
      return s3;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.DiagramSymbol").equals(deSer.getSerializedKind())) {
      DiagramSymbol s4 = (DiagramSymbol) deSer.deserialize(symbol);
      scope.add(s4);
      return s4;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.FunctionSymbol").equals(deSer.getSerializedKind())) {
      FunctionSymbol s5 = (FunctionSymbol) deSer.deserialize(symbol);
      scope.add(s5);
      scope.addSubScope(s5.getSpannedScope());
      return s5;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.TypeSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.TypeSymbol").equals(deSer.getSerializedKind())) {
      TypeSymbol s6 = (TypeSymbol) deSer.deserialize(symbol);
      scope.add(s6);
      scope.addSubScope(s6.getSpannedScope());
      return s6;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.TypeVarSymbol").equals(deSer.getSerializedKind())) {
      TypeVarSymbol s7 = (TypeVarSymbol) deSer.deserialize(symbol);
      scope.add(s7);
      return s7;
    } else if ("de.monticore.symbols.basicsymbols._symboltable.VariableSymbol".equals(kind) || ("de.monticore.symbols" +
      ".basicsymbols._symboltable.VariableSymbol").equals(deSer.getSerializedKind())) {
      VariableSymbol s8 = (VariableSymbol) deSer.deserialize(symbol);
      scope.add(s8);
      return s8;
    } else if ("de.monticore.symbols.oosymbols._symboltable.FieldSymbol".equals(kind) || ("de.monticore.symbols" +
      ".oosymbols._symboltable.FieldSymbol").equals(deSer.getSerializedKind())) {
      FieldSymbol s9 = (FieldSymbol) deSer.deserialize(symbol);
      scope.add(s9);
      return s9;
    } else if ("de.monticore.symbols.oosymbols._symboltable.MethodSymbol".equals(kind) || ("de.monticore.symbols" +
      ".oosymbols._symboltable.MethodSymbol").equals(deSer.getSerializedKind())) {
      MethodSymbol s10 = (MethodSymbol) deSer.deserialize(symbol);
      scope.add(s10);
      return s10;
    } else if ("de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol".equals(kind) || ("de.monticore.symbols" +
      ".oosymbols._symboltable.OOTypeSymbol").equals(deSer.getSerializedKind())) {
      OOTypeSymbol s11 = (OOTypeSymbol) deSer.deserialize(symbol);
      scope.add(s11);
      scope.addSubScope(s11.getSpannedScope());
      return s11;
    } else if ("variablearc._symboltable.ArcFeatureSymbol".equals(kind) || ("variablearc._symboltable" +
      ".ArcFeatureSymbol").equals(deSer.getSerializedKind())) {
      ArcFeatureSymbol s12 = (ArcFeatureSymbol) deSer.deserialize(symbol);
      scope.add(s12);
      return s12;
    } else {
      Log.warn("0xA1634xx39635 Unable to integrate deserialization with DeSer for kind `" + kind + "`. The following " +
        "will be ignored: " + symbol);
      return null;
    }
  }
}
