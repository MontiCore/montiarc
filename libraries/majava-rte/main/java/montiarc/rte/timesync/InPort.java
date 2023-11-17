/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import java.util.Collection;
import java.util.HashSet;

public class InPort<T> extends Port<T> implements IInPort<T> {

  protected Collection<IInPort<? super T>> observers;

  protected Collection<IInPort<? super T>> getObservers() {
    return this.observers;
  }

  boolean synced;

  public InPort(String name) {
    this.name = name;
    this.synced = false;
    this.observers = new HashSet<>(1);
  }

  public InPort() {
    this("");
  }

  @Override
  public boolean isSynced() {
    return this.synced;
  }

  @Override
  public void update(T value) {
    this.value = value;
    this.getObservers().forEach(p -> p.update(value));
    this.synced = true;
  }

  @Override
  public void tick() {
    super.tick();
    this.getObservers().forEach(IPort::tick);
    this.synced = false;
  }

  @Override
  public void connect(IInPort<? super T> port) {
    this.getObservers().add(port);
  }
}
