/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

public class ArcPortSymbolDeSer extends ArcPortSymbolDeSerTOP {

  @Override
  protected void serializeTiming(@NotNull Timing timing, @NotNull ArcBasisSymbols2Json s2j) {
    s2j.getJsonPrinter().member("timing", timing.getName());
  }

  @Override
  protected Timing deserializeTiming(@NotNull JsonObject symbolJson) {
    String timingString = symbolJson.getStringMember("timing");

    return Timing.of(timingString).orElseThrow(() ->
      new NoSuchElementException(String.format("Malformed Json: no such timing '%s'", timingString)));
  }
}
