/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests {@link RandomOracle} */
class LowestHashValueOracleTest {

  @ParameterizedTest
  @MethodSource("valuesAndExpectedResultProvider")
  void testDecideAmongList(OptionMock[] options, OptionMock expectedDecision) {
    // Given
    Oracle randomOracle = new LowestHashValueOracle();
    List<OptionMock> optionsAsList = Arrays.asList(options);

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsList);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }

  protected static Stream<Arguments> valuesAndExpectedResultProvider() {
    OptionMock obj1 = new OptionMock(1);
    OptionMock obj2 = new OptionMock(2);
    OptionMock obj3 = new OptionMock(3);

    return Stream.of(
      Arguments.of(new OptionMock[] {obj1, obj2, obj3}, obj1),
      Arguments.of(new OptionMock[] {obj2, obj1, obj3}, obj1),
      Arguments.of(new OptionMock[] {obj2, obj3, obj1}, obj1),
      Arguments.of(new OptionMock[] {obj3, obj2, obj1}, obj1)
    );
  }

  @ParameterizedTest
  @MethodSource("valuesAndExpectedResultProvider")
  void testDecideAmongSet(OptionMock[] options, OptionMock expectedDecision) {
    // Given
    Oracle randomOracle = new LowestHashValueOracle();
    Collection<OptionMock> optionsAsCollection = new HashSet<>(Arrays.asList(options));

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsCollection);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }

  @ParameterizedTest
  @MethodSource("valuesAndExpectedResultProvider")
  void testDecideAmongMap(OptionMock[] options, OptionMock expectedDecision) {
    // Given
    Oracle randomOracle = new LowestHashValueOracle();
    Map<OptionMock, OptionMock> optionsAsMap =
      Arrays.stream(options).collect(Collectors.toMap(Function.identity(), Function.identity()));

    // When
    OptionMock decision = randomOracle.decideAmong(optionsAsMap);

    // Then
    assertThat(decision).isSameAs(expectedDecision);
  }
}
