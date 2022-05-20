/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.port.IInSimPort;

import java.io.Serializable;
import java.util.List;

/**
 * Handles the information, which ports of a component are tick-free and which are not.
 */
class PortMap implements IPortMap, Serializable {

  /**
   * Holds the information, if a port is tick-free (true) or not (false). Each port is represented by the position in
   * the array.
   */
  private final boolean[] isTickFree;

  /**
   * Amount of tickfree ports
   */
  private int tickFreePortAmount;

  /**
   * Constructs a new port map and assigns port numbers to the given ports.
   */
  public PortMap(List<IInSimPort<?>> ports) {
    int size = ports.size();
    isTickFree = new boolean[size];
    for (int i = 0; i < size; i++) {
      isTickFree[i] = true;
      ports.get(i).setPortNumber(i);
    }
    this.tickFreePortAmount = size;
  }

  /**
   * @see sim.sched.IPortMap#setPortBlocked(sim.port.IInSimPort)
   */
  @Override
  public void setPortBlocked(IInSimPort<?> port) {
    int nr = port.getPortNumber();
    if (isTickFree[nr]) {
      isTickFree[nr] = false;
      tickFreePortAmount--;
    }
  }

  /**
   * @see sim.sched.IPortMap#setPortTickfree(sim.port.IInSimPort)
   */
  @Override
  public void setPortTickfree(IInSimPort<?> port) {
    int nr = port.getPortNumber();
    if (!isTickFree[nr]) {
      isTickFree[nr] = true;
      tickFreePortAmount++;
    }
  }

  /**
   * @see sim.sched.IPortMap#allPortsBlocked()
   */
  @Override
  public boolean allPortsBlocked() {
    return tickFreePortAmount == 0;
  }

  /**
   * @see sim.sched.IPortMap#isTickFree(sim.port.IInSimPort)
   */
  @Override
  public boolean isTickFree(IInSimPort<?> port) {
    return isTickFree[port.getPortNumber()];
  }
}
