/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ComponentTypeSymbolDeSer extends ComponentTypeSymbolDeSerTOP {

  public static final String PARAMETERS = "parameters";

  public static final String PORTS = "ports";
  public static final String TYPE_PARAMETERS = "typeParameters";
  public static final String PARENT = "parent";

  private ComposedCompTypeExprDeSer compTypeExprDeSer;

  public ComponentTypeSymbolDeSer() {
    this.compTypeExprDeSer = ArcBasisMill.compTypeExprDeSer();
  }

  /**
   * @param compTypeExprDeSer the DeSer to use for (de)serializing the parent.
   */
  public ComponentTypeSymbolDeSer(@NotNull ComposedCompTypeExprDeSer compTypeExprDeSer) {
    this.compTypeExprDeSer = Preconditions.checkNotNull(compTypeExprDeSer);
  }

  public ComposedCompTypeExprDeSer getCompTypeExprDeSer() {
    return compTypeExprDeSer;
  }

  public void setCompTypeExprDeSer(@NotNull ComposedCompTypeExprDeSer compTypeExprDeSer) {
    this.compTypeExprDeSer = Preconditions.checkNotNull(compTypeExprDeSer);
  }

  @Override
  public String serialize(@NotNull ComponentTypeSymbol toSerialize, @NotNull ArcBasisSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();
    printer.beginObject();
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

    // serialize symbolrule attributes
    if (toSerialize.isPresentParent()) {
      serializeParent(Optional.of(toSerialize.getParent()), s2j);
    }

    // Don't serialize the spanned scope (because it carries private information)
    // Instead, serialize type parameters and normal parameters separately.
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());  // So the spanned scope is not visited
    serializeParameters(toSerialize, s2j);
    serializePorts(toSerialize, s2j);
    serializeTypeParameters(toSerialize, s2j);

    serializeAddons(toSerialize, s2j);
    printer.endObject();

    return printer.toString();
  }

  @Override
  protected void deserializeAddons(ComponentTypeSymbol symbol, JsonObject symbolJson) {
    deserializeParameters(symbol, symbolJson);
    deserializePorts(symbol, symbolJson);
    deserializeTypeParameters(symbol, symbolJson);
  }

  @Override
  protected void serializeParent(@NotNull Optional<CompTypeExpression> parent, @NotNull ArcBasisSymbols2Json s2j) {
    parent
      .map(this.getCompTypeExprDeSer()::serializeAsJson)
      .ifPresent(json -> s2j.getJsonPrinter().memberJson(PARENT, json));
  }

  @Override
  protected Optional<CompTypeExpression> deserializeParent(@NotNull JsonObject compTypeJson) {
    return compTypeJson.getObjectMemberOpt(PARENT)
      .map(this.getCompTypeExprDeSer()::deserialize);
  }

  protected void serializeParameters(@NotNull ComponentTypeSymbol paramOwner, @NotNull ArcBasisSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PARAMETERS);
    paramOwner.getParameters().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  protected void serializePorts(@NotNull ComponentTypeSymbol portOwner, @NotNull ArcBasisSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PORTS);
    portOwner.getPorts().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param paramOwner the component which owns the parameter.
   * @param paramOwnerJson the component which owns the parameters, encoded as JSON.
   */
  protected void deserializeParameters(@NotNull ComponentTypeSymbol paramOwner, @NotNull JsonObject paramOwnerJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = paramOwnerJson.getArrayMemberOpt(PARAMETERS).orElseGet(Collections::emptyList);

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      if (paramJsonKind.equals(varSerializeKind)) {
        ISymbolDeSer deSer = ArcBasisMill.globalScope().getSymbolDeSer(varSerializeKind);
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());

        paramOwner.getSpannedScope().add(paramSym);
        paramOwner.addParameter(paramSym);

      } else {
        Log.error(String.format(
          "Could not deserialize parameter '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          param.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          paramOwner.getName(),
          paramJsonKind,
          varSerializeKind
        ));
      }
    }
  }

  /**
   * @param portOwner the component which owns the parameter.
   * @param paramOwnerJson the component which owns the parameters, encoded as JSON.
   */
  protected void deserializePorts(@NotNull ComponentTypeSymbol portOwner, @NotNull JsonObject paramOwnerJson) {
    final String portSerializeKind = PortSymbol.class.getCanonicalName();

    List<JsonElement> ports = paramOwnerJson.getArrayMemberOpt(PORTS).orElseGet(Collections::emptyList);

    for (JsonElement port : ports) {
      String portJasonKind = JsonDeSers.getKind(port.getAsJsonObject());
      if (portJasonKind.equals(portSerializeKind)) {
        ISymbolDeSer deSer = ArcBasisMill.globalScope().getSymbolDeSer(portSerializeKind);
        PortSymbol portSym = (PortSymbol) deSer.deserialize(port.getAsJsonObject());

        portOwner.getSpannedScope().add(portSym);

      } else {
        Log.error(String.format(
          "Could not deserialize port '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          port.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          portOwner.getName(),
          portJasonKind,
          portSerializeKind
        ));
      }
    }
  }

  protected void serializeTypeParameters(@NotNull ComponentTypeSymbol typeParamOwner, ArcBasisSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(TYPE_PARAMETERS);
    typeParamOwner.getTypeParameters().forEach(tp -> tp.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param typeParamOwner the component which owns the parameter.
   * @param typeParamOwnerJson the component which owns the type parameters, encoded as JSON.
   */
  protected void deserializeTypeParameters(@NotNull ComponentTypeSymbol typeParamOwner,
                                           @NotNull JsonObject typeParamOwnerJson) {
    final String typeVarSerializedKind = "de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol";

    List<JsonElement> typeParams =
      typeParamOwnerJson
        .getArrayMemberOpt(TYPE_PARAMETERS)
        .orElseGet(Collections::emptyList);

    for (JsonElement typeParam : typeParams) {
      String typeParamJsonKind = JsonDeSers.getKind(typeParam.getAsJsonObject());
      if (typeParamJsonKind.equals(typeVarSerializedKind)) {
        ISymbolDeSer deSer = arcbasis.ArcBasisMill.globalScope().getSymbolDeSer(typeVarSerializedKind);
        TypeVarSymbol typeParamSym = (TypeVarSymbol) deSer.deserialize(typeParam.getAsJsonObject());

        typeParamOwner.getSpannedScope().add(typeParamSym);
      } else {
        Log.error(String.format(
          "Could not deserialize parameter '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          typeParam.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          typeParamOwner.getName(),
          typeParamJsonKind,
          typeVarSerializedKind
        ));
      }
    }
  }
}
