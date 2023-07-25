/* (c) https://github.com/MontiCore/monticore */
package montiarc.scheduling;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.scheduling.Computation;
import montiarc.rte.scheduling.FifoSchedule;
import montiarc.rte.scheduling.ISchedule;
import montiarc.rte.scheduling.InstantSchedule;
import montiarc.rte.scheduling.LifoSchedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class SchedulerTest {

  ITimedComponent mockSource;
  TimeAwareOutPort<Boolean> input;
  StringBuilder trace;

  @BeforeEach
  public void beforeEach() {
    mockSource = new ITimedComponent() {
      @Override
      public List<TimeAwareInPort<?>> getAllInPorts() {
        return List.of();
      }

      @Override
      public List<TimeAwareOutPort<?>> getAllOutPorts() {
        return List.of();
      }

      @Override
      public String getName() {
        return "mockSource";
      }
    };
    input = new TimeAwareOutPort<>("input");
    trace = new StringBuilder();
  }

  @ParameterizedTest
  @MethodSource("methodSrc")
  public void proofOfConceptTest(ISchedule scheduler, String expectedTrace) {
    Scheduled scheduledComponent = new Scheduled("scheduledComponent", scheduler, trace);
    input.connect(scheduledComponent.trigger);

    // if we send the message while the scheduler is not in a computation, the order would change
    // (b/c the computations of the subcomponents would each be scheduled/executed instantly)
    scheduler.registerComputation(new Computation(mockSource, () -> input.send(true)));

    Assertions.assertEquals(expectedTrace, trace.toString());
  }

  public static Stream<Arguments> methodSrc() {
    return Stream.of(
      Arguments.of(new FifoSchedule(), "a1a2b1b2c1c2d1d2e1e2f1f2g1g2h1h2i1i2j1j2k1k2"), // no overlapping computations: string matches RegEx ((.)1\22)+
      Arguments.of(new LifoSchedule(), "c1c2g1g2k1k2b1b2f1f2j1j2e1e2i1i2a1a2d1d2h1h2"), // no overlapping computations: string matches RegEx ((.)1\22)+
      Arguments.of(new InstantSchedule(), "a1d1h1h2d2a2b1e1i1i2e2f1j1j2f2b2c1g1k1k2g2c2") // overlapping computations: string does not match RegEx ((.)1\22)+
    );
  }
}
