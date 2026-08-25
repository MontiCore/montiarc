/* (c) https://github.com/MontiCore/monticore */
package elevator;

import elevator.Commands.LiftCMD;
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
class LiftTest {

  @ParameterizedTest
  @MethodSource("runs")
  void testIO(LiftCMD[] cmd, Boolean[] expUp, Boolean[] expDown) {
    LiftComp sut = new LiftCompBuilder().setName("sut").build();
    PortObserver<Boolean> portUp = new PortObserver<>();
    PortObserver<Boolean> portDown = new PortObserver<>();

    sut.port_up().connect(portUp);
    sut.port_down().connect(portDown);

    for (Message<LiftCMD> input : messages(cmd)) {
      sut.port_cmd().receive(input);
    }

    sut.runToCompletion();

    assertThat(portUp.getObservedMessages()).containsExactlyElementsOf(messages(expUp));
    assertThat(portDown.getObservedMessages()).containsExactlyElementsOf(messages(expDown));
  }

  static Stream<Arguments> runs() {
    return Stream.of(
      Arguments.of(
        new LiftCMD[]{LiftCMD.UP, LiftCMD.STOP},
        new Boolean[]{true, false},
        new Boolean[]{false, false}
      ),
      Arguments.of(
        new LiftCMD[]{LiftCMD.DOWN, LiftCMD.STOP},
        new Boolean[]{false, false},
        new Boolean[]{true, false}
      ),
      Arguments.of(
        new LiftCMD[]{LiftCMD.STOP, LiftCMD.STOP},
        new Boolean[]{false, false},
        new Boolean[]{false, false}
      ),
      Arguments.of(
        new LiftCMD[]{LiftCMD.UP, null, LiftCMD.STOP},
        new Boolean[]{true, true, false},
        new Boolean[]{false, false, false}
      ),
      Arguments.of(
        new LiftCMD[]{LiftCMD.DOWN, null, LiftCMD.STOP},
        new Boolean[]{false, false, false},
        new Boolean[]{true, true, false}
      ),
      Arguments.of(
        new LiftCMD[]{LiftCMD.UP, LiftCMD.DOWN, LiftCMD.UP, LiftCMD.STOP},
        new Boolean[]{true, false, true, false},
        new Boolean[]{false, true, false, false}
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
