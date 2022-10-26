/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

/**
 * The interface of a component to its environment.
 *
 */
public interface IComponent {
  
  /**
   * Create members (subcomponents, ports, connectors).
   */
  void setUp();
  
  /**
   * Produces the initial output. Requires that the ports were created in the
   * subcomponents beforehand (see {@code setUp()}).
   */
  void init();
  
  /**
   * Called for the component to compute its next values.
   */
  void compute();
  
  /**
   * Switch to the next time slice, updating ports.
   */
  void tick();

  /**
   * @return true if all incoming ports are synced.
   * @see IInPort#isSynced()
   */
  boolean isSynced();
  
}
