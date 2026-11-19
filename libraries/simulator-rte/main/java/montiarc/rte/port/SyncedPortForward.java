/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;
import montiarc.rte.logging.Aspects;
import montiarc.rte.logging.DataFormatter;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.ArrayList;
import java.util.List;

/**
 * A port forward for synchronous streams.
 *
 * <p>Values are buffered until the tick that closes the current time slice. A synchronous stream
 * must contain exactly one value per time slice. If that contract is violated, the port reports a
 * warning. Values are forwarded in assignment order to preserve the existing runtime behavior.</p>
 *
 * @param <T> the type that is sent via this forward
 */
public class SyncedPortForward<T> extends PortForward<T> {

  protected List<Message<? extends T>> bufferedMessages = new ArrayList<>();

  public SyncedPortForward(String qualifiedName, Component owner) {
    super(qualifiedName, owner);
  }

  @Override
  public void send(T data) {
    buffer(Message.of(data));
  }

  @Override
  public void send(Message<? extends T> message) {
    if (message == Tick.get()) {
      sendTick();
    } else {
      buffer(message);
    }
  }

  protected void buffer(Message<? extends T> message) {
    this.bufferedMessages.add(message);
  }

  @Override
  public void sendTick() {
    List<Message<? extends T>> messages = this.bufferedMessages;
    this.bufferedMessages = new ArrayList<>();

    if (messages.isEmpty()) {
      Log.warn("No message was sent on synchronized port '" + getQualifiedName()
        + "' during the current time slice.");
    } else if (messages.size() > 1) {
      Log.warn(messages.size() + " messages were sent on synchronized port '"
        + getQualifiedName() + "' during the current time slice.");
    }

    for (Message<? extends T> message : messages) {
      Log.info(() -> DataFormatter.format(message.getData()), this.getQualifiedName() + "#" + Aspects.SEND_MSG);
      super.send(message);
    }

    Log.debug(() -> DataFormatter.TK, this.getQualifiedName() + "#" + Aspects.SEND_MSG);
    super.send(Tick.get());
  }
}
