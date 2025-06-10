/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;

/** Tests {@link RandomOracle} */
class RandomOracleTest {

  @ParameterizedTest
  @MethodSource("valuesAndRandomNumberResultProvider")
  void testDecideAmongList(
    OptionMock[] options, int randomNumResult, OptionMock expectedDecision) {

    // Given
    Random mockGenerator = Mockito.mock(Random.class);
    Mockito.when(mockGenerator.nextInt(anyInt())).thenReturn(randomNumResult);
    Oracle randomOracle = new RandomOracle(mockGenerator);

    List<OptionMock> optionsAsList = Arrays.asList(options);

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsList);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }

  protected static Stream<Arguments> valuesAndRandomNumberResultProvider() {
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    return Stream.of(
      Arguments.of(new OptionMock[] {obj1, obj2, obj3},  0, obj1),
      Arguments.of(new OptionMock[] {obj1, obj2, obj3},  1, obj2),
      Arguments.of(new OptionMock[] {obj1, obj2, obj3},  2, obj3)
    );
  }

  @ParameterizedTest
  @MethodSource("valuesAndRandomNumberResultProvider")
  @SuppressWarnings("unchecked")
  void testDecideAmongCollection(
    OptionMock[] options, int randomNumResult, OptionMock expectedDecision) {

    // Given
    Random mockGenerator = Mockito.mock(Random.class);
    Mockito.when(mockGenerator.nextInt(anyInt())).thenReturn(randomNumResult);
    Oracle randomOracle = new RandomOracle(mockGenerator);

    // We mock the collection so that we its conversion to a List reflect the array order.
    // To this end, toArray is used internally. Thus, we mock this method.
    Collection<OptionMock> optionsAsCollection = Mockito.mock(Collection.class);
    Mockito.when(optionsAsCollection.toArray()).thenReturn(options);

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsCollection);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }

  @ParameterizedTest
  @MethodSource("valuesAndRandomNumberResultProvider")
  @SuppressWarnings("unchecked")
  void testDecideAmongMap(
    OptionMock[] options, int randomNumResult, OptionMock expectedDecision) {

    // Given
    Random mockGenerator = Mockito.mock(Random.class);
    Mockito.when(mockGenerator.nextInt(anyInt())).thenReturn(randomNumResult);
    Oracle randomOracle = new RandomOracle(mockGenerator);

    // We mock the map so that the conversion of its keySet to a List reflects
    // the array order. Internally, the toArray() method is used, thus we mock it.
    Set<OptionMock> keySet = Mockito.mock(Set.class);
    Map<OptionMock, OptionMock> optionsAsMap = Mockito.mock(Map.class);
    Mockito.when(optionsAsMap.keySet()).thenReturn(keySet);
    Mockito.when(keySet.toArray()).thenReturn(options);
    Arrays.stream(options).forEach(o -> Mockito.when(optionsAsMap.get(o)).thenReturn(o));

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsMap);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }

  @ParameterizedTest
  @MethodSource("valuesAndRandomNumberResultProvider")
  void testDecideAmongLinkedSet(
    OptionMock[] options, int randomNumResult, OptionMock expectedDecision) {

    // Given
    Random mockGenerator = Mockito.mock(Random.class);
    Mockito.when(mockGenerator.nextInt(anyInt())).thenReturn(randomNumResult);
    Oracle randomOracle = new RandomOracle(mockGenerator);

    LinkedHashSet<OptionMock> optionsAsSet = new LinkedHashSet<>(options.length);
    Arrays.spliterator(options).forEachRemaining(optionsAsSet::add);

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsSet);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }

  @ParameterizedTest
  @MethodSource("valuesAndRandomNumberResultProvider")
  void testDecideAmongLinkedMap(
    OptionMock[] options, int randomNumResult, OptionMock expectedDecision) {

    // Given
    Random mockGenerator = Mockito.mock(Random.class);
    Mockito.when(mockGenerator.nextInt(anyInt())).thenReturn(randomNumResult);
    Oracle randomOracle = new RandomOracle(mockGenerator);

    LinkedHashMap<OptionMock, OptionMock> optionsAsMap
      = new LinkedHashMap<>(options.length);
    Arrays.spliterator(options).forEachRemaining(o -> optionsAsMap.put(o, o));

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsMap);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }

}
