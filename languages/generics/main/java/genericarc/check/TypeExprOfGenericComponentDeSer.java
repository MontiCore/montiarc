/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis.check.deser.CompTypeExpressionDeSer;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

import static arcbasis.check.deser.TypeExprOfComponentDeSer.COMP_TYPE_NAME;

public class TypeExprOfGenericComponentDeSer implements CompTypeExpressionDeSer<TypeExprOfGenericComponent> {
  // Attention: The following string may need adaption in case of changed package location / renaming
  public static final String SERIALIZED_KIND = "genericarc.check.TypeExprOfGenericComponent";
  public static final String TYPE_VAR_BINDINGS = "typeVarBindings";

  @Override
  public String serializeAsJson(@NotNull TypeExprOfGenericComponent toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(JsonDeSers.KIND, SERIALIZED_KIND);
    printer.member(COMP_TYPE_NAME, toSerialize.getTypeInfo().getFullName());
    SymTypeExpressionDeSer.serializeMember(printer, TYPE_VAR_BINDINGS, toSerialize.getBindingsAsList());
    printer.endObject();

    return printer.getContent();
  }

  @Override
  public TypeExprOfGenericComponent deserialize(JsonObject serialized) {
    Preconditions.checkNotNull(serialized);
    Preconditions.checkArgument(
      JsonDeSers.getKind(serialized).equals(SERIALIZED_KIND),
      "Kind must be %s, but is %s.",
      SERIALIZED_KIND, JsonDeSers.getKind(serialized)
    );

    Log.warn("Deserializing TypeExprOfGenericComponents is buggy currently!");

    String compTypeName = serialized.getMember(COMP_TYPE_NAME)
      .getAsJsonString()
      .getValue();

    ComponentTypeSymbolSurrogate compType = ArcBasisMill
      .componentTypeSymbolSurrogateBuilder()
      .setName(compTypeName)
      .setEnclosingScope(ArcBasisMill.globalScope())
      .build();

    List<SymTypeExpression> paramBindings = SymTypeExpressionDeSer.deserializeListMember(TYPE_VAR_BINDINGS, serialized);

    return new TypeExprOfGenericComponent(compType, paramBindings);
  }
}
