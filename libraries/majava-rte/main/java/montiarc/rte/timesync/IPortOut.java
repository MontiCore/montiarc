/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public interface IPortOut<T> {

  T getValue();

  void setValue(T value);

  void update();

  void setSynced(Boolean synced);
}
