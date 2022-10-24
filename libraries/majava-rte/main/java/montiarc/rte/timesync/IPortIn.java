/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public interface IPortIn<T> {

  T getValue();

  Boolean isSynced();
}
