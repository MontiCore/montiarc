/* (c) https://github.com/MontiCore/monticore */
package elevator;

import com.google.common.base.Preconditions;
import elevator.FloorControl.States;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class FloorControlTest {


  @Test
  @Order(1)
  public void testSetUp() {
    // Given
    FloorControl floor = new FloorControl();

    // When
    floor.setUp();

    // Then
    assertAll(
      () -> assertThat(floor.getBtn()).isNotNull(),
      () -> assertThat(floor.getLight()).isNotNull(),
      () -> assertThat(floor.getClear()).isNotNull(),
      () -> assertThat(floor.getReq()).isNotNull()
    );
  }

  @Test
  @Order(2)
  public void testInit() {
    // Given
    FloorControl floor = new FloorControl();
    floor.setUp();

    // When
    floor.init();

    // Then
    assertAll(
      () -> assertThat(floor.getBtn().getValue()).isNull(),
      () -> assertThat(floor.getLight().getValue()).isNull()
    );
  }

  @Order(3)
  @ParameterizedTest
  @MethodSource("transitions")
  public void testCompute(@NotNull States sourceState,
                          @NotNull boolean btn,
                          @NotNull boolean clear,
                          @NotNull States targetState,
                          @NotNull boolean light,
                          @NotNull boolean req) {
    Preconditions.checkNotNull(sourceState);
    Preconditions.checkNotNull(targetState);

    // Given
    FloorControl floor = new FloorControl();
    floor.setUp();
    floor.init();

    floor.currentState = sourceState;
    floor.getBtn().update(btn);
    floor.getClear().update(clear);

    // When
    floor.compute();

    // Then
    assertAll(
      () -> assertThat(floor.getCurrentState()).isEqualTo(targetState),
      () -> assertThat(floor.getLight().getValue()).isEqualTo(light),
      () -> assertThat(floor.getReq().getValue()).isEqualTo(req)
    );
  }

  public static Stream<Arguments> transitions() {
    return Stream.of(
      Arguments.of(States.LightOff, false, false, States.LightOff, false, false),
      Arguments.of(States.LightOff, true, false, States.LightOn, true, true),
      Arguments.of(States.LightOff, false, true, States.LightOff, false, false),
      Arguments.of(States.LightOff, true, true, States.LightOff, false, false),
      Arguments.of(States.LightOn, false, false, States.LightOn, true, true),
      Arguments.of(States.LightOn, true, false, States.LightOn, true, true),
      Arguments.of(States.LightOn, false, true, States.LightOff, false, false),
      Arguments.of(States.LightOn, true, true, States.LightOff, false, false)
    );
  }

  @Test
  @Order(4)
  public void testTick() {
    // Given
    FloorControl floor = new FloorControl();
    floor.setUp();

    floor.getBtn().update(true);
    floor.getClear().update(true);
    floor.getLight().setValue(true);
    floor.getReq().setValue(true);

    // When
    floor.tick();

    // Then
    assertAll(
      () -> assertThat(floor.getBtn().getValue()).isNotNull().isEqualTo(true),
      () -> assertThat(floor.getClear().getValue()).isNotNull().isEqualTo(true),
      () -> assertThat(floor.getLight().getValue()).isNull(),
      () -> assertThat(floor.getReq().getValue()).isNull()
    );
  }

  @Order(5)
  @ParameterizedTest
  @MethodSource("runs")
  public void testCompute(@NotNull boolean[] btn,
                          @NotNull boolean[] clear,
                          @NotNull States[] expStates,
                          @NotNull boolean[] expLight,
                          @NotNull boolean[] expReq) {
    Preconditions.checkNotNull(expStates);
    Preconditions.checkArgument(btn.length > 0);
    Preconditions.checkArgument(clear.length > 0);
    Preconditions.checkArgument(expStates.length == btn.length + 1);
    Preconditions.checkArgument(clear.length == btn.length);
    Preconditions.checkArgument(expLight.length == btn.length);
    Preconditions.checkArgument(expReq.length == btn.length);

    // Given
    FloorControl floor = new FloorControl();
    floor.setUp();

    States[] actStates = new States[expStates.length];
    boolean[] actLight = new boolean[expLight.length];
    boolean[] actReq = new boolean[expReq.length];

    // When
    floor.init();
    actStates[0] = floor.getCurrentState();

    for (int i = 0; i < btn.length; i++) {
      floor.getBtn().update(btn[i]);
      floor.getClear().update(clear[i]);

      floor.compute();

      actLight[i] = floor.getLight().getValue();
      actReq[i] = floor.getReq().getValue();
      actStates[i + 1] = floor.getCurrentState();

      floor.tick();
    }

    // Then
    assertAll(
      () -> assertThat(actStates).containsExactly(expStates),
      () -> assertThat(actLight).containsExactly(expLight),
      () -> assertThat(actReq).containsExactly(expReq)
    );
  }

  public static Stream<Arguments> runs() {
    return Stream.of(
      // 1 - No request
      Arguments.of(
        new boolean[]{false, false},
        new boolean[]{false, false},
        new States[]{States.LightOff, States.LightOff, States.LightOff},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 2 - Request
      Arguments.of(
        new boolean[]{true, true},
        new boolean[]{false, false},
        new States[]{States.LightOff, States.LightOn, States.LightOn},
        new boolean[]{true, true},
        new boolean[]{true, true}
      ),
      // 2 - Request, then clear
      Arguments.of(
        new boolean[]{true, true},
        new boolean[]{false, true},
        new States[]{States.LightOff, States.LightOn, States.LightOff},
        new boolean[]{true, false},
        new boolean[]{true, false}
      )
    );
  }

}
