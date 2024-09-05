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
 * An outgoing port of a MontiArc component that can send ticks.
 *
 * @param <T> the type that can be sent via this port
 */
public class AbstractOutPort<T>  implements OutPort<T> {

  protected final String qualifiedName;
  protected final Component owner;

  /**
   * A list of all ports connected to this outgoing port.
   * <br>
   * Implemented as a list rather than a set because this allows for constant iteration (and thus message sending) order.
   */
  protected List<InPort<? super T>> recipients = new ArrayList<>();

  public AbstractOutPort(String qualifiedName, Component owner) {
    this.qualifiedName = qualifiedName;
    this.owner = owner;
  }

  @Override
  public String getQualifiedName() {
    return qualifiedName;
  }

  @Override
  public Component getOwner() {
    return this.owner;
  }

  /**
   * Connect the given incoming port to this outgoing port.
   *
   * @param recipient the port that should be connected to this port
   * @return true iff the given port is connected to this port when this method finishes
   */
  @Override
  public boolean connect(InPort<? super T> recipient) {
    if (recipient == null) {
      return false;
    }

    if (!recipients.contains(recipient)) {
      recipients.add(recipient);
    }

    return true;
  }

  /**
   * Disconnect the given incoming port from this outgoing port.
   *
   * @param recipient the port that should be disconnected from this port
   * @return true iff the given port is not connected to this port when this method finishes
   */
  @Override
  public boolean disconnect(InPort<? super T> recipient) {
    return recipients.remove(recipient) || !recipients.contains(recipient);
  }

  /**
   * Send out the given data as a message to all registered recipients,
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   *
   * @param data the data to send
   */
  @Override
  public void send(T data) {
    Log.info(DataFormatter.format(data), this.getQualifiedName() + "#" + Aspects.SEND_MSG);
    send(new Message<>(data));
  }

  /**
   * Send out the given message to all registered recipients,
   *
   * @param message the message to send
   */
  @Override
  public void send(Message<T> message) {
    for (InPort<? super T> recipient : recipients) {
      recipient.receive(message);
    }
  }

  /**
   * Send a tick via this port.
   */
  @Override
  public void sendTick() {
    Log.info("TK", this.getQualifiedName() + "#" + Aspects.SEND_MSG);
    this.send(Tick.get());
  }
}
