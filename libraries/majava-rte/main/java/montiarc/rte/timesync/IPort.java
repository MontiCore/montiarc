/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public interface IPort<T> {

  /**
   * switch to the next time slice
   */
  void tick();

  /**
   * @return the value present at the current time slice
   */
  T getValue();
}
