/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.descriptior;

import montiarc.maunit.api.MaUnitTest;
import montiarc.maunit.api.MaUnitTestContext;
import montiarc.maunit.engine.MAUnitTestExecutionContext;
import montiarc.rte.Simulation;
import montiarc.rte.component.SimComponent;
import montiarc.rte.oracle.OracleFactory;
import montiarc.rte.scheduling.CoordinatingScheduler;
import org.codehaus.commons.nullanalysis.NotNull;
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

  protected Class<? extends SimComponent> testClass;

  protected MaUnitTestContext invocationContext;

  protected int iteration;

  protected MAUnitTestInvocationDescriptor(UniqueId uniqueId, MaUnitTestContext invocationContext, Class<? extends SimComponent> testClass, int iteration) {
    super(uniqueId, invocationContext.getDisplayName(iteration), ClassSource.from(testClass));
    this.testClass = testClass;
    this.invocationContext = invocationContext;
    this.iteration = iteration;
  }

  // --- TestDescriptor ------------------------------------------------------

  @NotNull
  @Override
  public Type getType() {
    return Type.TEST;
  }

  @NotNull
  @Override
  public String getLegacyReportingName() {
    return super.getLegacyReportingName() + "[" + iteration + "]";
  }

  // --- Node ----------------------------------------------------------------

  @NotNull
  @Override
  public MAUnitTestExecutionContext execute(MAUnitTestExecutionContext context, DynamicTestExecutor dynamicTestExecutor) throws Exception {
    OracleFactory oracleFactory = invocationContext.getOracleFactory(iteration);
    CoordinatingScheduler scheduler = new CoordinatingScheduler(oracleFactory);
    Simulation.nanosecondsPerTick = getSimulatedTickLength();
    Simulation.ticks = 0;
    SimComponent component = (SimComponent) testClass.getConstructors()[0].newInstance(getArguments(testClass.getConstructors()[0].getParameterCount(), scheduler, oracleFactory));
    boolean caughtException = false;
    try {
      component.run(getTickCount(), getSimulatedTickLength());
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

  protected Object[] getArguments(int size, CoordinatingScheduler scheduler, OracleFactory oracleFactory) {
    Object[] arguments = new Object[size];
    for (int i = 0; i < size; i++) {
      if (i == 0) {
        arguments[i] = this.getDisplayName();
      } else if (i == 1) {
        arguments[i] = scheduler;
      } else if (i == 2) {
        arguments[i] = oracleFactory;
      } else {
        arguments[i] = invocationContext.resolveParameter(iteration, i - 3);
      }
    }
    return arguments;
  }

  protected int getTickCount() {
    return invocationContext.getTickCount(iteration);
  }

  protected long getSimulatedTickLength() {
    return invocationContext.getSimulatedTickLength(iteration);
  }
}
