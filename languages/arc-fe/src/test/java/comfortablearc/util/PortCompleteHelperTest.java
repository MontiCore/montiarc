/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.util;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import comfortablearc.AbstractTest;
import comfortablearc._ast.ASTPortComplete;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.stream.Stream;

/**
 * Holds tests for methods of {@link ComfortableArcNodeHelper} targeting utility
 * functions of concepts around port completeness.
 */
public class PortCompleteHelperTest extends AbstractTest {

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
    Arrays.stream(componentTypes).forEach(PortCompleteHelperTest::putCompType);
  }

  @BeforeAll
  public static void setUpCompTypes() {
    ASTComponentType a = createCompType("A");
    ASTComponentType b = createCompType("B");
    b.getBody().addArcElements(createPC());
    ASTComponentType c = createCompType("C");
    c.getBody().addArcElements(createPC());
    c.getBody().addArcElements(createPC());
    putCompTypes(a, b, c);
  }

  protected static Stream<Arguments> compNameAndExpectedSizeProvider() {
    return Stream.of(Arguments.of("A", 0), Arguments.of("B", 1), Arguments.of("C", 2));
  }

  protected static Stream<Arguments> compNameAndMaxOnePCProvider() {
    return Stream.of(Arguments.of("A", true), Arguments.of("B", true), Arguments.of("C", false));
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getPortCompletes(ASTComponentType)}.
   *
   * @param typeName     the name of the component type
   * @param expectedSize the expected numbers of nodes (i.e. size of the collection)
   */
  @ParameterizedTest
  @MethodSource("compNameAndExpectedSizeProvider")
  public void shouldGetACs(@NotNull String typeName, int expectedSize) {
    Preconditions.checkArgument(typeName != null);
    Preconditions.checkArgument(expectedSize >= 0);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When
    Collection<ASTPortComplete> portCompletes = ComfortableArcNodeHelper.getPortCompletes(compType);

    //Then
    Assertions.assertNotNull(portCompletes);
    Assertions.assertEquals(expectedSize, portCompletes.size());
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getPortComplete(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has no port complete
   */
  @ParameterizedTest
  @ValueSource(strings = {"A"})
  public void getPCShouldThrowNoSuchElementException(@NotNull String typeName) {
    Preconditions.checkArgument(typeName != null);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));
    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertThrows(NoSuchElementException.class, () -> ComfortableArcNodeHelper.getPortComplete(compType));
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getPortComplete(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has a single port complete
   */
  @ParameterizedTest
  @ValueSource(strings = {"B"})
  public void shouldGetPCWithoutError(@NotNull String typeName) {
    Preconditions.checkArgument(typeName != null);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When
    ASTPortComplete portComplete = ComfortableArcNodeHelper.getPortComplete(compType);

    //Then
    Assertions.assertNotNull(portComplete);
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getPortComplete(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has multiple port completes
   */
  @ParameterizedTest
  @ValueSource(strings = {"C"})
  public void getPortCompleteShouldThrowIllegalArgumentException(@NotNull String typeName) {
    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> ComfortableArcNodeHelper.getPortComplete(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isMaxOnePortCompletePresent(ASTComponentType)}.
   *
   * @param typeName   the name of the component type
   * @param isPCMaxOne whether the component type has max one port complete
   */
  @ParameterizedTest
  @MethodSource("compNameAndMaxOnePCProvider")
  public void shouldEvaluateMaxOnePCCorrectly(@NotNull String typeName, boolean isPCMaxOne) {
    Preconditions.checkArgument(typeName != null);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(isPCMaxOne, ComfortableArcNodeHelper.isMaxOnePortCompletePresent(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isPortCompleteActive(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has port complete off
   */
  @ParameterizedTest
  @ValueSource(strings = {"A"})
  public void shouldEvaluatePCOff(@NotNull String typeName) {
    doEvaluatePC(typeName, false);
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isPortCompleteActive(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has port complete active
   */
  @ParameterizedTest
  @ValueSource(strings = {"B", "C"})
  public void shouldEvaluatePCActive(@NotNull String typeName) {
    doEvaluatePC(typeName, true);
  }

  protected void doEvaluatePC(@NotNull String typeName, boolean pcActive) {
    Preconditions.checkArgument(typeName != null);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(pcActive, ComfortableArcNodeHelper.isPortCompleteActive(compType));
  }
}