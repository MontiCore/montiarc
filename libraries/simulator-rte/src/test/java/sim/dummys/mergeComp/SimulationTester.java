/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import org.junit.jupiter.api.Test;
import sim.sched.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.error.SimpleErrorHandler;
import sim.message.StreamPrinter;
import sim.message.Tick;
import sim.port.TestPort;
import sim.sched.SchedulerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Merge Component
 */
public class SimulationTester {

  @Test
  public void botfirst() {
    IScheduler scheduler = SchedulerFactory.createDefaultScheduler();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();
    MergeCompBotFirst mc = new MergeCompBotFirst();

    mc.setup(scheduler, errorHandler);

    int input = -1;
    for (int i = 0; i < 10; i++) {
      mc.getin().accept(input);
      input = -input;
      mc.getin().accept(Tick.get());
    }

    System.out.println(StreamPrinter.print(((TestPort) mc.getout()).getStream()));

    assertTrue(!((TestPort) mc.getout()).getStream().isEmpty());
  }

  @Test
  public void topFirst() {
    IScheduler scheduler = SchedulerFactory.createDefaultScheduler();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();
    MergeCompTopFirst mc = new MergeCompTopFirst();

    mc.setup(scheduler, errorHandler);

    int input = -1;
    for (int i = 0; i < 10; i++) {
      mc.getin().accept(input);
      input = -input;
      mc.getin().accept(Tick.get());
    }

    System.out.println(StreamPrinter.print(((TestPort) mc.getout()).getStream()));

    assertTrue(!((TestPort) mc.getout()).getStream().isEmpty());
  }

  @Test
  public void concreteInputUnderSpeci() {
    IScheduler scheduler = SchedulerFactory.createDefaultScheduler();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();
    MergeCompUnderSpeci mc = new MergeCompUnderSpeci();

    mc.setup(scheduler, errorHandler);

    int input = 2;
    for (int i = 0; i < 10; i++) {
      mc.getin().accept(input);
      mc.getin().accept(Tick.get());
    }

    System.out.println(StreamPrinter.print(((TestPort) mc.getout()).getStream()));

    assertTrue(!((TestPort) mc.getout()).getStream().isEmpty());
  }
}
