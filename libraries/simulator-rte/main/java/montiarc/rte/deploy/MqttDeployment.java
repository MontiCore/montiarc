/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;
import montiarc.rte.deploy.mqtt.SimpleMqtt;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public abstract class MqttDeployment<T extends Component> extends Deployment<T> {

  @Override
  public void deploy(String[] args) {
    Log.ensureInitialization();

    final T component = Objects.requireNonNull(buildComponent());
    final SimpleMqtt mqtt;
    try {
      mqtt = Objects.requireNonNull(initMqtt());
      connect(component, mqtt);
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }

    runSimulation(component);
  }

  @Override
  protected void runSimulation(T component) {
    component.runIndefinitely(msPerStep * 1000000);
  }

  protected SimpleMqtt initMqtt() throws MqttException {
    IMqttAsyncClient client = new MqttAsyncClient(
      System.getenv().getOrDefault("MQTT_BROKER_ADDRESS", "tcp://127.0.0.1:1883"),
      "MontiArcSimulation"
    );
    client.connect();
    while (!client.isConnected()) {}

    return new SimpleMqtt(client);
  }

  protected abstract void connect(T component, SimpleMqtt mqtt) throws MqttException;

  public String serialize(Object o) {
    ObjectMapper om = new ObjectMapper();
    try {
      return om.writeValueAsString(o);
    } catch (IOException ignored) {}

    return "";
  }

  public <R> Optional<R> deserialize(String s, Class<R> clazz) {
    ObjectMapper om = new ObjectMapper();

    try {
      return Optional.of(om.readValue(s, clazz));
    } catch (IOException ignored) {}
    return Optional.empty();
  }

  protected String getTopic(String componentType, String portName) {
    return "/" + componentType + "/" + portName;
  }
}
