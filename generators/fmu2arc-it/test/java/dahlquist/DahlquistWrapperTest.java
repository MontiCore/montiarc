/* (c) https://github.com/MontiCore/monticore */
package dahlquist;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

// Tests that time is correctly processed for the fmus
@JSimTest
public class DahlquistWrapperTest {

  @Test
  @DisabledOnOs(architectures = "aarch64")
  public void testDahlquistFmu() {
    DahlquistWrapperComp sut = new DahlquistWrapperCompBuilder().setName("sut").build();

    PortObserver<Double> assertX = new PortObserver<>();
    sut.port_x().connect(assertX);

    long simulatedTickLength = 100_000_000;
    long ticks = 30; // Simulate 30 ticks to reach 3.0 seconds

    sut.run(ticks, simulatedTickLength);

    Assertions.assertEquals(30, assertX.getObservedValues().size());

    double x0 = 1.0;
    double k = 1.0;
    double dt = 0.1;
    double epsilon = 1e-9; // We can use a tiny epsilon because we are matching the exact math

    // Euler expectation: x(t) = x0 * (1 - k * dt)^N, where N is the tick count
    Assertions.assertEquals(x0 * Math.pow(1 - k * dt, 10), assertX.getObservedValues().get(9), epsilon); // t=1.0
    Assertions.assertEquals(x0 * Math.pow(1 - k * dt, 20), assertX.getObservedValues().get(19), epsilon); // t=2.0
    Assertions.assertEquals(x0 * Math.pow(1 - k * dt, 30), assertX.getObservedValues().get(29), epsilon); // t=3.0
  }
}
