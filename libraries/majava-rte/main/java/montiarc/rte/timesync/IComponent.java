/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

/**
 * The interface of a component to its environment.
 *
 */
public interface IComponent {
  
  /**
   * First method to be called. Creates members (e.g., ports). Propagated to
   * subcomponents.
   */
  public void setUp();
  
  /**
   * Second method to be called. Connects ports of subcomponents to another.
   * Requires that these were created in the subcomponents before (see
   * {@code setUp()}). Propagated to subcomponents.
   */
  public void init();
  
  /**
   * Called for the component to compute its next values. Propagated to
   * subcomponents.
   */
  public void compute();
  
  /**
   * Causes the component to update its outgoing ports and variables with the
   * newly computed values. Requires that {@code compute()} was performed
   * before. Propagated to subcomponents.
   */
  public void update();

  /**
   * Check whether all input ports are synced, i.e., whether the computation can be triggered.
   *
   * @see Port#synced
   */
  public boolean allInputsSynced();
  
}
