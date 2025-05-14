/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;

public abstract class Deployment<T extends Component> {

  public void deploy(String[] args) {
    Log.initWARN();

    final T component = buildComponent();

    runSimulation(component);
  }

  protected void runSimulation(T component) {
    component.run();
  }

  protected abstract T buildComponent();
}
