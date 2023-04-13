/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.event;

/**
 * Represents a tick event in an event-based MontiArc component.
 */
public class TickEvent extends Event {

  /**
   * Check whether this is equal to a given other event.
   * In this case the only check is whether the other event is also a {@link TickEvent}.
   *
   * @param other the other event
   * @return true if the other event is also a TickEvent
   */
  @Override
  protected boolean equalsOtherEvent(Event other) {
    return other instanceof TickEvent;
  }

  /**
   * A tick event does not consume any inputs, so none need to be dropped.
   */
  @Override
  public void dropConsumedInputs() {}
}
