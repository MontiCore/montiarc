/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.ArcBasisAbstractTest;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.PortSymbolDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import montiarc.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class PortSymbolDeSerTest extends ArcBasisAbstractTest {

  @ParameterizedTest
  @EnumSource(Timing.class)
  public void shouldSerializeTimingCorrectly(@NotNull Timing timing) {
    // Given
    String expectedJson = String.format("{\"timing\":\"%s\"}", timing.getName());
    PortSymbolDeSerForTests deser = new PortSymbolDeSerForTests();
    ArcBasisSymbols2Json arcBasis2Json = new ArcBasisSymbols2Json();

    // When
    arcBasis2Json.getJsonPrinter().beginObject();
    deser.serializeTiming(timing, arcBasis2Json);
    arcBasis2Json.getJsonPrinter().endObject();

    String serialized = arcBasis2Json.getSerializedString();

    // Then
    Assertions.assertEquals(expectedJson, serialized);
  }

  @ParameterizedTest
  @EnumSource(Timing.class)
  public void shouldDeserializeTimingCorrectly(@NotNull Timing timing) {
    // Given
    String json = String.format("{\"timing\":\"%s\"}", timing.getName());
    JsonObject timingObject = JsonParser.parseJsonObject(json);
    PortSymbolDeSerForTests deser = new PortSymbolDeSerForTests();

    // When
    Timing deserializedTiming = deser.deserializeTiming(timingObject);

    // Then
    Assertions.assertSame(timing, deserializedTiming);
  }

  /**
   * Provides access to the methods for serializing and deserializing timing
   */
  private static class PortSymbolDeSerForTests extends PortSymbolDeSer {
    @Override
    public void serializeTiming(@NotNull Timing timing, @NotNull ArcBasisSymbols2Json s2j) {
      super.serializeTiming(timing, s2j);
    }

    @Override
    protected Timing deserializeTiming(@NotNull JsonObject symbolJson) {
      return super.deserializeTiming(symbolJson);
    }
  }
}
