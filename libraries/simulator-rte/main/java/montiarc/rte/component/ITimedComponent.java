/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

public interface ITimedComponent extends IComponent {

  void init();

  void handleTick();

}
