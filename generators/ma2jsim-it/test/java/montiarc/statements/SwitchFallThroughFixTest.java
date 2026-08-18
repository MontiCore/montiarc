/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test ensures switch statements do not fall through between cases.
 */
@JSimTest
class SwitchFallThroughFixTest {

  @Test
  void testSwitchConditional() {
    // Given
    SwitchConditionalComp sut = new SwitchConditionalCompBuilder()
      .setName("switch_conditional")
      .build();

    PortObserver<Integer> out = new PortObserver<>();
    sut.port_o().connect(out);

    // When
    sut.port_i().receive(msg(1));
    sut.runToCompletion();

    sut.port_i().receive(msg(2));
    sut.runToCompletion();

    sut.port_i().receive(msg(-1));
    sut.runToCompletion();

    sut.port_c().receive(msg('a'));
    sut.runToCompletion();

    sut.port_c().receive(msg('b'));
    sut.runToCompletion();

    sut.port_c().receive(msg('z'));
    sut.runToCompletion();

    sut.port_s().receive(msg("a"));
    sut.runToCompletion();

    sut.port_s().receive(msg("b"));
    sut.runToCompletion();

    sut.port_s().receive(msg("z"));
    sut.runToCompletion();

    sut.port_e().receive(msg(OnOff.ON));
    sut.runToCompletion();

    sut.port_e().receive(msg(OnOff.OFF));
    sut.runToCompletion();

    // Then
    assertThat(out.getObservedMessages()).containsExactly(
      msg(1), msg(1),
      msg(2), msg(-1),
      msg(-1), msg(-1),
      msg(1), msg(2), msg(-1),
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

    sutNoBreaks.port_s().receive(msg("a"));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_s().receive(msg("b"));
    sutNoBreaks.runToCompletion();

    sutNoBreaks.port_s().receive(msg("z"));
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
      msg(1), msg(2), msg(-1),
      msg(1), msg(2)
    );
  }
}
