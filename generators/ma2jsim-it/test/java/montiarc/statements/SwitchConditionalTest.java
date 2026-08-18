/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class SwitchConditionalTest {

  private SwitchConditionalComp sut;
  private PortObserver<Integer> port_o;

  @BeforeEach
  void init() {
    // Given
    sut = new SwitchConditionalCompBuilder().setName("sut").build();

    port_o = new PortObserver<>();

    sut.port_o().connect(port_o);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "1, 1, 1",
    "2, 2, -1",
    "-1, -1, -1"
  })
  void testInt(int i, int o1, int o2) {
    // When
    sut.port_i().receive(msg(i));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(o1), msg(o2));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "'a', 1",
    "'b', 2",
    "0, -1"
  })
  void testChar(char i, int o) {
    // When
    sut.port_c().receive(msg(i));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(o));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "a, 1",
    "b, 2",
    "0, -1"
  })
  void testString(String i, int o) {
    // When
    sut.port_s().receive(msg(i));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(o));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "ON, 1",
    "OFF, 2",
  })
  void testEnum(OnOff i, int o) {
    // When
    sut.port_e().receive(msg(i));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(o));
  }
}
