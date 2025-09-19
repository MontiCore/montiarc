/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.mqtt;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.deploy.util.DeSerializer;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.Receiver;

public class MqttOutPort<T> implements Receiver<T> {

  final String topic;
  final DeSerializer serializer;
  final SimpleMqtt client;

  public MqttOutPort(SimpleMqtt client, String topic, DeSerializer serializer) {
    this.topic = topic;
    this.serializer = serializer;
    this.client = client;
  }

  @Override
  public void receive(Message<? extends T> message) {
    try {
      if (!Tick.get().equals(message)) {
        client.publish(topic, serializer.serialize(message.getData()));
      }
    } catch (Exception e) {
      Log.error("Failed to publish message", e);
    }
  }
}
