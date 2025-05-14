/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.descriptior;

import montiarc.lang.Simulation;
import montiarc.maunit.api.MaUnitTest;
import montiarc.maunit.api.MaUnitTestContext;
import montiarc.maunit.engine.MAUnitTestExecutionContext;
import montiarc.rte.component.AbstractComponent;
import montiarc.rte.port.ScheduledPort;
import montiarc.rte.scheduling.CoordinatingScheduler;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;

/**
 * {@link TestDescriptor} for a single {@link MaUnitTest @MaUnitTest} invocation.
 */
public class MAUnitTestInvocationDescriptor extends AbstractTestDescriptor implements Node<MAUnitTestExecutionContext> {

  public static final String SEGMENT_TYPE = "matest-invocation";

  protected Class<? extends AbstractComponent<?, ?>> testClass;

  protected MaUnitTestContext invocationContext;

  protected int iteration;

  protected MAUnitTestInvocationDescriptor(UniqueId uniqueId, MaUnitTestContext invocationContext, Class<? extends AbstractComponent<?, ?>> testClass, int iteration) {
    super(uniqueId, invocationContext.getDisplayName(iteration), ClassSource.from(testClass));
    this.testClass = testClass;
    this.invocationContext = invocationContext;
    this.iteration = iteration;
  }

  // --- TestDescriptor ------------------------------------------------------

  @Override
  public Type getType() {
    return Type.TEST;
  }

  @Override
  public String getLegacyReportingName() {
    return super.getLegacyReportingName() + "[" + iteration + "]";
  }

  // --- Node ----------------------------------------------------------------

  @Override
  public MAUnitTestExecutionContext execute(MAUnitTestExecutionContext context, DynamicTestExecutor dynamicTestExecutor) throws Exception {
    CoordinatingScheduler scheduler = new CoordinatingScheduler();
    AbstractComponent<?, ?> component = (AbstractComponent<?, ?>) testClass.getConstructors()[0].newInstance(getArguments(testClass.getConstructors()[0].getParameterCount(), scheduler));
    boolean caughtException = false;
    try {
      Simulation.ticks = 0;
      component.init();
      component.run(getTickCount());
    } catch (Throwable e) {
      caughtException = true;
      String schedulerTrace = "\nafter <" + Simulation.ticks + "> ticks.";
      if (invocationContext.isExceptionExpected(iteration)) {
        if (!invocationContext.getExpectedException(iteration).isInstance(e)) {
          throw new AssertionError("Expected throwable <" + invocationContext.getExpectedException(iteration).getName() + "> but was <" + e.getClass().getName() + ">" + schedulerTrace, e);
        }
      } else if (e instanceof AssertionError) {
        AssertionError ex = new AssertionError(e.getMessage() + schedulerTrace);
        ex.setStackTrace(e.getStackTrace());
        throw ex;
      } else throw e;
    }
    if (invocationContext.isExceptionExpected(iteration) && !caughtException)
      throw new AssertionError("Expected throwable <" + invocationContext.getExpectedException(iteration).getName() + "> was not thrown\nafter <" + Simulation.ticks + "> ticks.");
    return context;
  }

  protected Object[] getArguments(int size, CoordinatingScheduler scheduler) {
    Object[] arguments = new Object[size];
    for (int i = 0; i < size; i++) {
      if (i == 0) {
        arguments[i] = this.getDisplayName();
      } else if (i == 1) {
        arguments[i] = scheduler;
      } else {
        arguments[i] = invocationContext.resolveParameter(iteration, i);
      }
    }
    return arguments;
  }

  protected int getTickCount() {
    return invocationContext.getTickCount(iteration);
  }
}
