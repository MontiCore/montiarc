/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.behavior;

public interface IBehavior {
  
  /**
   * React to a tick event.
   * Every behavior implementation should be able to react to a tick, even in untimed components.
   * Untimed behavior simply implements an empty method.
   */
  void tick();
}
