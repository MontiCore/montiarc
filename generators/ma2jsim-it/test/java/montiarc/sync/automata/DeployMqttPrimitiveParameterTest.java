/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.rte.deploy.mqtt.MockSimpleMqtt;
import montiarc.rte.deploy.mqtt.SimpleMqtt;
import montiarc.rte.tests.JSimTest;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

@JSimTest
public class DeployMqttPrimitiveParameterTest {

  @Test
  public void testParameterDeploy() throws MqttException, InterruptedException {
    MockSimpleMqtt client = new MockSimpleMqtt();

    final PrimitiveParameterComp[] component = new PrimitiveParameterComp[1];

    DeployPrimitiveParameter deployment =
      new DeployPrimitiveParameter(new DeployMqttPrimitiveParameter() {
        @Override
        protected SimpleMqtt initMqtt(Map<String, String> options) throws MqttException {
          return client;
        }
      }) {
        @Override
        protected PrimitiveParameterComp buildComponent(
          montiarc.rte.scheduling.CoordinatingScheduler scheduler,
          java.util.Map<String, String> parameters) {
          component[0] = super.buildComponent(scheduler, parameters);
          return component[0];
        }
      };

    deployment.deploy(new String[]{
      "--tickCount", "0",
      "--p", "42"
    });

    component[0].run(1);

    Assertions.assertEquals(1, client.publishedMessages.get("/PrimitiveParameter/o").size());

    String payload = new String(
        client.publishedMessages
              .get("/PrimitiveParameter/o")
              .iterator()
              .next()
              .getPayload());

    Assertions.assertEquals("42", payload);
  }
}
