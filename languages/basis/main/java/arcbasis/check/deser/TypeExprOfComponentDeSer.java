/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExprDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * (De-)serializes {@link TypeExprOfComponent}s.
 */
public class TypeExprOfComponentDeSer implements CompKindExprDeSer<TypeExprOfComponent> {

  public static final String SERIALIZED_KIND = "arcbasis.check.TypeExprOfComponent";
  public static final String COMP_TYPE_NAME = "componentTypeName";

  @Override
  public String serializeAsJson(@NotNull TypeExprOfComponent toSerialize) {
    Preconditions.checkNotNull(toSerialize);
    Preconditions.checkNotNull(toSerialize.getTypeInfo());

    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(JsonDeSers.KIND, SERIALIZED_KIND);
    printer.member(COMP_TYPE_NAME, toSerialize.getTypeInfo().getFullName());
    printer.endObject();

    return printer.getContent();
  }

  @Override
  public TypeExprOfComponent deserialize(@NotNull ICompSymbolsScope scope, @NotNull JsonObject serialized) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(serialized);
    Preconditions.checkArgument(
      JsonDeSers.getKind(serialized).equals(SERIALIZED_KIND),
      "Kind must be %s, but is %s.",
      SERIALIZED_KIND, JsonDeSers.getKind(serialized)
    );

    String compTypeName = serialized.getMember(COMP_TYPE_NAME).getAsJsonString().getValue();

    ComponentTypeSymbolSurrogate compType = ArcBasisMill
      .componentTypeSymbolSurrogateBuilder()
      .setName(compTypeName)
      .setEnclosingScope((IArcBasisScope) scope)
      .build();

    return new TypeExprOfComponent(compType);
  }
}
