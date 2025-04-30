/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.automata;

import montiarc.rte.deploy.mqtt.MockSimpleMqtt;
import montiarc.rte.deploy.mqtt.SimpleMqtt;
import montiarc.rte.tests.JSimTest;
import montiarc.types.OnOff;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@JSimTest
public class DeployMqttMediumTest {

  @Test
  public void testSimulationRuns() throws MqttException, InterruptedException {
    MockSimpleMqtt client = new MockSimpleMqtt();

    final MediumComp[] component = new MediumComp[1];

    DeployMqttMedium deployment = new DeployMqttMedium() {
      @Override
      protected SimpleMqtt initMqtt() {
        return client;
      }

      @Override
      protected void runSimulation(MediumComp c) {
        component[0] = c;
      }
    };

    deployment.deploy(null);


    client.publish("/Medium/inY", createOnOffMsg(OnOff.ON));
    client.publish("/Medium/inZ", createOnOffMsg(OnOff.ON));

    component[0].run(1);

    Assertions.assertEquals(0, client.publishedMessages.get("/Medium/outA").size());
    Assertions.assertEquals(0, client.publishedMessages.get("/Medium/outB").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outY").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outZ").size());

    component[0].run(1);

    // values of sync ports are constant until changed
    Assertions.assertEquals(0, client.publishedMessages.get("/Medium/outA").size());
    Assertions.assertEquals(0, client.publishedMessages.get("/Medium/outB").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outY").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outZ").size());

    // event ports
    client.publish("/Medium/inA", createOnOffMsg(OnOff.ON));
    component[0].run(1);

    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outA").size());
    Assertions.assertEquals(0, client.publishedMessages.get("/Medium/outB").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outY").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outZ").size());

    client.publish("/Medium/inB", createOnOffMsg(OnOff.ON));
    client.publish("/Medium/inB", createOnOffMsg(OnOff.ON));
    component[0].run(1);

    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outA").size());
    Assertions.assertEquals(2, client.publishedMessages.get("/Medium/outB").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outY").size());
    Assertions.assertEquals(1, client.publishedMessages.get("/Medium/outZ").size());
  }

  private static String createOnOffMsg(OnOff v) {
    return ("\"" + v.name() + "\"");
  }
}
