/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.engine;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;


public class MAUnitTestExecutionContext implements EngineExecutionContext {
  final EngineExecutionListener executionListener;

  public MAUnitTestExecutionContext(EngineExecutionListener executionListener) {
    this.executionListener = executionListener;
  }

  public EngineExecutionListener getExecutionListener() {
    return executionListener;
  }
}
