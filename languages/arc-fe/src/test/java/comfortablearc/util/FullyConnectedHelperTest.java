/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.util;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import comfortablearc.AbstractTest;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Holds tests for methods of {@link ComfortableArcNodeHelper} targeting utility
 * functions of concepts around fully connected component instantiations.
 */
public class FullyConnectedHelperTest extends AbstractTest {

  protected static Map<String, ASTComponentType> compTypes = new HashMap<>();

  protected static Map<String, ASTComponentType> getCompTypes() {
    return compTypes;
  }

  protected static void putCompType(@NotNull ASTComponentType compType) {
    Preconditions.checkArgument(compType != null);
    getCompTypes().put(compType.getName(), compType);
  }

  protected static void putCompTypes(@NotNull ASTComponentType... componentTypes) {
    Preconditions.checkArgument(componentTypes != null);
    Preconditions.checkArgument(!Arrays.asList(componentTypes).contains(null));
    Arrays.stream(componentTypes).forEach(FullyConnectedHelperTest::putCompType);
  }

  @BeforeAll
  public static void setUpCompTypes() {
    ASTComponentType a = createCompType("A");
    a.getBody().addArcElement(createCompInstantiation(false, "a1"));
    ASTComponentType b = createCompType("B");
    b.getBody().addArcElement(createCompInstantiation(false, "a1"));
    b.getBody().addArcElement(createCompInstantiation(true, "a2"));
    ASTComponentType c = createCompType("C");
    c.getBody().addArcElement(createCompInstantiation(true, "a1", "a2"));
    c.getBody().addArcElement(createCompInstantiation(false, "a3", "a4"));
    c.getBody().addArcElement(createCompInstantiation(true, "a5"));
    putCompTypes(a, b, c);
  }

  protected static Stream<Arguments> compTypeAndFullyConnectedInstanceProvider() {
    return Stream.of(Arguments.of("B", "a2"), Arguments.of("C", "a1"), Arguments.of("C", "a2"),
      Arguments.of("C", "a5"));
  }

  protected static Stream<Arguments> compTypeAndNotFullyConnectedInstanceProvider() {
    return Stream.of(Arguments.of("A", "a1"), Arguments.of("B", "a1"), Arguments.of("C", "a3"),
      Arguments.of("C", "a4"));
  }

  protected static ASTComponentInstantiation createCompInstantiation(boolean fullyConnected,
                                                                     @NotNull String... instances) {
    Preconditions.checkArgument(instances != null);
    Preconditions.checkArgument(!Arrays.asList(instances).contains(null));
    return createCompInstantiation(fullyConnected, Mockito.mock(ASTMCType.class), instances);
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isFullyConnected(ASTComponentType, String)}.
   *
   * @param typeName     the name of the component type
   * @param instanceName a fully connected subcomponent instance
   */
  @ParameterizedTest
  @MethodSource("compTypeAndFullyConnectedInstanceProvider")
  public void shouldEvaluateFullyConnected(@NotNull String typeName, @NotNull String instanceName) {
    doShouldEvaluateFullyConnected(typeName, instanceName, true);
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isFullyConnected(ASTComponentType, String)}.
   *
   * @param typeName     the name of the component type
   * @param instanceName a not fully connected subcomponent instance
   */
  @ParameterizedTest
  @MethodSource("compTypeAndNotFullyConnectedInstanceProvider")
  public void shouldEvaluateNotFullyConnected(@NotNull String typeName, @NotNull String instanceName) {
    doShouldEvaluateFullyConnected(typeName, instanceName, false);
  }

  protected void doShouldEvaluateFullyConnected(@NotNull String typeName, @NotNull String instanceName,
                                                boolean fullyConnected) {
    Preconditions.checkArgument(typeName != null);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));
    Preconditions.checkArgument(instanceName != null);

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(fullyConnected, ComfortableArcNodeHelper.isFullyConnected(compType, instanceName));
  }
}