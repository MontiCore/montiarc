/* (c) https://github.com/MontiCore/monticore */
package elevator;

import com.google.common.base.Preconditions;
import elevator.Commands.DoorCMD;
import elevator.Commands.LiftCMD;
import elevator.Controller.States;
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

public class ControllerTest {

  @Test
  @Order(1)
  public void testSetUp() {
    // Given
    Controller ctrl = new Controller();

    // When
    ctrl.setUp();

    // Then
    assertAll(
      () -> assertThat(ctrl.getAt1()).isNotNull(),
      () -> assertThat(ctrl.getAt2()).isNotNull(),
      () -> assertThat(ctrl.getAt3()).isNotNull(),
      () -> assertThat(ctrl.getAt4()).isNotNull(),
      () -> assertThat(ctrl.getReq1()).isNotNull(),
      () -> assertThat(ctrl.getReq2()).isNotNull(),
      () -> assertThat(ctrl.getReq3()).isNotNull(),
      () -> assertThat(ctrl.getReq4()).isNotNull(),
      () -> assertThat(ctrl.getIsClosed()).isNotNull(),
      () -> assertThat(ctrl.getDoor()).isNotNull(),
      () -> assertThat(ctrl.getLift()).isNotNull(),
      () -> assertThat(ctrl.getClear()).isNotNull()
    );
  }

  @Test
  @Order(2)
  public void testInit() {
    // Given
    Controller ctrl = new Controller();
    ctrl.setUp();

    // When
    ctrl.init();

    // Then
    assertAll(
      () -> assertThat(ctrl.getDoor().getValue()).isNull(),
      () -> assertThat(ctrl.getLift().getValue()).isNull(),
      () -> assertThat(ctrl.getClear().getValue()).isNotNull().isEqualTo(0)
    );
  }

  @Order(3)
  @ParameterizedTest
  @MethodSource("transitions")
  public void testCompute(@NotNull States sourceState,
                          @NotNull boolean req1, @NotNull boolean req2,
                          @NotNull boolean req3, @NotNull boolean req4,
                          @NotNull boolean at1, @NotNull boolean at2,
                          @NotNull boolean at3, @NotNull boolean at4,
                          @NotNull boolean isClosed,
                          @NotNull States targetState,
                          @Nullable DoorCMD door,
                          @Nullable LiftCMD lift,
                          @Nullable Integer clear) {
    Preconditions.checkNotNull(sourceState);
    Preconditions.checkNotNull(targetState);

    // Given
    Controller ctrl = new Controller();
    ctrl.setUp();
    ctrl.init();

    ctrl.currentState = sourceState;
    ctrl.getReq1().update(req1);
    ctrl.getReq2().update(req2);
    ctrl.getReq3().update(req3);
    ctrl.getReq4().update(req4);
    ctrl.getAt1().update(at1);
    ctrl.getAt2().update(at2);
    ctrl.getAt3().update(at3);
    ctrl.getAt4().update(at4);
    ctrl.getIsClosed().update(isClosed);

    // When
    ctrl.compute();
    ctrl.getClear().tick();

    // Then
    assertAll(
      () -> assertThat(ctrl.getCurrentState()).isEqualTo(targetState),
      () -> assertThat(ctrl.getDoor().getValue()).isEqualTo(door),
      () -> assertThat(ctrl.getLift().getValue()).isEqualTo(lift),
      () -> assertThat(ctrl.getClear().getValue()).isEqualTo(clear)
    );
  }

