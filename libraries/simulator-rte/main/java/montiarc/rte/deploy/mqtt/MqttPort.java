/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.mqtt;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;
import montiarc.rte.deploy.util.DeSerializer;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.port.InPort;

import java.util.function.Function;

public class MqttPort<T> implements InPort<T> {

  final String topic;
  final DeSerializer serializer;
  final SimpleMqtt client;

  public MqttPort(SimpleMqtt client, String topic, DeSerializer serializer) {
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

  @Override
  public Message<T> peekBuffer() {
    return null;
  }

  @Override
  public Message<T> pollBuffer() {
    return null;
  }

  @Override
  public Message<T> peekLastBuffer() {
    return null;
  }

  @Override
  public Message<T> pollLastBuffer() {
    return null;
  }

  @Override
  public boolean isBufferEmpty() {
    return false;
  }

  @Override
  public boolean hasBufferedTick() {
    return false;
  }

  @Override
  public void dropMessagesIgnoredBySync() {
  }

  @Override
  public String getQualifiedName() {
    return "";
  }

  @Override
  public Component getOwner() {
    return null;
  }
}
