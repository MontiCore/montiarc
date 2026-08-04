/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.SimComponent;
import montiarc.rte.scheduling.CoordinatingScheduler;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DeploymentCLITest {

  protected CoordinatingScheduler scheduler;
  protected TestDeployment deployment;

  protected ArgumentCaptor<Long> tickCapture;
  protected ArgumentCaptor<Long> simulationTickLengthCapture;
  protected ArgumentCaptor<Long> simulatedTickLengthCapture;

  @BeforeEach
  public void setup() {
    scheduler = Mockito.mock(CoordinatingScheduler.class);
    deployment = new TestDeployment(scheduler);
    tickCapture = ArgumentCaptor.forClass(Long.class);
    simulationTickLengthCapture = ArgumentCaptor.forClass(Long.class);
    simulatedTickLengthCapture = ArgumentCaptor.forClass(Long.class);
    Log.clearFindings();
  }

  public static class TestDeployment extends Deployment<SimComponent> {

    protected CoordinatingScheduler scheduler;

    public Map<String, String> receivedParams = Map.of();

    public TestDeployment(CoordinatingScheduler coordinatingScheduler) {
      scheduler = coordinatingScheduler;
    }

    @Override
    protected SimComponent buildComponent(CoordinatingScheduler scheduler, Map<String, String> parameters) {
      this.receivedParams = parameters;
      return Mockito.mock(SimComponent.class);
    }

    @Override
    protected CoordinatingScheduler buildCoordinatingScheduler() {
      return scheduler;
    }

    @Override
    protected void addOptionsForParameters(Options options) {
      options.addOption(
        Option.builder()
          .required(false)
          .longOpt("myParam")
          .desc("Sets the myParam component parameter")
          .hasArg()
          .argName("myParam")
          .get());
    }
  }

  @Test
  void testCliArgumentsAreParsedCorrectly() {
    String[] args = {"--tickCount", "123", "--simulationTickLength", "456",  "--simulatedTickLength", "789"};
    deployment.deploy(args);
    Mockito.verify(scheduler).run(Mockito.any(), Mockito.anyBoolean(), tickCapture.capture(), simulationTickLengthCapture.capture(), simulatedTickLengthCapture.capture());
    assertEquals(123, tickCapture.getValue().longValue());
    assertEquals(456000000, simulationTickLengthCapture.getValue().longValue());
    assertEquals(789000000, simulatedTickLengthCapture.getValue().longValue());
  }

  @Test
  void testCliDefaultsAreUsed() {
    deployment.deploy(new String[0]);
    Mockito.verify(scheduler).run(Mockito.any(), Mockito.anyBoolean(), tickCapture.capture(), simulationTickLengthCapture.capture(), simulatedTickLengthCapture.capture());
    assertEquals(Long.MIN_VALUE, tickCapture.getValue().longValue());
    assertEquals(1000000, simulationTickLengthCapture.getValue().longValue());
    assertEquals(0, simulatedTickLengthCapture.getValue().longValue());
  }

  @Test
  void testExtraPrimitiveParameterIsParsed() {
    String[] args = {"--tickCount", "10", "--simulationTickLength", "20", "--myParam", "value"};
    deployment.deploy(args);
    Mockito.verify(scheduler).run(Mockito.any(), Mockito.anyBoolean(), tickCapture.capture(), simulationTickLengthCapture.capture(), simulatedTickLengthCapture.capture());
    assertEquals("value", deployment.receivedParams.get("myParam"));
  }

  @Test
  void testHelpOptionPrintsHelpAndDoesNotRun() {
    deployment.deploy(new String[]{"--help"});

    Mockito.verify(scheduler, Mockito.times(0)).run(Mockito.any(), Mockito.anyBoolean(), tickCapture.capture(), simulationTickLengthCapture.capture(), simulatedTickLengthCapture.capture());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  void testInvalidTickCountDoesNotChangeValueAndPrintsError() {
    deployment.deploy(new String[]{"--tickCount", "bad", "--simulationTickLength", "100"});

    Mockito.verify(scheduler, Mockito.times(0)).run(Mockito.any(), Mockito.anyBoolean(), tickCapture.capture(), simulationTickLengthCapture.capture(), simulatedTickLengthCapture.capture());
    assertEquals(1, Log.getErrorCount());
  }

  @Test
  void testInvalidTickLengthDoesNotChangeValueAndPrintsError() {
    deployment.deploy(new String[]{"--tickCount", "10", "--simulationTickLength", "oops"});

    Mockito.verify(scheduler, Mockito.times(0)).run(Mockito.any(), Mockito.anyBoolean(), tickCapture.capture(), simulationTickLengthCapture.capture(), simulatedTickLengthCapture.capture());
    assertEquals(1, Log.getErrorCount());
  }
}
