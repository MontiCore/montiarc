/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;

import java.util.Optional;

/**
 * A port forward for synchronous streams whose values are optional.
 *
 * <p>If no value was sent during a time slice, this forward emits {@link Optional#empty()} before
 * closing the time slice. Consequently, the synchronous stream still contains exactly one value.
 * All other behavior, including diagnostics for multiple values, is inherited from
 * {@link SyncedPortForward}.</p>
 *
 * @param <T> the type wrapped by the optional values sent via this forward
 */
public class SyncedOptionalPortForward<T> extends SyncedPortForward<Optional<T>> {

  public SyncedOptionalPortForward(String qualifiedName, Component owner) {
    super(qualifiedName, owner);
  }

  @Override
  public void sendTick() {
    if (this.bufferedMessages.isEmpty()) {
      buffer(Message.of(Optional.empty()));
    }
    super.sendTick();
  }
}
