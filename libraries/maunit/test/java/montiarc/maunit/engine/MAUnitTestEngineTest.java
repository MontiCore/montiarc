/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.engine;

import montiarc.maunit.api.MaUnitTest;
import montiarc.maunit.api.MaUnitTestContext;
import montiarc.rte.behavior.AbstractBehavior;
import montiarc.rte.component.AbstractComponent;
import montiarc.rte.component.Component;
import montiarc.rte.port.InOutPort;
import montiarc.rte.port.InPort;
import montiarc.rte.port.OutPort;
import montiarc.rte.scheduling.Scheduler;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.List;
import java.util.Objects;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class MAUnitTestEngineTest {

  @Test
  void correctLifecycleEvents() {
    ExampleMaUnitTest.isLifecycleTest = true;

    EngineTestKit.engine("maunit").selectors(selectClass(ExampleMaUnitTest.class)).execute().testEvents().assertStatistics(stats ->
      stats.started(2)   // the example test method has 2 test cases
        .succeeded(1)    // 1 success
        .failed(1)       // and 1 failure
    );
  }

  @MaUnitTest(ExampleMaUnitTest.ExampleMaUnitTestContext.class)
  public static class ExampleMaUnitTest extends AbstractComponent<Object, AbstractBehavior<ExampleMaUnitTest, Object>> {

    public static boolean isLifecycleTest = false;

    public ExampleMaUnitTest(String name, Scheduler scheduler) {
      super(name, scheduler);
      this.scheduler.register(this, List.of(), List.of(tickPort));
    }

    @Override
    protected void handleMessageWithBehavior(InPort<?> p) {
    }

    @Override
    protected Object buildSyncMessage() {
      return null;
    }

    @Override
    public void init() {
    }

    @Override
    public void handleTick() {
      tickPort.dropBlockingTick();
      assert !isLifecycleTest || Objects.equals(getName(), "MaUnitTest:0");
    }

    @Override
    public List<Component> getAllSubcomponents() {
      return List.of();
    }

    @Override
    public List<InOutPort<?, ?>> getAllInPorts() {
      return List.of();
    }

    @Override
    public List<OutPort<?>> getAllOutPorts() {
      return List.of();
    }

    @Override
    protected List<InOutPort<?, ?>> getAllSyncedInPorts() { return List.of(); }

    @Override
    protected Object portValueOf(InPort<?> p) {
      return null;
    }

    public static class ExampleMaUnitTestContext implements MaUnitTestContext {

      @Override
      public int testCount() {
        return 2;
      }

      @Override
      public String getDisplayName(int testIndex) {
        return "MaUnitTest:" + testIndex;
      }
    }
  }
}
