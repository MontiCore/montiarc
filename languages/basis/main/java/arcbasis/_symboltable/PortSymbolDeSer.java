/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import montiarc.Timing;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

public class PortSymbolDeSer extends PortSymbolDeSerTOP {

  @Override
  protected void serializeType(@NotNull SymTypeExpression type, @NotNull ArcBasisSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "type", type);
  }

  @Override
  protected void serializeTiming(@NotNull Timing timing, @NotNull ArcBasisSymbols2Json s2j) {
    s2j.getJsonPrinter().member("timing", timing.getName());
  }

  @Override
  protected SymTypeExpression deserializeType(@NotNull JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
  }

  @Override
  protected Timing deserializeTiming(@NotNull JsonObject symbolJson) {
    String timingString = symbolJson.getStringMember("timing");

    return Timing.of(timingString).orElseThrow(() ->
      new NoSuchElementException(String.format("Malformed Json: no such timing '%s'", timingString)));
  }
}
