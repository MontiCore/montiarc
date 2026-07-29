/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.engine;

import montiarc.maunit.api.MaUnitTest;
import montiarc.maunit.descriptior.MAUnitTestDescriptor;
import montiarc.rte.component.AbstractComponent;
import montiarc.rte.component.SimComponent;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

/**
 * The JUnit MontiArc unit {@link org.junit.platform.engine.TestEngine TestEngine}.
 */
public class MAUnitTestEngine extends HierarchicalTestEngine<MAUnitTestExecutionContext> {

  @NotNull
  @Override
  public String getId() {
    return "maunit";
  }

  @NotNull
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
    EngineDescriptor engineDescriptor = new EngineDescriptor(uniqueId, "MontiArc Unit Test");

    request.getSelectorsByType(ClassSelector.class).forEach(selector -> appendTestsInClass(selector.getJavaClass(), engineDescriptor));

    return engineDescriptor;
  }

  private void appendTestsInClass(Class<?> c, EngineDescriptor engineDescriptor) {
    if (AnnotationSupport.isAnnotated(c, MaUnitTest.class) && AbstractComponent.class.isAssignableFrom(c)) {
      MAUnitTestDescriptor classTestDescriptor = new MAUnitTestDescriptor(engineDescriptor.getUniqueId().append("component", c.getName()), (Class<? extends SimComponent>) c);
      engineDescriptor.addChild(classTestDescriptor);
    }
  }

  @Override
  protected MAUnitTestExecutionContext createExecutionContext(ExecutionRequest request) {
    return new MAUnitTestExecutionContext(request.getEngineExecutionListener());
  }
}
