/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.behavior;

/**
 * Base class for behavior implementations.
 * Makes two major assumptions:
 * <ol>
 *   <li>Every instance of a behavior implementation belongs to a component instance, reflected in the field {@link #context}.</li>
 *   <li>Every behavior implementation should be able to react to a tick event.</li>
 * </ol>
 * @param <C> the type of the owning component
 */
public abstract class AbstractBehavior<C> implements IBehavior {
  
  protected C context;
  
  public AbstractBehavior(C context) {
    this.context = context;
  }
  
  /**
   * @return the context (i.e., owning component) of the behavior implementation
   */
  protected C getContext() {
    return this.context;
  }
}
