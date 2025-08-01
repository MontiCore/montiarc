/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.rte.deploy.mqtt.MockSimpleMqtt;
import montiarc.rte.deploy.mqtt.SimpleMqtt;
import montiarc.rte.deploy.util.DeSerializer;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

@JSimTest
public class DeployMqttParameterTest {

  @Test
  public void testParameterDeploy() throws MqttException, InterruptedException {
    MockSimpleMqtt client = new MockSimpleMqtt();

    final ParameterComp[] component = new ParameterComp[1];

    DeployParameter deployment =
      new DeployParameter(new DeployMqttParameter() {
        @Override
        protected SimpleMqtt initMqtt(Map<String, String> options) throws MqttException {
          return client;
        }
      }) {
        @Override
        protected ParameterComp buildComponent(
          montiarc.rte.scheduling.CoordinatingScheduler scheduler,
          java.util.Map<String, String> parameters) {
          component[0] = super.buildComponent(scheduler, parameters);
          return component[0];
        }
      };

    deployment.deploy(new String[]{
      "--tickCount", "0",
      "--p", new DeSerializer().serialize(OnOff.ON),
    });

    component[0].run(1);

    Assertions.assertEquals(1, client.publishedMessages.get("/Parameter/o").size());
    String payload = new String(client.publishedMessages
      .get("/Parameter/o")
      .iterator()
      .next()
      .getPayload());
    Assertions.assertEquals(new DeSerializer().serialize(OnOff.ON), payload);
  }
}
