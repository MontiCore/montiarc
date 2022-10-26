/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * The system under test is the component {@code Hierarchy}. The black-box tests
 * ensure that the system produces the expected outputs.
 */
public class HierarchyTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param expected the expected output
   */
  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  @DisplayName("Component with hierarchy should produce expected outputs")
  public void shouldProduceExpectedOutput(int cycles,
                                          @NotNull String expected) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(cycles >= 0);

    // Given
    Hierarchy hierarchy = new Hierarchy();
    hierarchy.setUp();
    hierarchy.init();

    String actualPath = hierarchy.getOPath().getValue();

    // When
    for (int i = 0; i < cycles; i++) {
      // compute
      hierarchy.compute();

      // get output
      actualPath = hierarchy.getOPath().getValue();

      // tick
      hierarchy.tick();
    }

    // Then
    Assertions.assertEquals(expected, actualPath);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * automaton should produce for the given input.
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    String path0 = "aIni->aEn->a1Ini->a1En";
    String path1 = path0 + "->a1Ex" + "->aEx" + "->aToB" + "->bEn";
    String path2 = path1 + "->bEx" + "->bToC" + "->cEn" + "->c1En";
    String path3 = path2 + "->c1Ex" + "->c1ToC2" + "->c2En";
    String path4 = path3 + "->c2Ex" + "->cEx" + "->cToD" + "->dEn" + "->d2En";
    String path5 = path4 + "->d2Ex" + "->d2ToD1" + "->d1En";
    String path6 = path5 + "->d1Ex" + "->dEx" + "->d1ToE" + "->eEn" + "->e1Ini" + "->e1En";
    String path7 = path6 + "->e1Ex" + "->e1ToE2" + "->e2En";
    String path8 = path7 + "->e2Ex" + "->eEx" + "->eToF11" + "->fEn" + "->f1En" + "->f11En";
    String path9 = path8 + "->f11Ex" + "->f1Ex" + "->fEx" + "->fToA" + "->aEn" + "->a1Ini" + "->a1En";
    return Stream.of(Arguments.of(0, path0),
      Arguments.of(1, path1),
      Arguments.of(2, path2),
      Arguments.of(3, path3),
      Arguments.of(4, path4),
      Arguments.of(5, path5),
      Arguments.of(6, path6),
      Arguments.of(7, path7),
      Arguments.of(8, path8),
      Arguments.of(9, path9)
    );
  }
}
