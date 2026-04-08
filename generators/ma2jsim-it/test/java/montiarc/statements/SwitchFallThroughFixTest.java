/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test compares the behavior of SwitchConditional (with break statements) and
 * SwitchConditionalNoBreak (without break statements) to ensure that both produce identical outputs.
 */
@JSimTest
class SwitchFallThroughFixTest {

  @Test
  void testSwitchConditionalWithBreaks() {
    // Given
    SwitchConditionalComp sutWithBreaks = new SwitchConditionalCompBuilder()
      .setName("with_breaks")
      .build();

    PortObserver<Integer> out = new PortObserver<>();
    sutWithBreaks.port_o().connect(out);

    // When
    sutWithBreaks.port_i().receive(msg(1));
    sutWithBreaks.runToCompletion();

    sutWithBreaks.port_i().receive(msg(2));
    sutWithBreaks.runToCompletion();

    sutWithBreaks.port_i().receive(msg(-1));
    sutWithBreaks.runToCompletion();

    sutWithBreaks.port_c().receive(msg('a'));
    sutWithBreaks.runToCompletion();

    sutWithBreaks.port_c().receive(msg('b'));
    sutWithBreaks.runToCompletion();

    sutWithBreaks.port_c().receive(msg('z'));
    sutWithBreaks.runToCompletion();

    sutWithBreaks.port_e().receive(msg(OnOff.ON));
    sutWithBreaks.runToCompletion();

    sutWithBreaks.port_e().receive(msg(OnOff.OFF));
    sutWithBreaks.runToCompletion();

    // Then
    assertThat(out.getObservedMessages()).containsExactly(
      msg(1), msg(1),
      msg(2), msg(-1),
      msg(-1), msg(-1),
      msg(1), msg(2), msg(-1),
      msg(1), msg(2)
    );
  }

  @Test
  void testSwitchConditionalWithoutBreaks() {
    // Given
    SwitchConditionalNoBreakComp sutNoBreaks = new SwitchConditionalNoBreakCompBuilder()
      .setName("no_breaks")
      .build();

    PortObserver<Integer> out = new PortObserver<>();
    sutNoBreaks.port_o().connect(out);

    // When
    sutNoBreaks.port_i().receive(msg(1));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_i().receive(msg(2));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_i().receive(msg(-1));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_c().receive(msg('a'));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_c().receive(msg('b'));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_c().receive(msg('z'));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_e().receive(msg(OnOff.ON));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_e().receive(msg(OnOff.OFF));
    sutNoBreaks.runToCompletion();

    // Then
    assertThat(out.getObservedMessages()).containsExactly(
      msg(1), msg(1),
      msg(2), msg(-1),
      msg(-1), msg(-1),
      msg(1), msg(2), msg(-1),
      msg(1), msg(2)
    );
  }
}
