/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import montiarc.rte.component.AbstractComponent;

public abstract class Deployment<T extends AbstractComponent<?, ?>> {

  public void deploy(String[] args) {
    de.se_rwth.commons.logging.Log.initWARN();

    final T component = buildComponent();

    runSimulation(component);
  }

  protected void runSimulation(T component) {
    component.run();
  }

  protected abstract T buildComponent();
}
