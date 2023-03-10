/* (c) https://github.com/MontiCore/monticore */
package elevator;

import com.google.common.base.Preconditions;
import elevator.Commands.LiftCMD;
import elevator.Lift.States;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LiftTest {

  @Test
  @Order(1)
  public void testSetUp() {
    // Given
    Lift lift = new Lift();

    // When
    lift.setUp();

    // Then
    assertAll(
      () -> assertThat(lift.getCmd()).isNotNull(),
      () -> assertThat(lift.getUp()).isNotNull(),
      () -> assertThat(lift.getDown()).isNotNull()
    );
  }

  @Test
  @Order(2)
  public void testInit() {
    // Given
    Lift lift = new Lift();
    lift.setUp();

    // When
    lift.init();

    // Then
    assertAll(
      () -> assertThat(lift.getUp().getValue()).isNull(),
      () -> assertThat(lift.getDown().getValue()).isNull()
    );
  }

  @Order(3)
  @ParameterizedTest
  @MethodSource("transitions")
  public void testCompute(@NotNull States sourceState,
                          @Nullable LiftCMD cmd,
                          @NotNull States targetState,
                          @NotNull boolean up,
                          @NotNull boolean down) {
    Preconditions.checkNotNull(sourceState);
    Preconditions.checkNotNull(targetState);

    // Given
    Lift lift = new Lift();
    lift.setUp();
    lift.init();

    lift.currentState = sourceState;
    lift.getCmd().update(cmd);

    // When
    lift.compute();

    // Then
    assertAll(
      () -> assertThat(lift.getCurrentState()).isEqualTo(targetState),
      () -> assertThat(lift.getUp().getValue()).isEqualTo(up),
      () -> assertThat(lift.getDown().getValue()).isEqualTo(down)
    );
  }

  public static Stream<Arguments> transitions() {
    return Stream.of(
      Arguments.of(States.Wait, null, States.Wait, false, false),
      Arguments.of(States.Wait, LiftCMD.STOP, States.Wait, false, false),
      Arguments.of(States.Wait, LiftCMD.UP, States.Up, true, false),
      Arguments.of(States.Wait, LiftCMD.DOWN, States.Down, false, true),
      Arguments.of(States.Up, null, States.Up, true, false),
      Arguments.of(States.Up, LiftCMD.STOP, States.Wait, false, false),
      Arguments.of(States.Up, LiftCMD.UP, States.Up, true, false),
      Arguments.of(States.Up, LiftCMD.DOWN, States.Down, false, true),
      Arguments.of(States.Down, null, States.Down, false, true),
      Arguments.of(States.Down, LiftCMD.STOP, States.Wait, false, false),
      Arguments.of(States.Down, LiftCMD.UP, States.Up, true, false),
      Arguments.of(States.Down, LiftCMD.DOWN, States.Down, false, true)
    );
  }

  @Test
  @Order(4)
  public void testTick() {
    // Given
    Lift lift = new Lift();
    lift.setUp();

    LiftCMD cmd = LiftCMD.STOP;
    lift.getCmd().update(cmd);
    lift.getUp().setValue(true);
    lift.getDown().setValue(true);

    // When
    lift.tick();

    // Then
    assertAll(
      () -> assertThat(lift.getCmd().getValue()).isNotNull().isEqualTo(cmd),
      () -> assertThat(lift.getUp().getValue()).isNull(),
      () -> assertThat(lift.getDown().getValue()).isNull()
    );
  }

  @Order(5)
  @ParameterizedTest
  @MethodSource("runs")
  public void testCompute(@NotNull LiftCMD[] cmd,
                          @NotNull States[] expStates,
                          @NotNull boolean[] expUp,
                          @NotNull boolean[] expDown) {
    Preconditions.checkNotNull(expStates);
    Preconditions.checkNotNull(cmd);
    Preconditions.checkNotNull(expUp);
    Preconditions.checkNotNull(expDown);
    Preconditions.checkArgument(cmd.length > 0);
    Preconditions.checkArgument(expStates.length == cmd.length + 1);
    Preconditions.checkArgument(expUp.length == cmd.length);
    Preconditions.checkArgument(expDown.length == cmd.length);

    // Given
    Lift lift = new Lift();
    lift.setUp();

    States[] actStates = new States[expStates.length];
    boolean[] actUp = new boolean[expUp.length];
    boolean[] actDown = new boolean[expDown.length];

    // When
    lift.init();
    actStates[0] = lift.getCurrentState();

    for (int i = 0; i < cmd.length; i++) {
      lift.getCmd().update(cmd[i]);

      lift.compute();

      actUp[i] = lift.getUp().getValue();
      actDown[i] = lift.getDown().getValue();
      actStates[i + 1] = lift.getCurrentState();

      lift.tick();
    }

    // Then
    assertAll(
      () -> assertThat(actStates).containsExactly(expStates),
      () -> assertThat(actUp).containsExactly(expUp),
      () -> assertThat(actDown).containsExactly(expDown)
    );
  }

  public static Stream<Arguments> runs() {
    return Stream.of(
      // 1
      Arguments.of(
        new LiftCMD[]{LiftCMD.UP, LiftCMD.STOP},
        new States[]{States.Wait, States.Up, States.Wait},
        new boolean[]{true, false},
        new boolean[]{false, false}
      ),
      // 2
      Arguments.of(
        new LiftCMD[]{LiftCMD.DOWN, LiftCMD.STOP},
        new States[]{States.Wait, States.Down, States.Wait},
        new boolean[]{false, false},
        new boolean[]{true, false}
      ),
      // 3
      Arguments.of(
        new LiftCMD[]{LiftCMD.STOP, LiftCMD.STOP},
        new States[]{States.Wait, States.Wait, States.Wait},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 4
      Arguments.of(
        new LiftCMD[]{LiftCMD.UP, null, LiftCMD.STOP},
        new States[]{States.Wait, States.Up, States.Up, States.Wait},
        new boolean[]{true, true, false},
        new boolean[]{false, false, false}
      ),
      // 5
      Arguments.of(
        new LiftCMD[]{LiftCMD.DOWN, null, LiftCMD.STOP},
        new States[]{States.Wait, States.Down, States.Down, States.Wait},
        new boolean[]{false, false, false},
        new boolean[]{true, true, false}
      )
    );
  }

}
