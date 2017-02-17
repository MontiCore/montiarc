package de.montiarcautomaton.runtimes.timesync.delegation;

/**
 * The interface of a component to its environment.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public interface IComponent {
  
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
  
}
