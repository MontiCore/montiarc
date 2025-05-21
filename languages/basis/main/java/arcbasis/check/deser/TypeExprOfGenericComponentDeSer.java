/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcComponentTypeSymbolSurrogate;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.TypeExprOfGenericComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

import static arcbasis.check.deser.TypeExprOfComponentDeSer.COMP_TYPE_NAME;

public class TypeExprOfGenericComponentDeSer {

  public static final String SERIALIZED_KIND = "arcbasis.check.TypeExprOfGenericComponent";
  public static final String TYPE_VAR_BINDINGS = "typeVarBindings";

  public String serialize(@NotNull TypeExprOfGenericComponent toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(JsonDeSers.KIND, SERIALIZED_KIND);
    printer.member(COMP_TYPE_NAME, toSerialize.getTypeInfo().getFullName());
    SymTypeExpressionDeSer.serializeMember(printer, TYPE_VAR_BINDINGS, toSerialize.getTypeBindingsAsList());
    printer.endObject();

    return printer.getContent();
  }

  public TypeExprOfGenericComponent deserialize(@NotNull ICompSymbolsScope scope, @NotNull JsonObject serialized) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(serialized);
    Preconditions.checkArgument(
      JsonDeSers.getKind(serialized).equals(SERIALIZED_KIND),
      "Kind must be %s, but is %s.",
      SERIALIZED_KIND, JsonDeSers.getKind(serialized)
    );

    String compTypeName = serialized.getMember(COMP_TYPE_NAME)
      .getAsJsonString()
      .getValue();

    ArcComponentTypeSymbolSurrogate compType = ArcBasisMill
      .arcComponentTypeSymbolSurrogateBuilder()
      .setName(compTypeName)
      .setEnclosingScope((IArcBasisScope) scope)
      .build();

    List<SymTypeExpression> paramBindings = SymTypeExpressionDeSer.deserializeListMember(TYPE_VAR_BINDINGS, serialized, scope);

    return new TypeExprOfGenericComponent(compType, paramBindings);
  }
}
