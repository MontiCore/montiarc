/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentTypeSymbolDeSer extends ComponentTypeSymbolDeSerTOP {

  public static final String PARAMETERS = "parameters";
  public static final String PORTS = "ports";
  public static final String TYPE_PARAMETERS = "typeParameters";
  public static final String PARENTS = "parents";
  public static final String INNER_COMPONENTS = "innerComponents";
  public static final String SUBCOMPONENTS = "subcomponents";
  public static final String FIELDS = "fields";

  private FullCompKindExprDeSer compTypeExprDeSer;

  public ComponentTypeSymbolDeSer() {
    this.compTypeExprDeSer = ArcBasisMill.compTypeExprDeSer();
  }

  /**
   * @param compTypeExprDeSer the DeSer to use for (de)serializing the parent.
   */
  public ComponentTypeSymbolDeSer(@NotNull FullCompKindExprDeSer compTypeExprDeSer) {
    this.compTypeExprDeSer = Preconditions.checkNotNull(compTypeExprDeSer);
  }

  public FullCompKindExprDeSer getCompTypeExprDeSer() {
    return compTypeExprDeSer;
  }

  public void setCompTypeExprDeSer(@NotNull FullCompKindExprDeSer compTypeExprDeSer) {
    this.compTypeExprDeSer = Preconditions.checkNotNull(compTypeExprDeSer);
  }

  @Override
  public String serialize(@NotNull ComponentTypeSymbol toSerialize, @NotNull ArcBasisSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();
    printer.beginObject();
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

    // serialize symbolrule attributes
    if (!toSerialize.isEmptySuperComponents()) {
      serializeSuperComponents(toSerialize.getSuperComponentsList(), s2j);
    }

    // Don't serialize the spanned scope (because it carries private information)
    // Instead, serialize type parameters and normal parameters separately.
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());  // So the spanned scope is not visited
    serializeAddons(toSerialize, s2j);

    printer.endObject();

    return printer.toString();
  }

  @Override
  protected void serializeAddons(@NotNull ComponentTypeSymbol toSerialize, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(toSerialize);
    Preconditions.checkNotNull(s2j);

    serializeParameters(toSerialize, s2j);
    serializePorts(toSerialize, s2j);
    serializeTypeParameters(toSerialize, s2j);
    serializeInnerComponents(toSerialize, s2j);
    serializeSubcomponents(toSerialize, s2j);
    serializeFields(toSerialize, s2j);
  }

  @Override
  protected void deserializeAddons(ComponentTypeSymbol symbol, JsonObject symbolJson) {
    deserializeParameters(symbol, symbolJson);
    deserializePorts(symbol, symbolJson);
    deserializeTypeParameters(symbol, symbolJson);
    deserializeInnerComponents(symbol, symbolJson);
    deserializeSubcomponents(symbol, symbolJson);
    deserializeFields(symbol, symbolJson);
  }

  @Override
  protected void serializeSuperComponents(List<CompKindExpression> superComponents, ArcBasisSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PARENTS);
    superComponents.stream()
      .map(this.getCompTypeExprDeSer()::serializeAsJson).forEach(printer::valueJson);
    printer.endArray();
  }

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements, ArcBasisSymbols2Json s2j) {
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(@NotNull JsonObject compTypeJson) {
    return compTypeJson.getArrayMemberOpt(PARENTS).orElse(Collections.emptyList()).stream().map(JsonElement::getAsJsonObject)
      .map(this.getCompTypeExprDeSer()::deserialize).collect(Collectors.toList());
  }

  @Override
  protected List<CompKindExpression> deserializeRefinements(JsonObject symbolJson) {
    return new ArrayList<>();
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
    portOwner.getArcPorts().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param component the component that is serialized
   * @param s2j       the json printer that the inner components are serialized to
   */
  protected void serializeInnerComponents(@NotNull ComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);

    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(INNER_COMPONENTS);
    component.getInnerComponents().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param component the component that is serialized
   * @param s2j       the json printer that the subcomponents are serialized to
   */
  protected void serializeSubcomponents(@NotNull ComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);

    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(SUBCOMPONENTS);
    component.getSubcomponents().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param component the component that is serialized
   * @param s2j       the json printer that the fields are serialized to
   */
  protected void serializeFields(@NotNull ComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);

    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(FIELDS);
    component.getFields().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param paramOwner     the component which owns the parameter.
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
   * @param portOwner      the component which owns the parameter.
   * @param paramOwnerJson the component which owns the parameters, encoded as JSON.
   */
  protected void deserializePorts(@NotNull ComponentTypeSymbol portOwner, @NotNull JsonObject paramOwnerJson) {
    final String portSerializeKind = ArcPortSymbol.class.getCanonicalName();

    List<JsonElement> ports = paramOwnerJson.getArrayMemberOpt(PORTS).orElseGet(Collections::emptyList);

    for (JsonElement port : ports) {
      String portJasonKind = JsonDeSers.getKind(port.getAsJsonObject());
      if (portJasonKind.equals(portSerializeKind)) {
        ISymbolDeSer deSer = ArcBasisMill.globalScope().getSymbolDeSer(portSerializeKind);
        ArcPortSymbol portSym = (ArcPortSymbol) deSer.deserialize(port.getAsJsonObject());

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
   * @param typeParamOwner     the component which owns the parameter.
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

  /**
   * @param component the component which owns the inner components.
   * @param json      the component which owns the inner components, encoded as JSON.
   */
  protected void deserializeInnerComponents(@NotNull ComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);

    final String componentSerializeKind = ComponentTypeSymbol.class.getCanonicalName();

    List<JsonElement> innerComponents = json.getArrayMemberOpt(INNER_COMPONENTS).orElseGet(Collections::emptyList);

    for (JsonElement innerComponent : innerComponents) {
      String componentJsonKind = JsonDeSers.getKind(innerComponent.getAsJsonObject());
      if (componentJsonKind.equals(componentSerializeKind)) {
        ISymbolDeSer deSer = ArcBasisMill.globalScope().getSymbolDeSer(componentJsonKind);
        ComponentTypeSymbol componentSym = (ComponentTypeSymbol) deSer.deserialize(innerComponent.getAsJsonObject());

        component.getSpannedScope().add(componentSym);

      } else {
        Log.error(String.format(
          "Could not deserialize inner component '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          innerComponent.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          component.getName(),
          componentJsonKind,
          componentSerializeKind
        ));
      }
    }
  }

  /**
   * @param component the component which owns the subcomponents.
   * @param json      the component which owns the subcomponents, encoded as JSON.
   */
  protected void deserializeSubcomponents(@NotNull ComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);

    final String instanceSerializeKind = SubcomponentSymbol.class.getCanonicalName();

    List<JsonElement> instances = json.getArrayMemberOpt(SUBCOMPONENTS).orElseGet(Collections::emptyList);

    for (JsonElement instance : instances) {
      String instanceJsonKind = JsonDeSers.getKind(instance.getAsJsonObject());
      if (instanceJsonKind.equals(instanceSerializeKind)) {
        ISymbolDeSer deSer = ArcBasisMill.globalScope().getSymbolDeSer(instanceJsonKind);
        SubcomponentSymbol instanceSym = (SubcomponentSymbol) deSer.deserialize(instance.getAsJsonObject());

        component.getSpannedScope().add(instanceSym);

      } else {
        Log.error(String.format(
          "Could not deserialize instance '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          instance.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          component.getName(),
          instanceJsonKind,
          instanceSerializeKind
        ));
      }
    }
  }

  /**
   * @param component the component which owns the instance.
   * @param json      the component which owns the instance, encoded as JSON.
   */
  protected void deserializeFields(@NotNull ComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);

    final String fieldSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> fields = json.getArrayMemberOpt(FIELDS).orElseGet(Collections::emptyList);

    for (JsonElement field : fields) {
      String fieldJsonKind = JsonDeSers.getKind(field.getAsJsonObject());
      if (fieldJsonKind.equals(fieldSerializeKind)) {
        ISymbolDeSer deSer = ArcBasisMill.globalScope().getSymbolDeSer(fieldJsonKind);
        VariableSymbol fieldSym = (VariableSymbol) deSer.deserialize(field.getAsJsonObject());

        component.getSpannedScope().add(fieldSym);

      } else {
        Log.error(String.format(
          "Could not deserialize field '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          field.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          component.getName(),
          fieldJsonKind,
          fieldSerializeKind
        ));
      }
    }
  }
}
