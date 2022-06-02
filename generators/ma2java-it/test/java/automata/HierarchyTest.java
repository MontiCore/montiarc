/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The system under test is the component {@code Hierarchy}. The black-box
 * tests ensure that the system produces the expected outputs.
 */
public class HierarchyTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param expectedCounter the expected counter messages in order
   * @param expectedPath    the expected path in order
   */
  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  @DisplayName("Component with hierarchy should produce expected outputs")
  public void shouldProduceExpectedOutput(@NotNull List<Integer> expectedCounter, @NotNull String expectedPath) {
    Preconditions.checkNotNull(expectedCounter);
    Preconditions.checkNotNull(expectedPath);

    //Given
    Hierarchy hierarchy = new Hierarchy();
    hierarchy.setUp();
    hierarchy.init();

    // When
    List<Integer> actualCounter = new ArrayList<>(expectedCounter.size());
    // no initial output
    actualCounter.add(hierarchy.getPortPCounter().getCurrentValue());
    for (int i = 0; i < 7; i++) {
      hierarchy.compute();
      hierarchy.update();

      // add the current value after computation
      actualCounter.add(hierarchy.getPortPCounter().getCurrentValue());
    }
    String actualPath = hierarchy.getPortPPath().getCurrentValue();

    // Then
    Assertions.assertIterableEquals(expectedCounter, actualCounter);
    Assertions.assertEquals(expectedPath, actualPath);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * automaton should produce for the given input.
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
        Arguments.of(List.of(1,4,7,9,13,15,19,24),
                "aIni" + "aEn" + "aEx" + "aToB" + "bEn" + "bEx" + "cEn" + "c1En" + "c1Ex" + "c2En" + "c2Ex" + "cEx" + "dEn" +
                "d2En" + "d2Ex" + "d1En" + "d1Ex" + "dEx" + "eEn" + "e1En" + "e1Ex" + "eEx" + "fEn" + "f1En" + "f11En"
        )
    );
  }
}
