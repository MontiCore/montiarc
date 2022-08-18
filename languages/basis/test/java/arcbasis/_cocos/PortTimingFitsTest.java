/*
 *  (c) https://github.com/MontiCore/monticore
 */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPort;
import arcbasis._symboltable.PortSymbol;
import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class PortTimingFitsTest extends AbstractTest {

  protected static Stream<Arguments> portSymbolWithErrorProvider() {
    return Stream.of(
        Arguments.of(
            ArcBasisMill.portSymbolBuilder()
                .setName("i1")
                .setTiming(Timing.SYNC)
                .setIncoming(true)
                .buildWithoutType(),
            new ArcError[]{}),
        Arguments.of(ArcBasisMill.portSymbolBuilder()
            .setTiming(Timing.DELAYED)
            .setName("o1")
            .setIncoming(false)
            .buildWithoutType(), new ArcError[]{}),
        Arguments.of(ArcBasisMill.portSymbolBuilder()
            .setTiming(Timing.DELAYED)
            .setName("i2")
            .setIncoming(true)
            .buildWithoutType(), new ArcError[]{ArcError.TIMING_DELAYED_WITH_INCOMING_PORT})
    );
  }

  @ParameterizedTest
  @MethodSource("portSymbolWithErrorProvider")
  public void testCocoWithPortSymbol(PortSymbol portSymbol, @NotNull ArcError... expectedErrors) {
    Preconditions.checkNotNull(portSymbol);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTPort port = ArcBasisMill.portBuilder().setName(portSymbol.getName()).build();
    port.setSymbol(portSymbol);

    // When
    PortTimingFits coco = new PortTimingFits();
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }
}
