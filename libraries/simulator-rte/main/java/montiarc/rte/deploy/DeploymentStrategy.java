/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import montiarc.rte.component.Component;
import montiarc.rte.deploy.util.DeSerializer;
import org.apache.commons.cli.Options;

import java.util.Map;

public interface DeploymentStrategy<T extends Component> {

  void connect(T component, Map<String, String> options);

  void disconnect();

  void setDeSerializer(DeSerializer deSerializer);

  default void addCLIOptions(Options options) {
  }
}
