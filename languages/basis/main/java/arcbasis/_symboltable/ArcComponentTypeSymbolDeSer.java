/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolDeSer;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcComponentTypeSymbolDeSer extends ArcComponentTypeSymbolDeSerTOP {

  protected final CompKindExpressionDeSer compTypeExprDeSer;

  public ArcComponentTypeSymbolDeSer() {
    compTypeExprDeSer = ArcBasisMill.compKindExprDeSer();
  }

  /**
   * @param compTypeExprDeSer the DeSer to use for (de)serializing the super components
   */
  public ArcComponentTypeSymbolDeSer(@NotNull CompKindExpressionDeSer compTypeExprDeSer) {
    this.compTypeExprDeSer = Preconditions.checkNotNull(compTypeExprDeSer);
  }

  protected CompKindExpressionDeSer getCompTypeExprDeSer() {
    return compTypeExprDeSer;
  }

  @Override
  protected void deserializeAddons(ArcComponentTypeSymbol symbol, JsonObject symbolJson) {
    symbol.getParameterList().forEach(symbol.getSpannedScope()::add);
    symbol.getInnerComponents().forEach(s -> s.setOuterComponent(symbol));
  }

  @Override
  protected void serializeSuperComponents(@NotNull List<CompKindExpression> superComponents,
                                          @NotNull ArcBasisSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ComponentTypeSymbolDeSer.SUPER);
    for (CompKindExpression superComponent : superComponents) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
        .createJsonString(this.getCompTypeExprDeSer().serialize(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(IArcBasisScope scope, JsonObject symbolJson) {

    List<JsonElement> superComponents = symbolJson.getArrayMemberOpt(ComponentTypeSymbolDeSer.SUPER).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(superComponents.size());

    for (JsonElement superComponent : superComponents) {
      result.add(this.getCompTypeExprDeSer().deserialize(scope, superComponent));
    }
    return result;
  }

  @Override
  protected void serializeParameter(List<VariableSymbol> parameter, ArcBasisSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(ComponentTypeSymbolDeSer.PARAMETERS);
    parameter.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  @Override
  protected List<VariableSymbol> deserializeParameter(JsonObject symbolJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = symbolJson.getArrayMemberOpt(ComponentTypeSymbolDeSer.PARAMETERS).orElseGet(Collections::emptyList);
    List<VariableSymbol> parameterResult = new ArrayList<>(params.size());

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      ISymbolDeSer<?, ?> deSer = CompSymbolsMill.globalScope().getSymbolDeSer(paramJsonKind);
      if (deSer != null && deSer.getSerializedKind().equals(varSerializeKind)) {
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());
        parameterResult.add(paramSym);
      } else {
        Log.error(String.format(
          "0xD0101 Malformed json, parameter '%s' of unsupported kind '%s'",
          param.getAsJsonObject().getStringMember(JsonDeSers.NAME), paramJsonKind
        ));
      }
    }
    return parameterResult;
  }

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements,
                                      ArcBasisSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ComponentTypeSymbolDeSer.REFINEMENTS);
    for (CompKindExpression superComponent : refinements) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
        .createJsonString(compTypeExprDeSer.serialize(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected List<CompKindExpression> deserializeRefinements(IArcBasisScope scope, JsonObject symbolJson) {
    List<JsonElement> refinements = symbolJson.getArrayMemberOpt(ComponentTypeSymbolDeSer.REFINEMENTS).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(refinements.size());

    for (JsonElement refinement : refinements) {
      result.add(compTypeExprDeSer.deserialize(scope, refinement));
    }
    return result;
  }

  @Override
  protected List<CompKindExpression> deserializeRefinements(JsonObject symbolJson) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(JsonObject symbolJson) {
    throw new UnsupportedOperationException();
  }
}
