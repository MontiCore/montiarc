/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPort;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.Timing;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DelayOutPortOnly} is the class and context-condition under test.
 */
public class DelayOutPortOnlyTest extends AbstractTest {

  @ParameterizedTest
  @CsvSource(value = {
    "false, true, false", // out port no delay
    "false, true, true", // out port has delay
    "true, false, false", // in port no delay
    "true, false, null", // in port no delay by default
    "false, true, null", // out port no delay by default
  })
  public void shouldNotReportPort(boolean in, boolean out, @Nullable Boolean delay) {
    // Given
    final ASTPort port = ArcBasisMill.portBuilder().setName("port").build();
    port.setSymbol(ArcBasisMill.portSymbolBuilder()
      .setName(port.getName())
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(in)
      .setOutgoing(out)
      .setTiming(Timing.DEFAULT)
      .setDelayed(delay)
      .build());
    DelayOutPortOnly coco = new DelayOutPortOnly();

    // When
    coco.check(port);

    // Then
    assertThat(Log.getErrorCount()).isEqualTo(0);
  }

  @Test
  public void shouldReportInPortHasDelay() {
    // Given
    final ASTPort port = ArcBasisMill.portBuilder().setName("port").build();
    port.setSymbol(ArcBasisMill.portSymbolBuilder()
      .setName(port.getName())
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .setOutgoing(false)
      .setTiming(Timing.DEFAULT)
      .setDelayed(true)
      .build());
    DelayOutPortOnly coco = new DelayOutPortOnly();

    // When
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent(ArcError.IN_PORT_DELAYED);
  }
}
