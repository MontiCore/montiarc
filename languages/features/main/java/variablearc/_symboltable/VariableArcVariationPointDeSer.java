/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import variablearc._visitor.VariableArcTraverser;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VariableArcVariationPointDeSer {

  protected static final String JSON_CHILD_VARIATION_POINTS = "childVariationPoints";
  protected static final String JSON_SYMBOLS = "symbols";
  protected static final String JSON_CONDITION = "condition";

  protected final FunctionThrowingIOException<String, Optional<ASTExpression>> parse;
  protected final Function<ASTExpression, String> print;
  protected final BiFunction<IScope, JsonObject, ISymbol> deserializeSymbol;

  public VariableArcVariationPointDeSer(FunctionThrowingIOException<String, Optional<ASTExpression>> parse,
                                        Function<ASTExpression, String> print, BiFunction<IScope, JsonObject,
    ISymbol> deserializeSymbol) {
    this.parse = parse;
    this.print = print;
    this.deserializeSymbol = deserializeSymbol;
  }

  public String getSerializedKind() {
    return "variablearc._symboltable.VariableArcVariationPoint";
  }

  public void serialize(VariableArcVariationPoint toSerialize, de.monticore.symboltable.serialization.JsonPrinter p,
                        VariableArcTraverser serializeSymbolTraverser) {
    p.beginObject();
    p.member(JsonDeSers.KIND, getSerializedKind());

    p.member(JSON_CONDITION, this.print.apply(toSerialize.getCondition()));

    p.beginArray(JSON_SYMBOLS);
    for (ISymbol symbol : toSerialize.symbols) {
      serializeSymbol(serializeSymbolTraverser, symbol);
    }
    p.endArray();

    p.beginArray(JSON_CHILD_VARIATION_POINTS);
    for (VariableArcVariationPoint child : toSerialize.childVariationPoints) {
      serialize(child, p, serializeSymbolTraverser);
    }
    p.endArray();
    p.endObject();
  }

  public VariableArcVariationPoint deserialize(JsonObject variationPointJson, IVariableArcScope scope) {
    return deserialize(variationPointJson, scope, Optional.empty());
  }

  public VariableArcVariationPoint deserialize(JsonObject variationPointJson, IVariableArcScope scope,
                                               Optional<VariableArcVariationPoint> dependsOn) {
    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(null, dependsOn);

    if (variationPointJson.getStringMemberOpt(JSON_CONDITION).isPresent()) {
      try {
        variationPoint.condition =
          this.parse.apply(variationPointJson.getStringMemberOpt(JSON_CONDITION).get()).orElse(null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (variationPointJson.getArrayMemberOpt(JSON_SYMBOLS).isPresent()) {
      for (JsonElement e : variationPointJson.getArrayMemberOpt(JSON_SYMBOLS).get()) {
        variationPoint.add(this.deserializeSymbol.apply(scope, e.getAsJsonObject()));
      }
    }

    if (variationPointJson.getArrayMemberOpt(JSON_CHILD_VARIATION_POINTS).isPresent()) {
      for (JsonElement e : variationPointJson.getArrayMemberOpt(JSON_CHILD_VARIATION_POINTS).get()) {
        deserialize(e.getAsJsonObject(), scope, Optional.of(variationPoint));
      }
    }

    return variationPoint;
  }

  private void serializeSymbol(VariableArcTraverser traverser, ISymbol symbol) {
    /* keep it up to date with all possible common symbols IN VARIATION POINTS */
    if (symbol instanceof arcbasis._symboltable.ICommonArcBasisSymbol) {
      ((arcbasis._symboltable.ICommonArcBasisSymbol) symbol).accept(traverser);
    }  else if (symbol instanceof de.monticore.scbasis._symboltable.SCStateSymbol) {
      traverser.handle(symbol);
    } else if (symbol instanceof de.monticore.symbols.basicsymbols._symboltable.ICommonBasicSymbolsSymbol) {
      ((de.monticore.symbols.basicsymbols._symboltable.ICommonBasicSymbolsSymbol) symbol).accept(traverser);
    } else if (symbol instanceof variablearc._symboltable.ICommonVariableArcSymbol) {
      ((variablearc._symboltable.ICommonVariableArcSymbol) symbol).accept(traverser);
    } else {
      traverser.handle(symbol);
    }
  }

  @FunctionalInterface
  public interface FunctionThrowingIOException<T, R> {
    R apply(T t) throws IOException;
  }
}
