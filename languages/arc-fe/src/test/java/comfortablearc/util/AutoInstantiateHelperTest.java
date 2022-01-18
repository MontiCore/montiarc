/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.util;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import comfortablearc.AbstractTest;
import comfortablearc._ast.ASTArcAIMode;
import comfortablearc._ast.ASTArcAutoInstantiate;
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
 * functions of concepts around auto instantiate.
 */
public class AutoInstantiateHelperTest extends AbstractTest {

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
    Arrays.stream(componentTypes).forEach(AutoInstantiateHelperTest::putCompType);
  }

  @BeforeAll
  public static void setUpCompTypes() {
    ASTComponentType a = createCompType("A");
    ASTComponentType b = createCompType("B");
    b.getBody().addArcElement(createAIOff());
    ASTComponentType c = createCompType("C");
    c.getBody().addArcElement(createAIOn());
    ASTComponentType d = createCompType("D");
    d.getBody().addArcElement(createAIOff());
    d.getBody().addArcElement(createAIOn());
    ASTComponentType e = createCompType("E");
    e.getBody().addArcElement(createAIOn());
    e.getBody().addArcElement(createAIOff());
    putCompTypes(a, b, c, d, e);
  }

  protected static Stream<Arguments> compNameAndExpectedSizeProvider() {
    return Stream.of(Arguments.of("A", 0), Arguments.of("B", 1), Arguments.of("C", 1), Arguments.of("D", 2),
      Arguments.of("E", 2));
  }

  protected static Stream<Arguments> compNameAndUniqueAIProvider() {
    return Stream.of(Arguments.of("A", false), Arguments.of("B", true), Arguments.of("C", true),
      Arguments.of("D", false), Arguments.of("E", false));
  }

  protected static Stream<Arguments> compNameAndMaxOneAIProvider() {
    return Stream.of(Arguments.of("A", true), Arguments.of("B", true), Arguments.of("C", true),
      Arguments.of("D", false), Arguments.of("E", false));
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoInstantiates(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getAIModes(ASTComponentType)}.
   *
   * @param typeName     the name of the component type
   * @param expectedSize the expected numbers of nodes (i.e. size of the collection)
   */
  @ParameterizedTest
  @MethodSource("compNameAndExpectedSizeProvider")
  public void shouldGetAIs(@NotNull String typeName, int expectedSize) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(expectedSize >= 0);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When
    Collection<ASTArcAutoInstantiate> autoInstantiates = ComfortableArcNodeHelper.getAutoInstantiates(compType);
    Collection<ASTArcAIMode> autoInstantiateModes = ComfortableArcNodeHelper.getAIModes(compType);

    //Then
    Assertions.assertNotNull(autoInstantiates);
    Assertions.assertNotNull(autoInstantiateModes);
    Assertions.assertEquals(expectedSize, autoInstantiates.size());
    Assertions.assertEquals(expectedSize, autoInstantiateModes.size());
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoInstantiate(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getAIMode(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has no auto instantiate
   */
  @ParameterizedTest
  @ValueSource(strings = {"A"})
  public void getAIShouldThrowNoSuchElementException(@NotNull String typeName) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));
    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertThrows(NoSuchElementException.class, () -> ComfortableArcNodeHelper.getAutoInstantiate(compType));
    Assertions.assertThrows(NoSuchElementException.class, () -> ComfortableArcNodeHelper.getAIMode(compType));
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoInstantiate(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getAIMode(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has a single auto instantiate
   */
  @ParameterizedTest
  @ValueSource(strings = {"B", "C"})
  public void shouldGetAIWithoutError(@NotNull String typeName) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When
    ASTArcAutoInstantiate autoInstantiate = ComfortableArcNodeHelper.getAutoInstantiate(compType);
    ASTArcAIMode autoInstantiateMode = ComfortableArcNodeHelper.getAIMode(compType);

    //Then
    Assertions.assertNotNull(autoInstantiate);
    Assertions.assertNotNull(autoInstantiateMode);
  }

  /**
   * Methods under test {@link ComfortableArcNodeHelper#getAutoInstantiate(ASTComponentType)}
   * and {@link ComfortableArcNodeHelper#getAIMode(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has multiple auto instantiates
   */
  @ParameterizedTest
  @ValueSource(strings = {"D", "E"})
  public void getAIShouldThrowIllegalArgumentException(@NotNull String typeName) {
    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> ComfortableArcNodeHelper.getAutoInstantiate(compType));
    Assertions.assertThrows(IllegalArgumentException.class, () -> ComfortableArcNodeHelper.getAIMode(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isUniqueAIModePresent(ASTComponentType)}.
   *
   * @param typeName   the name of the component type
   * @param isAIUnique whether the component type has a single auto instantiate
   */
  @ParameterizedTest
  @MethodSource("compNameAndUniqueAIProvider")
  public void shouldEvaluateUniqueAICorrectly(@NotNull String typeName, boolean isAIUnique) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(isAIUnique, ComfortableArcNodeHelper.isUniqueAIModePresent(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isMaxOneAIModePresent(ASTComponentType)}.
   *
   * @param typeName   the name of the component type
   * @param isAIMaxOne whether the component type has max one auto instantiate
   */
  @ParameterizedTest
  @MethodSource("compNameAndMaxOneAIProvider")
  public void shouldEvaluateMaxOneAICorrectly(@NotNull String typeName, boolean isAIMaxOne) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(isAIMaxOne, ComfortableArcNodeHelper.isMaxOneAIModePresent(compType));
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isAIOff(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has auto instantiate off
   */
  @ParameterizedTest
  @ValueSource(strings = {"A", "B", "D"})
  public void shouldEvaluateAIOff(@NotNull String typeName) {
    doEvaluateAI(typeName, false);
  }

  /**
   * Method under test {@link ComfortableArcNodeHelper#isAIOn(ASTComponentType)}.
   *
   * @param typeName the name of the component type that has auto instantiate on
   */
  @ParameterizedTest
  @ValueSource(strings = {"C", "E"})
  public void shouldEvaluateAIOn(@NotNull String typeName) {
    doEvaluateAI(typeName, true);
  }

  protected void doEvaluateAI(@NotNull String typeName, boolean aiOn) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkArgument(getCompTypes().containsKey(typeName));

    //Given
    ASTComponentType compType = getCompTypes().get(typeName);

    //When && Then
    Assertions.assertEquals(!aiOn, ComfortableArcNodeHelper.isAIOff(compType));
    Assertions.assertEquals(aiOn, ComfortableArcNodeHelper.isAIOn(compType));
  }
}