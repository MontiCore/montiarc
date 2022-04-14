/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.port.IInSimPort;

/**
 * Handles the information, which ports of a component are tick-free and which are not.
 */
interface IPortMap {

  /**
   * Sets the given port to blocked. Thus it is not tickfree afterwards.
   *
   * @param port port to set blocked
   */
  void setPortBlocked(IInSimPort<?> port);

  /**
   * Sets the given port to tickfree.
   *
   * @param port tickfree port
   */
  void setPortTickfree(IInSimPort<?> port);

  /**
   * @return true, if no ports are tickfree, else false.
   */
  boolean allPortsBlocked();

  /**
   * Checks, if the given port is tickfree.
   *
   * @param port port to check
   * @return true, if port is tickfree, else false.
   */
  boolean isTickFree(IInSimPort<?> port);
}
