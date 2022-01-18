/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.util;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import comfortablearc.AbstractTest;
import comfortablearc._ast.ASTArcACMode;
import comfortablearc._ast.ASTArcAutoConnect;
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
 * functions of concepts around auto connect.
 */
public class AutoConnectHelperTest extends AbstractTest {

  protected static Map<String, ASTComponentType> compTypes = new HashMap<>();

  protected static Map<String, ASTComponentType> getCompTypes() {
    return compTypes;
  }

  protected static void putCompType(@NotNull ASTComponentType compType) {
    Preconditions.checkNotNull(compType);
    getCompTypes().put(compType.getName(), compType);
  }

  protected static void putCompTypes(@NotNull ASTComponentType... componentTypes) {
    Preconditions.checkNotNull(componentTypes);
    Preconditions.checkArgument(!Arrays.asList(componentTypes).contains(null));
    Arrays.stream(componentTypes).forEach(AutoConnectHelperTest::putCompType);
  }

  @BeforeAll
  public static void setUpCompTypes() {
    ASTComponentType a = createCompType("A");
    ASTComponentType b = createCompType("B");
    b.getBody().addArcElement(createACOff());
    ASTComponentType c = createCompType("C");
    c.getBody().addArcElement(createACPort());
    ASTComponentType d = createCompType("D");
    d.getBody().addArcElement(createACType());
    ASTComponentType e = createCompType("E");
    e.getBody().addArcElement(createACOff());
    e.getBody().addArcElement(createACOff());
    ASTComponentType f = createCompType("F");
    f.getBody().addArcElement(createACPort());
    f.getBody().addArcElement(createACPort());
    ASTComponentType g = createCompType("G");
    g.getBody().addArcElement(createACType());
    g.getBody().addArcElement(createACType());
    ASTComponentType h = createCompType("H");
    h.getBody().addArcElement(createACOff());
    h.getBody().addArcElement(createACPort());
    h.getBody().addArcElement(createACType());
    putCompTypes(a, b, c, d, e, f, g, h);
  }

  protected static Stream<Arguments> compNameAndExpectedSizeProvider() {
    return Stream.of(Arguments.of("A", 0), Arguments.of("B", 1), Arguments.of("C", 1), Arguments.of("D", 1),
      Arguments.of("E", 2), Arguments.of("F", 2), Arguments.of("G", 2), Arguments.of("H", 3));
  }

  protected static Stream<Arguments> compNameAndUniqueACProvider() {
    return Stream.of(Arguments.of("A", false), Arguments.of("B", true), Arguments.of("C", true), Arguments.of("D",
      true),
      Arguments.of("E", false), Arguments.of("F", false), Arguments.of("G", false), Arguments.of("H", false));
  }

  protected static Stream<Arguments> compNameAndMaxOneACProvider() {
    return Stream.of(Arguments.of("A", true), Arguments.of("B", true), Arguments.of("C", true), Arguments.of("D", true),
      Arguments.of("E", false), Arguments.of("F", false), Arguments.of("G", false), Arguments.of("H", false));
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoConnects(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getACModes(ASTComponentType)}.
   *
   * @param typeName     the name of the component type
   * @param expectedSize the expected numbers of nodes (i.e. size of the collection)
   */
  @ParameterizedTest
  @MethodSource("compNameAndExpectedSizeProvider")
  public void shouldGetACs(@NotNull String typeName, int expectedSize) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(expectedSize >= 0);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When
    Collection<ASTArcAutoConnect> autoConnects = ComfortableArcNodeHelper.getAutoConnects(compType);
    Collection<ASTArcACMode> autoConnectModes = ComfortableArcNodeHelper.getACModes(compType);

    //Then
    Assertions.assertNotNull(autoConnects);
    Assertions.assertNotNull(autoConnectModes);
    Assertions.assertEquals(expectedSize, autoConnects.size());
    Assertions.assertEquals(expectedSize, autoConnectModes.size());
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoConnect(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getACMode(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has no auto connect
   */
  @ParameterizedTest
  @ValueSource(strings = {"A"})
  public void getACShouldThrowNoSuchElementException(@NotNull String typeName) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));
    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertThrows(NoSuchElementException.class, () -> ComfortableArcNodeHelper.getAutoConnect(compType));
    Assertions.assertThrows(NoSuchElementException.class, () -> ComfortableArcNodeHelper.getACMode(compType));
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoConnect(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getACMode(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has a single auto connect
   */
  @ParameterizedTest
  @ValueSource(strings = {"B", "C", "D"})
  public void shouldGetACWithoutError(@NotNull String typeName) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When
    ASTArcAutoConnect autoConnect = ComfortableArcNodeHelper.getAutoConnect(compType);
    ASTArcACMode autoConnectMode = ComfortableArcNodeHelper.getACMode(compType);

    //Then
    Assertions.assertNotNull(autoConnect);
    Assertions.assertNotNull(autoConnectMode);
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoConnect(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getACMode(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has multiple auto connects
   */
  @ParameterizedTest
  @ValueSource(strings = {"E", "F", "G", "H"})
  public void getACShouldThrowIllegalArgumentException(@NotNull String typeName) {
    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> ComfortableArcNodeHelper.getAutoConnect(compType));
    Assertions.assertThrows(IllegalArgumentException.class, () -> ComfortableArcNodeHelper.getACMode(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isUniqueACModePresent(ASTComponentType)}.
   *
   * @param typeName   the name of the component type
   * @param isACUnique whether the component type has a single auto connect
   */
  @ParameterizedTest
  @MethodSource("compNameAndUniqueACProvider")
  public void shouldEvaluateUniqueACCorrectly(@NotNull String typeName, boolean isACUnique) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(isACUnique, ComfortableArcNodeHelper.isUniqueACModePresent(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isMaxOneACModePresent(ASTComponentType)}.
   *
   * @param typeName   the name of the component type
   * @param isACMaxOne whether the component type has max one auto connect
   */
  @ParameterizedTest
  @MethodSource("compNameAndMaxOneACProvider")
  public void shouldEvaluateMaxOneACCorrectly(@NotNull String typeName, boolean isACMaxOne) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(isACMaxOne, ComfortableArcNodeHelper.isMaxOneACModePresent(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isACOff(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has auto connect off
   */
  @ParameterizedTest
  @ValueSource(strings = {"A", "B", "E", "H"})
  public void shouldEvaluateACOff(@NotNull String typeName) {
    doEvaluateAC(typeName, true, false, false);
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isACPortActive(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has auto connect port active
   */
  @ParameterizedTest
  @ValueSource(strings = {"C", "F"})
  public void shouldEvaluateACPortActive(@NotNull String typeName) {
    doEvaluateAC(typeName, false, true, false);
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isACTypeActive(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has auto connect type active
   */
  @ParameterizedTest
  @ValueSource(strings = {"D", "G"})
  public void shouldEvaluateACTypeActive(@NotNull String typeName) {
    doEvaluateAC(typeName, false, false, true);
  }

  protected void doEvaluateAC(@NotNull String typeName, boolean acOff, boolean acPortActive, boolean acTypeActive) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(acOff, ComfortableArcNodeHelper.isACOff(compType));
    Assertions.assertEquals(acPortActive, ComfortableArcNodeHelper.isACPortActive(compType));
    Assertions.assertEquals(acTypeActive, ComfortableArcNodeHelper.isACTypeActive(compType));
  }
}