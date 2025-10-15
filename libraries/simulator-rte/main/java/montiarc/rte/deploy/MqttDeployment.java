/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import com.google.common.base.Preconditions;
import montiarc.rte.component.Component;
import montiarc.rte.deploy.mqtt.SimpleMqtt;
import montiarc.rte.deploy.util.DeSerializer;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Map;
import java.util.Objects;

public abstract class MqttDeployment<T extends Component> implements DeploymentStrategy<T> {

  protected SimpleMqtt mqtt;
  protected DeSerializer deSerializer;

  @Override
  public void setDeSerializer(DeSerializer deSerializer) {
    this.deSerializer = deSerializer;
  }

  @Override
  public void connect(T component, Map<String, String> options) {
    try {
      this.mqtt = Preconditions.checkNotNull(initMqtt(options));
      setupMqttConnections(component);
    } catch (MqttException e) {
      throw new RuntimeException("Failed to initialize MQTT client", e);
    }
  }

  protected abstract void setupMqttConnections(T component) throws MqttException;

  protected SimpleMqtt initMqtt(Map<String, String> options) throws MqttException {
    IMqttAsyncClient client = new MqttAsyncClient(
      options.getOrDefault("MQTT_BROKER_ADDRESS", "tcp://127.0.0.1:1883").replace("\"", ""),
      "MontiArcSimulation"
    );
    client.connect();
    while (!client.isConnected()) { }

    return new SimpleMqtt(client);
  }

  protected String getTopic(String componentType, String portName) {
    return "/" + componentType + "/" + portName;
  }

  @Override
  public void disconnect() {
    try {
      this.mqtt.disconnect();
    } catch (MqttException e) {
      throw new RuntimeException("Failed to disconnect MQTT client", e);
    }
    mqtt = null;
  }

  @Override
  public void addCLIOptions(Options options) {
    options.addOption(Option.builder().longOpt("mqttBroker")
      .required(false)
      .desc("Sets the mqtt broker address (by default: \"tcp://127.0.0.1:1883\")")
      .hasArg().argName("url")
      .get());
  }
}
