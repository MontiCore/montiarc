/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.event;

/**
 * Represents an event that can trigger an action in an event-based MontiArc component.
 */
public abstract class Event {
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Event && this.equalsOtherEvent((Event) obj);
  }
  
  /**
   * Check whether this is equal to a given other event.
   * Used to force effective overriding of {@link #equals(Object)}.
   *
   * @param other the other event
   *
   * @return true if this equals the given other event
   */
  protected abstract boolean equalsOtherEvent(Event other);
  
  /**
   * If an event depends on certain inputs, they need to be dropped after the event has been handled.
   * This should happen in this method since we do not need to expose the respective inputs to the outside world.
   */
  public abstract void dropConsumedInputs();
}
