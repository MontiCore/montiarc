/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import types.Direction;
import types.OnOff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The systems under test are the components {@code InitialActionSinglePort},
 * {@code InitialActionTwoPorts}, and {@code InitialActionMultiplePorts}. The
 * black-box tests ensure that the systems produce initial and subsequent
 * outputs as expected.
 */
public class InitialActionTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param cycles the number of computation cycles to run the simulation,
   * should match the number of expected output messages (minus one in case of
   * initial actions)
   * @param expected the expected output messages on port o
   */
  @ParameterizedTest
  @MethodSource("expOutputForInitialActionSinglePortProvider")
  @DisplayName("Component with single port should provide expected output")
  @Order(1)
  public void shouldProduceExpectedOutput(int cycles,
                                          @NotNull OnOff[] expected) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(cycles >= 0);
    Preconditions.checkArgument(expected.length > 0);
    Preconditions.checkArgument(expected.length == cycles + 1);

    //Given
    InitialActionSinglePort component = new InitialActionSinglePort();
    component.setUp();
    component.init();

    // When
    List<OnOff> actual = new ArrayList<>(cycles);

    // add the initial value
    actual.add(component.getPortO().getCurrentValue());
    for (int i = 0; i < cycles; i++) {
      component.compute();
      component.update();

      // add the current value after computation
      actual.add(component.getPortO().getCurrentValue());
    }

    // Then
    Assertions.assertThat(actual).containsExactly(expected);
  }

  /**
   * @return The number of computation cycles the simulation should run and an
   * array of the expected outputs.
   */
  protected static Stream<Arguments> expOutputForInitialActionSinglePortProvider() {
    return Stream.of(
        Arguments.of(0, new OnOff[]{ OnOff.OFF, }),
        Arguments.of(1, new OnOff[]{ OnOff.OFF, OnOff.ON }),
        Arguments.of(2, new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF }),
        Arguments.of(3, new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON })
    );
  }

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param cycles the number of computation cycles to run the simulation, the
   * number of expected output messages on each port should match the number of
   * computation cycles (plus one for initial actions)
   * @param o1 the expected output messages on port o1
   * @param o2 the expected output messages on port o2
   */
  @ParameterizedTest
  @MethodSource("expOutputForInitialActionTwoPortsProvider")
  @DisplayName("Component with two ports should provide expected output")
  @Order(2)
  public void shouldProduceExpectedOutput(int cycles,
                                          @NotNull OnOff[] o1,
                                          @NotNull OnOff[] o2) {
    Preconditions.checkNotNull(o1);
    Preconditions.checkNotNull(o2);
    Preconditions.checkArgument(cycles >= 0);
    Preconditions.checkArgument(o1.length > 0);
    Preconditions.checkArgument(o2.length > 0);
    Preconditions.checkArgument(o1.length == cycles + 1);
    Preconditions.checkArgument(o2.length == cycles + 1);

    //Given
    InitialActionTwoPorts component = new InitialActionTwoPorts();
    component.setUp();
    component.init();

    // When
    List<OnOff> a1 = new ArrayList<>(cycles);
    List<OnOff> a2 = new ArrayList<>(cycles);

    // add the initial values
    a1.add(component.getPortO1().getCurrentValue());
    a2.add(component.getPortO2().getCurrentValue());
    for (int i = 0; i < cycles; i++) {
      component.compute();
      component.update();

      // add the current values after computation
      a1.add(component.getPortO1().getCurrentValue());
      a2.add(component.getPortO2().getCurrentValue());
    }

    // Then
    Assertions.assertThat(a1).containsExactly(o1);
    Assertions.assertThat(a2).containsExactly(o2);
  }

  /**
   * @return The number of computation cycles the simulation should run and an
   * array of the expected outputs.
   */
  protected static Stream<Arguments> expOutputForInitialActionTwoPortsProvider() {
    return Stream.of(
        Arguments.of(0, new OnOff[]{ OnOff.OFF, },
                     new OnOff[]{ OnOff.OFF, }),
        Arguments.of(1, new OnOff[]{ OnOff.OFF, OnOff.ON },
                     new OnOff[]{ OnOff.OFF, OnOff.ON }),
        Arguments.of(2, new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF },
                     new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF }),
        Arguments.of(3, new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
                     new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON })
    );
  }

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param cycles the number of computation cycles to run the simulation, the
   * number of expected output messages on each port should match the number of
   * computation cycles (plus one for initial actions)
   * @param o1 the expected output messages on port o1
   * @param o2 the expected output messages on port o2
   * @param o3 the expected output messages on port o3
   */
  @ParameterizedTest
  @MethodSource("expOutputForInitialActionMultiplePortsProvider")
  @DisplayName("Component with multiple ports should provide expected output")
  @Order(3)
  public void shouldProduceExpectedOutput(int cycles,
                                          @NotNull OnOff[] o1,
                                          @NotNull Direction[] o2,
                                          @NotNull Direction[] o3) {
    Preconditions.checkNotNull(o1);
    Preconditions.checkNotNull(o2);
    Preconditions.checkNotNull(o3);
    Preconditions.checkArgument(cycles >= 0);
    Preconditions.checkArgument(o1.length > 0);
    Preconditions.checkArgument(o2.length > 0);
    Preconditions.checkArgument(o3.length > 0);
    Preconditions.checkArgument(o1.length == cycles + 1);
    Preconditions.checkArgument(o2.length == cycles + 1);
    Preconditions.checkArgument(o3.length == cycles + 1);

    //Given
    InitialActionMultiplePorts component = new InitialActionMultiplePorts();
    component.setUp();
    component.init();

    // When
    List<OnOff> a1 = new ArrayList<>(cycles);
    List<Direction> a2 = new ArrayList<>(cycles);
    List<Direction> a3 = new ArrayList<>(cycles);

    // add the initial values
    a1.add(component.getPortO1().getCurrentValue());
    a2.add(component.getPortO2().getCurrentValue());
    a3.add(component.getPortO3().getCurrentValue());
    for (int i = 0; i < cycles; i++) {
      component.compute();
      component.update();

      // add the current values after computation
      a1.add(component.getPortO1().getCurrentValue());
      a2.add(component.getPortO2().getCurrentValue());
      a3.add(component.getPortO3().getCurrentValue());
    }

    // Then
    Assertions.assertThat(a1).containsExactly(o1);
    Assertions.assertThat(a2).containsExactly(o2);
    Assertions.assertThat(a3).containsExactly(o3);
  }

  /**
   * @return The number of computation cycles the simulation should run and an
   * array of the expected outputs.
   */
  protected static Stream<Arguments> expOutputForInitialActionMultiplePortsProvider() {
    return Stream.of(
        Arguments.of(0, new OnOff[]{ OnOff.OFF, },
                     new Direction[]{ Direction.LEFT, },
                     new Direction[]{ Direction.BACKWARDS }),
        Arguments.of(1, new OnOff[]{ OnOff.OFF, OnOff.ON },
                     new Direction[]{
                         Direction.LEFT,
                         Direction.RIGHT },
                     new Direction[]{
                         Direction.BACKWARDS,
                         Direction.FORWARDS }),
        Arguments.of(2, new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF },
                     new Direction[]{
                         Direction.LEFT,
                         Direction.RIGHT,
                         Direction.LEFT },
                     new Direction[]{
                         Direction.BACKWARDS,
                         Direction.FORWARDS,
                         Direction.BACKWARDS }),
        Arguments.of(3, new OnOff[]{ OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON },
                     new Direction[]{
                         Direction.LEFT,
                         Direction.RIGHT,
                         Direction.LEFT,
                         Direction.RIGHT },
                     new Direction[]{
                         Direction.BACKWARDS,
                         Direction.FORWARDS,
                         Direction.BACKWARDS,
                         Direction.FORWARDS })
    );
  }
}