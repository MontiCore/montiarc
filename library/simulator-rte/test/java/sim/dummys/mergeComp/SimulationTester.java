/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import sim.error.ISimulationErrorHandler;
import sim.error.SimpleErrorHandler;
import sim.message.StreamPrinter;
import sim.message.Tick;
import sim.port.TestPort;
import sim.sched.IScheduler;
import sim.sched.SchedulerFactory;
import sim.serialiser.BackTrackHandler;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Merge Component
 */
public class SimulationTester {

  @Test
  public void botfirst() {
    IScheduler scheduler = SchedulerFactory.createDefaultScheduler();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();
    BackTrackHandler backTrackHandler = new BackTrackHandler("serialser/");
    MergeCompBotFirst mc = new MergeCompBotFirst();

    mc.setup(scheduler, errorHandler, backTrackHandler);

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
    BackTrackHandler backTrackHandler = new BackTrackHandler("serialser/");
    MergeCompTopFirst mc = new MergeCompTopFirst();

    mc.setup(scheduler, errorHandler, backTrackHandler);

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
    BackTrackHandler backTrackHandler = new BackTrackHandler("serialser/");
    MergeCompUnderSpeci mc = new MergeCompUnderSpeci();

    mc.setup(scheduler, errorHandler, backTrackHandler);

    int input = 2;
    for (int i = 0; i < 10; i++) {
      mc.getin().accept(input);
      mc.getin().accept(Tick.get());
    }

    System.out.println(StreamPrinter.print(((TestPort) mc.getout()).getStream()));

    assertTrue(!((TestPort) mc.getout()).getStream().isEmpty());
  }

  @AfterEach
  public void cleanup() throws IOException {
    FileUtils.cleanDirectory(new File("serialser"));
  }
}
