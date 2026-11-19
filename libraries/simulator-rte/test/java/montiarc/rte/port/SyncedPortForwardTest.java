/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.TestBase;
import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SyncedPortForwardTest extends TestBase {

  @Test
  void buffersMessageUntilEndOfTimeSlice() {
    SyncedPortForward<Integer> port = createPort();
    PortObserver<Integer> observer = observe(port);

    port.send(1);

    assertThat(observer.getObservedMessages()).isEmpty();

    port.sendTick();

    assertThat(observer.getObservedMessages()).containsExactly(Message.of(1), Tick.get());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void warnsWhenNoMessageWasSent() {
    SyncedPortForward<Integer> port = createPort();
    PortObserver<Integer> observer = observe(port);

    port.sendTick();

    assertThat(observer.getObservedMessages()).containsExactly(Tick.get());
    assertThat(Log.getFindings())
      .allMatch(Finding::isWarning)
      .extracting(Finding::getMsg)
      .containsExactly("No message was sent on synchronized port 'component.output' during the current time slice.");
  }

  @Test
  void warnsAndForwardsAllMessagesWhenMultipleMessagesWereSent() {
    SyncedPortForward<Integer> port = createPort();
    PortObserver<Integer> observer = observe(port);

    port.send(1);
    port.receive(Message.of(2));
    port.send(3);
    port.sendTick();

    assertThat(observer.getObservedMessages())
      .containsExactly(Message.of(1), Message.of(2), Message.of(3), Tick.get());
    assertThat(Log.getFindings())
      .allMatch(Finding::isWarning)
      .extracting(Finding::getMsg)
      .containsExactly("3 messages were sent on synchronized port 'component.output' during the current time slice.");
  }

  @Test
  void startsFreshAfterEveryTick() {
    SyncedPortForward<Integer> port = createPort();
    PortObserver<Integer> observer = observe(port);

    port.send(1);
    port.sendTick();
    port.send(2);
    port.sendTick();

    assertThat(observer.getObservedMessages())
      .containsExactly(Message.of(1), Tick.get(), Message.of(2), Tick.get());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void treatsTickMessageAsEndOfTimeSlice() {
    SyncedPortForward<Integer> port = createPort();
    PortObserver<Integer> observer = observe(port);

    port.send(1);
    port.send(Tick.get());

    assertThat(observer.getObservedMessages()).containsExactly(Message.of(1), Tick.get());
    assertThat(Log.getFindings()).isEmpty();
  }

  private SyncedPortForward<Integer> createPort() {
    return new SyncedPortForward<>("component.output", mock(Component.class));
  }

  private PortObserver<Integer> observe(SyncedPortForward<Integer> port) {
    PortObserver<Integer> observer = new PortObserver<>();
    port.connect(observer);
    return observer;
  }
}
