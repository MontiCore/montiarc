/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.TestBase;
import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SyncedOptionalPortForwardTest extends TestBase {

  @Test
  void sendsEmptyOptionalWhenNoMessageWasSent() {
    SyncedOptionalPortForward<Integer> port = createPort();
    PortObserver<Optional<Integer>> observer = observe(port);

    port.sendTick();

    assertThat(observer.getObservedMessages())
      .containsExactly(Message.of(Optional.empty()), Tick.get());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void forwardsSentOptionalInsteadOfGeneratingEmptyOptional() {
    SyncedOptionalPortForward<Integer> port = createPort();
    PortObserver<Optional<Integer>> observer = observe(port);

    port.send(Optional.of(1));
    port.sendTick();

    assertThat(observer.getObservedMessages())
      .containsExactly(Message.of(Optional.of(1)), Tick.get());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void warnsAndForwardsAllMessagesWhenMultipleMessagesWereSent() {
    SyncedOptionalPortForward<Integer> port = createPort();
    PortObserver<Optional<Integer>> observer = observe(port);

    port.send(Optional.of(1));
    port.send(Optional.empty());
    port.sendTick();

    assertThat(observer.getObservedMessages())
      .containsExactly(Message.of(Optional.of(1)), Message.of(Optional.empty()), Tick.get());
    assertThat(Log.getFindings())
      .allMatch(Finding::isWarning)
      .extracting(Finding::getMsg)
      .containsExactly("2 messages were sent on synchronized port 'component.output' during the current time slice.");
  }

  @Test
  void generatesEmptyOptionalInEveryEmptyTimeSlice() {
    SyncedOptionalPortForward<Integer> port = createPort();
    PortObserver<Optional<Integer>> observer = observe(port);

    port.sendTick();
    port.sendTick();

    assertThat(observer.getObservedMessages()).containsExactly(
      Message.of(Optional.empty()), Tick.get(),
      Message.of(Optional.empty()), Tick.get()
    );
    assertThat(Log.getFindings()).isEmpty();
  }

  private SyncedOptionalPortForward<Integer> createPort() {
    return new SyncedOptionalPortForward<>("component.output", mock(Component.class));
  }

  private PortObserver<Optional<Integer>> observe(SyncedOptionalPortForward<Integer> port) {
    PortObserver<Optional<Integer>> observer = new PortObserver<>();
    port.connect(observer);
    return observer;
  }
}
