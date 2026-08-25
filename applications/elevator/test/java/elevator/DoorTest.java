/* (c) https://github.com/MontiCore/monticore */
package elevator;

import elevator.Commands.DoorCMD;
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
class DoorTest {

  @ParameterizedTest
  @MethodSource("runs")
  void testIO(DoorCMD[] cmd,
              Boolean[] isOpen,
              Boolean[] isClosed,
              Boolean[] isObstacle,
              Boolean[] expOpen,
              Boolean[] expClose,
              Boolean[] expClosed) {
    DoorComp sut = new DoorCompBuilder().setName("sut").build();
    PortObserver<Boolean> portOpen = new PortObserver<>();
    PortObserver<Boolean> portClose = new PortObserver<>();
    PortObserver<Boolean> portClosed = new PortObserver<>();

    sut.port_open().connect(portOpen);
    sut.port_close().connect(portClose);
    sut.port_closed().connect(portClosed);

    List<Message<DoorCMD>> cmdMessages = messages(cmd);
    List<Message<Boolean>> isOpenMessages = messages(isOpen);
    List<Message<Boolean>> isClosedMessages = messages(isClosed);
    List<Message<Boolean>> isObstacleMessages = messages(isObstacle);
    for (int i = 0; i < cmdMessages.size(); i++) {
      sut.port_cmd().receive(cmdMessages.get(i));
      sut.port_isOpen().receive(isOpenMessages.get(i));
      sut.port_isClosed().receive(isClosedMessages.get(i));
      sut.port_isObstacle().receive(isObstacleMessages.get(i));
    }

    sut.runToCompletion();

    assertThat(portOpen.getObservedValues()).containsSubsequence(expOpen);
    assertThat(portClose.getObservedValues()).containsSubsequence(expClose);
    assertThat(portClosed.getObservedValues()).containsSubsequence(expClosed);
  }

  static Stream<Arguments> runs() {
    return Stream.of(
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE},
        new Boolean[]{false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, true},
        new Boolean[]{false, false, false, false, false, false, false}
      ),
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, true},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, true, true, false},
        new Boolean[]{false, false, false, false, false, false, false, false, true}
      ),
      Arguments.of(
        new DoorCMD[]{DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN},
        new Boolean[]{false, false, false, false, false, false, false, false, true},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, true, true, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false}
      ),
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, true},
        new Boolean[]{false, false, false, false, false, false, false, false, true},
        new Boolean[]{false, false, false, false, false, false, true, true, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false}
      ),
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN},
        new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true},
        new Boolean[]{false, false, false, false, false, false, false, false, true, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false, true, true, true, false},
        new Boolean[]{false, false, false, false, false, false, true, true, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, true, false, false, false, false}
      ),
      Arguments.of(
        new DoorCMD[]{DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE},
        new Boolean[]{false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true},
        new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false},
        new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true}
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
