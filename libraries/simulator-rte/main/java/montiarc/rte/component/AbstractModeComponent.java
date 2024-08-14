/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.behavior.IBehavior;
import montiarc.rte.port.IInPort;
import montiarc.rte.scheduling.Scheduler;


/**
 * Customization of {@link AbstractComponent} for dynamic components with modes.
 * @param <I> the type of the synced input message class
 *            (containing a message for every port at the time of a synced tick)
 * @param <B> the class defining the interface of the behavior (accepting tick and message events)
 * @param <ModeC> the class of the mode automaton
 */
public abstract class AbstractModeComponent<I, B extends IBehavior<I>, ModeC extends IBehavior<I>> extends AbstractComponent<I, B> {

  protected ModeC modeAutomaton;

  protected ModeC getModeAutomaton() {
    return this.modeAutomaton;
  }

  @Override
  public boolean hasModeAutomaton() { return true; }

  protected AbstractModeComponent(String name, Scheduler scheduler) {
    super(name, scheduler);
  }

  @Override
  public void init() {
    super.init();
    modeAutomaton.init();
  }

  @Override
  public void handleMessage(IInPort<?> p) {
    this.handleMessageWithModeAutomaton(p);
    super.handleMessage(p);
  }

  /**
   * To be overwritten by timed components, calling the mode automaton reaction to the message on port <i>p</i>
   */
  protected void handleMessageWithModeAutomaton(montiarc.rte.port.IInPort<?> p) {
    // Empty default behavior for synced components that don't react to message triggers
  }

  @Override
  protected void handleSyncedTickExecution() {
    modeAutomaton.tick(buildSyncMessage());
    super.handleSyncedTickExecution();
  }

  @Override
  protected void handleEventTickExecution() {
    modeAutomaton.tick(null);
    super.handleEventTickExecution();
  }
}
