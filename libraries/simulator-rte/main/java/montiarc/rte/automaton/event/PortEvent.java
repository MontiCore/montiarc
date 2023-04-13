/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.event;

import montiarc.rte.port.AbstractInPort;

/**
 * Represents a port-specific event in an event-based MontiArc component.
 */
public class PortEvent extends Event {
  
  protected AbstractInPort<?> usedPort;
  
  public PortEvent(AbstractInPort<?> usedPort) {
    this.usedPort = usedPort;
  }
  
  /**
   * Check whether this is equal to a given other event.
   * In this case the other event must also be a port event referencing the same port instance.
   *
   * @param other the other event
   * @return true if this equals the given other event
   */
  @Override
  protected boolean equalsOtherEvent(Event other) {
    return other instanceof PortEvent && this.usedPort == ((PortEvent) other).usedPort;
  }
  
  /**
   * A port event consumes a message on its respective used port.
   */
  @Override
  public void dropConsumedInputs() {
    usedPort.pollBuffer();
  }
}
