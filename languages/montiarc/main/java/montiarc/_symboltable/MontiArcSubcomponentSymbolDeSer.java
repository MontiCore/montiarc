/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.IArcBasisScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbolDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import montiarc.MontiArcMill;

import java.io.IOException;
import java.util.Optional;

/**
 * (De-)serializes subcomponents together with the arguments of their types.
 */
public class MontiArcSubcomponentSymbolDeSer extends SubcomponentSymbolDeSer {

  public static final String ARGUMENTS = "arguments";

  protected final ComponentArgumentsDeSer argumentsDeSer = new ComponentArgumentsDeSer(this::parse);

  protected Optional<ASTExpression> parse(String expression) {
    try {
      return MontiArcMill.parser().parse_StringExpression(expression);
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  @Override
  protected void serializeAddons(SubcomponentSymbol toSerialize, CompSymbolsSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    if (toSerialize.isTypePresent() && !toSerialize.getType().getArguments().isEmpty()) {
      s2j.getJsonPrinter().beginArray(ARGUMENTS);
      this.argumentsDeSer.serialize(s2j.getJsonPrinter(), toSerialize.getType());
      s2j.getJsonPrinter().endArray();
    }
  }

  @Override
  public SubcomponentSymbol deserialize(ICompSymbolsScope scope, JsonObject symbolJson) {
    SubcomponentSymbol symbol = super.deserialize(scope, symbolJson);
    if (symbolJson.hasArrayMember(ARGUMENTS) && scope instanceof IArcBasisScope arcScope) {
      this.argumentsDeSer.deserialize(symbolJson.getArrayMember(ARGUMENTS), symbol.getType(), arcScope);
    }
    return symbol;
  }
}
