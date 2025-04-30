/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.mqtt;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.se_rwth.commons.logging.Log;
import org.eclipse.paho.client.mqttv3.*;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Wrapper around an MQTT Client
 */
public class SimpleMqtt implements MqttCallback {

  protected final Multimap<String, Consumer<MqttMessage>> actions = ArrayListMultimap.create();
  protected final IMqttClient client;
  protected final IMqttAsyncClient asyncClient;
  protected final boolean isAsync;

  /**
   * For testing only
   */
  SimpleMqtt() {
    client = null;
    asyncClient = null;
    isAsync = false;
  }

  public SimpleMqtt(IMqttClient client) {
    this.client = client;
    this.asyncClient = null;
    isAsync = false;
    client.setCallback(this);
  }

  public SimpleMqtt(IMqttAsyncClient client) {
    this.asyncClient = client;
    this.client = null;
    isAsync = true;
    client.setCallback(this);
  }

  protected Closeable subscribeMqtt(String topic, Consumer<MqttMessage> action) throws MqttException {
    if (isAsync) {
      asyncClient.subscribe(topic, 1);
    } else {
      client.subscribe(topic);
    }
    actions.put(topic, action);
    return () -> actions.remove(topic, action);
  }

  public Closeable subscribe(String topic, Consumer<String> action) throws MqttException {
    return subscribeMqtt(topic, msg ->
      action.accept(new String(msg.getPayload(), StandardCharsets.UTF_8))
    );
  }

  @Override
  public void connectionLost(Throwable throwable) {
    Log.error("Connection to mqtt lost", throwable);
  }

  @Override
  public void messageArrived(String s, MqttMessage mqttMessage) {
    actions.get(s).forEach(a -> a.accept(mqttMessage));
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

  }

  public void publish(String topic, String content) throws MqttException {
    MqttMessage msg = new MqttMessage(content.getBytes(StandardCharsets.UTF_8));
    if (isAsync) {
      asyncClient.publish(topic, msg);
    } else {
      client.publish(topic, msg);
    }
  }
}
