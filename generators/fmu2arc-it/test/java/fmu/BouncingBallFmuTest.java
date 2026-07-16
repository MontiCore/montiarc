/* (c) https://github.com/MontiCore/monticore */
package fmu;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import static montiarc.rte.msg.MessageFactory.msg;

// Tests if tunable Parameter is changeable at Runtime
// Doesnt check specific Values because not feasible with this fmu
@JSimTest
public class BouncingBallFmuTest {

  @Test
  @DisabledOnOs(architectures = "aarch64")
  public void testTunableRestitutionAcceptedDuringSimulation() {
    BouncingBallComp sut = new BouncingBallCompBuilder().setName("sut").set_param_g(-9.81).build();

    PortObserver<Double> assertV = new PortObserver<>();
    sut.port_v().connect(assertV);

    sut.port_e().receive(msg(0.5));

    long simulatedTickLength = 100_000_000; // 0.1s

    // run a few ticks, then change e mid-simulation (before ground contact)
    sut.run(2, simulatedTickLength);
    sut.port_e().receive(msg(0.9));
    sut.run(2, simulatedTickLength);

    // simulation should continue without error and keep producing output
    Assertions.assertEquals(4, assertV.getObservedValues().size());
  }
}
