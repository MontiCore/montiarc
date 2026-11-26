/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static montiarc.types.OnOff.ON;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class SendNoOptionalMessagesTest {

  @Test
  void sendsEmptyOptionalForEveryTimeSliceWithoutAnAssignment() {
    SendNoOptionalMessagesComp sut = new SendNoOptionalMessagesCompBuilder().setName("sut").build();
    PortObserver<Optional<OnOff>> port_o = new PortObserver<>();
    sut.port_o().connect(port_o);

    sut.port_p().receive(msg(ON));
    sut.port_p().receive(tk());
    sut.port_p().receive(msg(ON));
    sut.port_p().receive(tk());
    sut.runToCompletion();

    assertThat(port_o.getObservedMessages()).containsExactly(
      Message.of(Optional.empty()), Tick.get(),
      Message.of(Optional.empty()), Tick.get()
    );
    assertThat(Log.getFindings()).isEmpty();
  }
}
