/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/** Tests {@link PreferUnexploredOracle} */
class PreferUnexploredOracleTest {

  @Test
  void testDecideAmongCompletelyUnexploredList() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    List<OptionMock> optionsAsList = List.of(obj1, obj2, obj3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new PreferFirstOracle());

    // When
    OptionMock decision = oracle.decideAmong(optionsAsList);

    // Then should apply the default strategy (prefer first = obj1)
    assertThat(decision).isSameAs(obj1);
  }
  
  @Test
  void testDecideAmongCompletelyExploredList() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    List<OptionMock> optionsAsList = List.of(obj1, obj2, obj3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new PreferFirstOracle());
    oracle.decideAmong(List.of(obj1));
    oracle.decideAmong(List.of(obj2));
    oracle.decideAmong(List.of(obj3));

    // When
    OptionMock decision = oracle.decideAmong(optionsAsList);

    // Then should apply the default strategy (prefer first = obj1)
    assertThat(decision).isSameAs(obj1);
  }
  
  @Test
  void testDecideAmongPartiallyExploredList() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new PreferFirstOracle());
    oracle.decideAmong(List.of(obj1));

    // When
    OptionMock decisionOnObj1and3 = oracle.decideAmong(List.of(obj1, obj3));
    OptionMock decisionOnAll = oracle.decideAmong(List.of(obj1, obj2, obj3));

    // Then
    assertAll(
      () -> assertThat(decisionOnObj1and3).isSameAs(obj3),
      () -> assertThat(decisionOnAll).isSameAs(obj2)  
    );
  }

  @Test
  void testDecideAmongCompletelyUnexploredCollection() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new LowestHashValueOracle());

    // When
    OptionMock decision = oracle.decideAmong(Set.of(obj1, obj2, obj3));

    // Then should apply default strategy: select obj with the lowest hash = obj1
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  void testDecideAmongCompletelyExploredCollection() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new LowestHashValueOracle());
    oracle.decideAmong(Set.of(obj1));
    oracle.decideAmong(Set.of(obj2));
    oracle.decideAmong(Set.of(obj3));

    // When
    OptionMock decision = oracle.decideAmong(Set.of(obj1, obj2, obj3));

    // Then should apply default strategy: select obj with the lowest hash = obj1
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  void testDecideAmongPartiallyExploredCollection() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new LowestHashValueOracle());
    oracle.decideAmong(Set.of(obj1));

    // When
    OptionMock decisionOnObj1and3 = oracle.decideAmong(Set.of(obj1, obj3));
    OptionMock decisionOnAll = oracle.decideAmong(Set.of(obj1, obj2, obj3));

    // Then
    assertAll(
      () -> assertThat(decisionOnObj1and3).isSameAs(obj3),
      () -> assertThat(decisionOnAll).isSameAs(obj2)
    );
  }

  @Test
  void testDecideAmongCompletelyUnexploredMap() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new LowestHashValueOracle());

    // When
    OptionMock decision = oracle.decideAmong(Map.of(obj1, obj1, obj2, obj2, obj3, obj3));

    // Then should apply default strategy: select by lowest hash value = obj1
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  void testDecideAmongCompletelyExploredMap() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new LowestHashValueOracle());
    oracle.decideAmong(Map.of(obj1, obj1));
    oracle.decideAmong(Map.of(obj2, obj2));
    oracle.decideAmong(Map.of(obj3, obj3));

    // When
    OptionMock decision = oracle.decideAmong(Map.of(obj1, obj1, obj2, obj2, obj3, obj3));

    // Then should apply default strategy: select by lowest hash value = obj1
    assertThat(decision).isSameAs(obj1);
  }

  @Test
  void testDecideAmongPartiallyExploredMap() {
    // Given
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    PreferUnexploredOracle oracle = new PreferUnexploredOracle(new LowestHashValueOracle());
    oracle.decideAmong(Map.of(obj1, obj1));

    // When
    OptionMock decisionOnObj1and3 = oracle.decideAmong(Map.of(obj1, obj1, obj3, obj3));
    OptionMock decisionOnAll = oracle.decideAmong(Map.of(obj1, obj1, obj2, obj2, obj3, obj3));

    // Then
    assertAll(
      () -> assertThat(decisionOnObj1and3).isSameAs(obj3),
      () -> assertThat(decisionOnAll).isSameAs(obj2)
    );
  }
}
