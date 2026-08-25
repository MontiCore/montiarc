/* (c) https://github.com/MontiCore/monticore */
package elevator;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class SplitterTest {

  @ParameterizedTest
  @MethodSource("histories")
  void testIO(Integer[] i, Boolean[] o1, Boolean[] o2, Boolean[] o3, Boolean[] o4) {
    SplitterComp sut = new SplitterCompBuilder().setName("sut").build();
    PortObserver<Boolean> portO1 = new PortObserver<>();
    PortObserver<Boolean> portO2 = new PortObserver<>();
    PortObserver<Boolean> portO3 = new PortObserver<>();
    PortObserver<Boolean> portO4 = new PortObserver<>();

    sut.port_o1().connect(portO1);
    sut.port_o2().connect(portO2);
    sut.port_o3().connect(portO3);
    sut.port_o4().connect(portO4);

    for (Message<Integer> input : messages(i)) {
      sut.port_i().receive(input);
    }

    sut.runToCompletion();

    assertThat(portO1.getObservedMessages()).containsExactlyElementsOf(messages(o1));
    assertThat(portO2.getObservedMessages()).containsExactlyElementsOf(messages(o2));
    assertThat(portO3.getObservedMessages()).containsExactlyElementsOf(messages(o3));
    assertThat(portO4.getObservedMessages()).containsExactlyElementsOf(messages(o4));
  }

  static Stream<Arguments> histories() {
    return Stream.of(
      Arguments.of(
        new Integer[]{null, null},
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false}
      ),
      Arguments.of(
        new Integer[]{0, 0},
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false}
      ),
      Arguments.of(
        new Integer[]{1, 0},
        new Boolean[]{true, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false}
      ),
      Arguments.of(
        new Integer[]{0, 1},
        new Boolean[]{false, true},
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false}
      ),
      Arguments.of(
        new Integer[]{0, 1, 0},
        new Boolean[]{false, true, false},
        new Boolean[]{false, false, false},
        new Boolean[]{false, false, false},
        new Boolean[]{false, false, false}
      ),
      Arguments.of(
        new Integer[]{1, 0, 1},
        new Boolean[]{true, false, true},
        new Boolean[]{false, false, false},
        new Boolean[]{false, false, false},
        new Boolean[]{false, false, false}
      ),
      Arguments.of(
        new Integer[]{1, 1, 1},
        new Boolean[]{true, true, true},
        new Boolean[]{false, false, false},
        new Boolean[]{false, false, false},
        new Boolean[]{false, false, false}
      ),
      Arguments.of(
        new Integer[]{0, 1, 2, 3, 4, 0},
        new Boolean[]{false, true, false, false, false, false},
        new Boolean[]{false, false, true, false, false, false},
        new Boolean[]{false, false, false, true, false, false},
        new Boolean[]{false, false, false, false, true, false}
      ),
      Arguments.of(
        new Integer[]{0, 4, 3, 2, 1, 0},
        new Boolean[]{false, false, false, false, true, false},
        new Boolean[]{false, false, false, true, false, false},
        new Boolean[]{false, false, true, false, false, false},
        new Boolean[]{false, true, false, false, false, false}
      )
    );
  }

  private static <T> List<Message<T>> messages(T[] values) {
    List<Message<T>> messages = new ArrayList<>();
    for (T value : values) {
      messages.add(value == null ? tk() : msg(value));
      if (value != null) {
        messages.add(tk());
      }
    }
    return messages;
  }
}
