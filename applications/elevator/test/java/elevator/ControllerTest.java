/* (c) https://github.com/MontiCore/monticore */
package elevator;

import elevator.Commands.DoorCMD;
import elevator.Commands.LiftCMD;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ControllerTest {

  @Test
  void testHomesDownUntilFloorOneIsReached() {
    ControllerComp sut = new ControllerCompBuilder().setName("sut").build();
    PortObserver<LiftCMD> portLift = new PortObserver<>();
    PortObserver<Integer> portClear = new PortObserver<>();

    sut.port_lift().connect(portLift);
    sut.port_clear().connect(portClear);

    repeat(sut, 8, false, false, false, false, false, true, true, true, true);

    sut.runToCompletion();

    assertThat(portLift.getObservedValues()).containsSubsequence(LiftCMD.DOWN);
    assertThat(portClear.getObservedValues()).contains(0);
  }

  @Test
  void testMovesUpTowardSecondFloorRequest() {
    ControllerComp sut = new ControllerCompBuilder().setName("sut").build();
    PortObserver<LiftCMD> portLift = new PortObserver<>();
    PortObserver<Integer> portClear = new PortObserver<>();

    sut.port_lift().connect(portLift);
    sut.port_clear().connect(portClear);

    repeat(sut, 8, false, true, false, false, true, false, false, false, true);
    repeat(sut, 8, false, true, false, false, false, true, false, false, true);

    sut.runToCompletion();

    assertThat(portLift.getObservedValues()).contains(LiftCMD.UP);
    assertThat(portClear.getObservedValues()).contains(0);
  }

  @Test
  void testServesRequestAtCurrentFloor() {
    ControllerComp sut = new ControllerCompBuilder().setName("sut").build();
    PortObserver<DoorCMD> portDoor = new PortObserver<>();
    PortObserver<LiftCMD> portLift = new PortObserver<>();
    PortObserver<Integer> portClear = new PortObserver<>();

    sut.port_door().connect(portDoor);
    sut.port_lift().connect(portLift);
    sut.port_clear().connect(portClear);

    repeat(sut, 13, true, false, false, false, true, false, false, false, true);

    sut.runToCompletion();

    assertThat(portLift.getObservedValues()).contains(LiftCMD.STOP);
    assertThat(portDoor.getObservedValues()).contains(DoorCMD.OPEN);
    assertThat(portClear.getObservedValues()).contains(1);
  }

  private static void repeat(ControllerComp sut,
                             int times,
                             boolean req1,
                             boolean req2,
                             boolean req3,
                             boolean req4,
                             boolean at1,
                             boolean at2,
                             boolean at3,
                             boolean at4,
                             boolean isClosed) {
    for (int i = 0; i < times; i++) {
      receiveStep(sut, req1, req2, req3, req4, at1, at2, at3, at4, isClosed);
    }
  }

  private static void receiveStep(ControllerComp sut,
                                  boolean req1,
                                  boolean req2,
                                  boolean req3,
                                  boolean req4,
                                  boolean at1,
                                  boolean at2,
                                  boolean at3,
                                  boolean at4,
                                  boolean isClosed) {
    sut.port_req1().receive(msg(req1));
    sut.port_req2().receive(msg(req2));
    sut.port_req3().receive(msg(req3));
    sut.port_req4().receive(msg(req4));
    sut.port_at1().receive(msg(at1));
    sut.port_at2().receive(msg(at2));
    sut.port_at3().receive(msg(at3));
    sut.port_at4().receive(msg(at4));
    sut.port_isClosed().receive(msg(isClosed));
    sut.port_req1().receive(tk());
    sut.port_req2().receive(tk());
    sut.port_req3().receive(tk());
    sut.port_req4().receive(tk());
    sut.port_at1().receive(tk());
    sut.port_at2().receive(tk());
    sut.port_at3().receive(tk());
    sut.port_at4().receive(tk());
    sut.port_isClosed().receive(tk());
  }
}
