/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.behavior;

/**
 * Base class for behavior implementations.
 * Makes the assumption that every instance of a behavior implementation belongs to a component instance,
 * reflected in the field {@link #context}.
 *
 * @param <C> the type of the owning component
 * @param <I> the Sync message type of the behavior.
 *           Its objects conclude the values of the input ports at the time of a tick.
 *           This type must also be bound for event behaviors as part of the 150% modeling that
 *           the generator currently implements in order to model variability.
 */
public abstract class AbstractBehavior<C, I> implements Behavior<I> {
  
  protected C context;

  protected String name;

  public String getName() {
    return this.name;
  }
  
  protected AbstractBehavior(C context, String name) {
    this.context = context;
    this.name = name;
  }
  
  /**
   * @return the context (i.e., owning component) of the behavior implementation
   */
  protected C getContext() {
    return this.context;
  }
}
