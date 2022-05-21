/* (c) https://github.com/MontiCore/monticore */
package sim.backtracking;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sim.automaton.AutomataState;
import sim.automaton.ComponentState;
import sim.dummys.ComponentPortTest;
import sim.error.SimpleErrorHandler;
import sim.message.TickedMessage;
import sim.port.IPort;
import sim.port.SimplePort;
import sim.port.SimulationScheduler;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class BackTrackingHandlerTest {

  protected SimplePort<String> testling;

  protected ComponentPortTest comp;

  protected IScheduler scheduler;

  BackTrackHandler bth;

  @BeforeEach
  public void setUp() {
    new File("target/test-sources/").mkdir();
    comp = new ComponentPortTest();
    testling = new SimplePort<String>();
    IScheduler s = new SimulationScheduler();
    bth = new BackTrackHandler("target/test-sources/serialser/");
    comp.setup(s, new SimpleErrorHandler(), bth);
    testling.setup(comp, s);
  }

  @Test
  public void testSaveandLoad() {
    comp.setComponentName("TestComp");
    Map<IPort, List<TickedMessage>> outmsgs = new HashMap<>();

    ComponentState cs = new ComponentState(AutomataState.ONE, null, null, null, null);
    bth.saveComponentState(comp, cs);
    ComponentState cs2 = new ComponentState(AutomataState.TWO, null, null, null, null);
    bth.saveComponentState(comp, cs2);
    Stack<ComponentState> streamIn = bth.loadComponentState(comp);
    assert (streamIn.pop().getCurrentState().equals(AutomataState.TWO));
    assert (streamIn.pop().getCurrentState().equals(AutomataState.ONE));
  }

  @AfterEach
  public void cleanup() throws IOException {
    FileUtils.cleanDirectory(new File("target/test-sources/serialser/"));
  }

}
