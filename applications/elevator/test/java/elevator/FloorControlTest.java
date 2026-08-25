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
class FloorControlTest {

  @ParameterizedTest
  @MethodSource("runs")
  void testIO(Boolean[] btn, Boolean[] clear, Boolean[] expLight, Boolean[] expReq) {
    FloorControlComp sut = new FloorControlCompBuilder().setName("sut").build();
    PortObserver<Boolean> portLight = new PortObserver<>();
    PortObserver<Boolean> portReq = new PortObserver<>();

    sut.port_light().connect(portLight);
    sut.port_req().connect(portReq);

    List<Message<Boolean>> btnMessages = messages(btn);
    List<Message<Boolean>> clearMessages = messages(clear);
    for (int i = 0; i < btnMessages.size(); i++) {
      sut.port_btn().receive(btnMessages.get(i));
      sut.port_clear().receive(clearMessages.get(i));
    }

    sut.runToCompletion();

    assertThat(portLight.getObservedMessages()).containsExactlyElementsOf(messages(expLight));
    assertThat(portReq.getObservedMessages()).containsExactlyElementsOf(messages(expReq));
  }

  static Stream<Arguments> runs() {
    return Stream.of(
      Arguments.of(
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false},
        new Boolean[]{false, false}
      ),
      Arguments.of(
        new Boolean[]{true, true},
        new Boolean[]{false, false},
        new Boolean[]{true, true},
        new Boolean[]{true, true}
      ),
      Arguments.of(
        new Boolean[]{true, true},
        new Boolean[]{false, true},
        new Boolean[]{true, false},
        new Boolean[]{true, false}
      ),
      Arguments.of(
        new Boolean[]{false, true, false, false, true},
        new Boolean[]{false, false, false, true, true},
        new Boolean[]{false, true, true, false, false},
        new Boolean[]{false, true, true, false, false}
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
