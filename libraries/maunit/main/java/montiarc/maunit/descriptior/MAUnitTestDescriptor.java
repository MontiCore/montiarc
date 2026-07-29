/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.descriptior;

import montiarc.maunit.api.MaUnitTest;
import montiarc.maunit.api.MaUnitTestContext;
import montiarc.maunit.engine.MAUnitTestExecutionContext;
import montiarc.rte.component.SimComponent;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.engine.descriptor.DynamicDescendantFilter;
import org.junit.jupiter.engine.descriptor.Filterable;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * {@link TestDescriptor} for a {@link MaUnitTest @MaUnitTest}.
 */
public class MAUnitTestDescriptor extends AbstractTestDescriptor implements Filterable, Node<MAUnitTestExecutionContext> {

  private final DynamicDescendantFilter dynamicDescendantFilter = new DynamicDescendantFilter();
  protected Class<? extends SimComponent> testClass;

  public MAUnitTestDescriptor(UniqueId uniqueId, Class<? extends SimComponent> testClass) {
    super(uniqueId, testClass.getName().substring(0, testClass.getName().length() - 4), ClassSource.from(testClass));
    this.testClass = testClass;
  }

  // --- Filterable ----------------------------------------------------------

  @NotNull
  @Override
  public DynamicDescendantFilter getDynamicDescendantFilter() {
    return dynamicDescendantFilter;
  }

  // --- TestDescriptor ------------------------------------------------------

  @NotNull
  @Override
  public Type getType() {
    return Type.CONTAINER;
  }

  @Override
  public boolean mayRegisterTests() {
    return true;
  }

  // --- Node ----------------------------------------------------------------

  @NotNull
  @Override
  public MAUnitTestExecutionContext execute(MAUnitTestExecutionContext context, DynamicTestExecutor dynamicTestExecutor) {
    MaUnitTestContext testContext = createTestContext();
    for (int i = 0; i < testContext.testCount(); i++) {
      Optional<TestDescriptor> testDescription = createInvocationTestDescriptor(testContext, i);
      testDescription.ifPresent(testDescriptor -> execute(dynamicTestExecutor, testDescriptor));
    }
    return context;
  }

  protected Optional<TestDescriptor> createInvocationTestDescriptor(MaUnitTestContext invocationContext, int index) {
    UniqueId uniqueId = getUniqueId().append(MAUnitTestInvocationDescriptor.SEGMENT_TYPE, "#" + index);
    if (getDynamicDescendantFilter().test(uniqueId, index - 1)) {
      return Optional.of(new MAUnitTestInvocationDescriptor(uniqueId, invocationContext, testClass, index));
    }
    return Optional.empty();
  }

  protected MaUnitTestContext createTestContext() {
    try {
      return AnnotationSupport.findAnnotation(testClass, MaUnitTest.class).get().value().getDeclaredConstructor().newInstance();
    } catch (InvocationTargetException | InstantiationException |
             IllegalAccessException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  protected void execute(DynamicTestExecutor dynamicTestExecutor, TestDescriptor testDescriptor) {
    testDescriptor.setParent(this);
    dynamicTestExecutor.execute(testDescriptor);
  }
}
