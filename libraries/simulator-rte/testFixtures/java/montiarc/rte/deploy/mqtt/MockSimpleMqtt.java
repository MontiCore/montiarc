/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.mqtt;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class MockSimpleMqtt extends SimpleMqtt {

  public Multimap<String, MqttMessage> publishedMessages = ArrayListMultimap.create();

  public MockSimpleMqtt() {
    super();
  }

  @Override
  protected Closeable subscribeMqtt(String topic, Consumer<MqttMessage> action) throws MqttException {
    actions.put(topic, action);
    return () -> actions.remove(topic, action);
  }

  @Override
  public void publish(String topic, String content) throws MqttException {
    MqttMessage msg = new MqttMessage(content.getBytes(StandardCharsets.UTF_8));
    publishedMessages.put(topic, msg);
    messageArrived(topic, msg);
  }
}