  public static Stream<Arguments> transitions() {
    return Stream.of(
      // 1
      Arguments.of(States.CloseDoor, false, false, false, false, false, false, false, false, false,
        States.CloseDoor, null, null, 0),
      // 2
      Arguments.of(States.CloseDoor, true, true, true, true, true, true, true, true, false,
        States.CloseDoor, null, null, 0),
      // 3
      Arguments.of(States.CloseDoor, false, false, false, false, false, false, false, false, true,
        States.DriveDown, null, LiftCMD.DOWN, 0),
      // 4
      Arguments.of(States.CloseDoor, true, true, true, true, false, true, true, true, true,
        States.DriveDown, null, LiftCMD.DOWN, 0),
      // 5
      Arguments.of(States.CloseDoor, false, false, false, false, true, false, false, false, true,
        States.OK, null, null, 0),
      // 6
      Arguments.of(States.DriveDown, false, false, false, false, false, false, false, false, false,
        States.DriveDown, null, null, 0),
      // 7
      Arguments.of(States.DriveDown, true, true, true, true, false, true, true, true, true,
        States.DriveDown, null, null, 0),
      // 8
      Arguments.of(States.DriveDown, false, false, false, false, true, false, false, false, false,
        States.OK, null, null, 0),
      // 9
      Arguments.of(States.OK, false, false, false, false, false, false, false, false, false,
        States.SearchFloor1, null, null, 0),
      // 10
      Arguments.of(States.OK, true, true, true, true, true, true, true, true, true,
        States.SearchFloor1, null, null, 0),
      // 11
      Arguments.of(States.Door1, false, false, false, false, false, false, false, false, false,
        States.Door1, null, null, null),
      // 12
      Arguments.of(States.Door1, true, false, false, false, true, false, false, false, false,
        States.Door1, DoorCMD.OPEN, null, 1),
      // 13
      Arguments.of(States.Door1, true, false, false, false, false, false, false, false, true,
        States.Door1, null, null, null),
      // 14
      Arguments.of(States.Door1, false, false, false, false, false, false, false, false, true,
        States.SearchFloor1, null, null, null),
      // 15
      Arguments.of(States.Door2, false, false, false, false, false, false, false, false, false,
        States.Door2, null, null, null),
      // 16
      Arguments.of(States.Door2, false, true, false, false, false, true, false, false, false,
        States.Door2, DoorCMD.OPEN, null, 2),
      // 17
      Arguments.of(States.Door2, false, true, false, false, false, false, false, false, true,
        States.Door2, null, null, null),
      // 18
      Arguments.of(States.Door2, false, false, false, false, false, false, false, false, true,
        States.SearchFloor2, null, null, null),
      // 19
      Arguments.of(States.Door3, false, false, false, false, false, false, false, false, false,
        States.Door3, null, null, null),
      // 20
      Arguments.of(States.Door3, false, false, true, false, false, false, true, false, false,
        States.Door3, DoorCMD.OPEN, null, 3),
      // 21
      Arguments.of(States.Door3, false, false, true, false, false, false, false, false, true,
        States.Door3, null, null, null),
      // 22
      Arguments.of(States.Door3, false, false, false, false, false, false, false, false, true,
        States.SearchFloor3, null, null, null),
      // 23
      Arguments.of(States.Door4, false, false, false, false, false, false, false, false, false,
        States.Door4, null, null, null),
      // 24
      Arguments.of(States.Door4, false, false, false, true, false, false, false, true, false,
        States.Door4, DoorCMD.OPEN, null, 4),
      // 25
      Arguments.of(States.Door4, false, false, false, true, false, false, false, false, true,
        States.Door4, null, null, null),
      // 26
      Arguments.of(States.Door4, false, false, false, false, false, false, false, false, true,
        States.SearchFloor4, null, null, null)

    );
  }

  @Test
  @Order(4)
  public void testTick() {
    // Given
    Controller ctrl = new Controller();
    ctrl.setUp();

    ctrl.getReq1().update(true);
    ctrl.getReq2().update(true);
    ctrl.getReq3().update(true);
    ctrl.getReq4().update(true);
    ctrl.getAt1().update(true);
    ctrl.getAt2().update(true);
    ctrl.getAt3().update(true);
    ctrl.getAt4().update(true);
    ctrl.getIsClosed().update(true);
    ctrl.getDoor().setValue(DoorCMD.OPEN);
    ctrl.getLift().setValue(LiftCMD.STOP);
    ctrl.getClear().setValue(0);

    // When
    ctrl.tick();

    // Then
    assertAll(
      () -> assertThat(ctrl.getReq1().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getReq2().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getReq3().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getReq4().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getAt1().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getAt2().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getAt3().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getAt4().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getIsClosed().getValue()).isNotNull().isTrue(),
      () -> assertThat(ctrl.getDoor().getValue()).isNull(),
      () -> assertThat(ctrl.getLift().getValue()).isNull(),
      () -> assertThat(ctrl.getClear().getValue()).isNotNull().isEqualTo(0)
    );
  }

}
