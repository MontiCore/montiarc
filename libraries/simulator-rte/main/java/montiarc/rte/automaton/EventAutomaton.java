/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * @param <C> the type of the owning component
 * @param <I> the Sync message type of the behavior.
 *            Its objects conclude the values of the input ports at the time of a tick.
 *            This type must also be bound for event behaviors as part of the 150% modeling that
 *            the generator currently implements in order to model variability.
 */
public abstract class EventAutomaton<C, I> extends Automaton<C, I> {

  protected EventAutomaton(C context,
                           State initial,
                           String name) {
    super(context, initial, name);
  }
}
