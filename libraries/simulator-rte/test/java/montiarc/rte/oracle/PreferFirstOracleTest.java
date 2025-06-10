/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests {@link PreferFirstOracle} */
class PreferFirstOracleTest {

  @Test
  void testDecideAmongList() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    List<OptionMock> optionsAsList = List.of(obj1, obj2, obj3);

    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    OptionMock decision = oracle.decideAmong(optionsAsList);

    // Then
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testDecideAmongCollection() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    OptionMock[] optionsArray = new OptionMock[] {obj1, obj2, obj3};

    // We mock the collection so that we its conversion to a List reflect the array order.
    // To this end, toArray is used internally. Thus, we mock this method.
    Collection<OptionMock> optionsAsCollection = Mockito.mock(Collection.class);
    Mockito.when(optionsAsCollection.toArray()).thenReturn(optionsArray);

    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    OptionMock decision = oracle.decideAmong(optionsAsCollection);

    // Then
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testDecideAmongMap() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    OptionMock[] optionsArray = new OptionMock[] {obj1, obj2, obj3};

    // We mock the map so that the conversion of its keySet to a List reflects
    // the array order. Internally, the toArray() method is used, thus we mock it.
    Set<OptionMock> keySet = Mockito.mock(Set.class);
    Map<OptionMock, OptionMock> optionsAsMap = Mockito.mock(Map.class);
    Mockito.when(optionsAsMap.keySet()).thenReturn(keySet);
    Mockito.when(keySet.toArray()).thenReturn(optionsArray);
    Arrays.stream(optionsArray).forEach(o -> Mockito.when(optionsAsMap.get(o)).thenReturn(o));

    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    OptionMock decision = oracle.decideAmong(optionsAsMap);

    // Then
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  void testDecideAmongLinkedSet() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    Set<OptionMock> optionsAsSet = new LinkedHashSet<>();
    optionsAsSet.add(obj1);
    optionsAsSet.add(obj2);
    optionsAsSet.add(obj3);

    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    OptionMock decision = oracle.decideAmong(optionsAsSet);

    // Then
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  void testDecideAmongLinkedMap() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    LinkedHashMap<OptionMock, OptionMock> optionsAsMap = new LinkedHashMap<>();
    optionsAsMap.put(obj1, obj1);
    optionsAsMap.put(obj2, obj2);
    optionsAsMap.put(obj3, obj3);

    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    OptionMock decision = oracle.decideAmong(optionsAsMap);

    // Then
    assertThat(decision).isSameAs(obj1);
  }
}
