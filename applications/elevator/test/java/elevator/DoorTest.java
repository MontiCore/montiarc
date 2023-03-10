/* (c) https://github.com/MontiCore/monticore */
package elevator;

import com.google.common.base.Preconditions;
import elevator.Commands.DoorCMD;
import elevator.Door.States;
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

public class DoorTest {

  @Test
  public void testSetUp() {
    // Given
    Door door = new Door();

    // When
    door.setUp();

    // Then
    assertAll(
      () -> assertThat(door.getCmd()).isNotNull(),
      () -> assertThat(door.getIsClosed()).isNotNull(),
      () -> assertThat(door.getIsOpen()).isNotNull(),
      () -> assertThat(door.getIsObstacle()).isNotNull(),
      () -> assertThat(door.getOpen()).isNotNull(),
      () -> assertThat(door.getClose()).isNotNull(),
      () -> assertThat(door.getClosed()).isNotNull()
    );
  }

  @Test
  public void testInit() {
    // Given
    Door door = new Door();
    door.setUp();

    // When
    door.init();

    // Then
    assertAll(
      () -> assertThat(door.getOpen().getValue()).isNull(),
      () -> assertThat(door.getClose().getValue()).isNull(),
      () -> assertThat(door.getClosed().getValue()).isNotNull().isFalse()
    );
  }

  @Order(3)
  @ParameterizedTest
  @MethodSource("transitions")
  public void testCompute(@NotNull States sourceState,
                          @Nullable DoorCMD cmd,
                          @NotNull boolean isOpen,
                          @NotNull boolean isClosed,
                          @NotNull boolean isObstacle,
                          @NotNull States targetState,
                          @NotNull boolean open,
                          @NotNull boolean close,
                          @NotNull boolean closed) {
    Preconditions.checkNotNull(sourceState);
    Preconditions.checkNotNull(targetState);

    // Given
    Door door = new Door();
    door.setUp();
    door.init();

    door.currentState = sourceState;
    door.getCmd().update(cmd);
    door.getIsOpen().update(isOpen);
    door.getIsClosed().update(isClosed);
    door.getIsObstacle().update(isObstacle);

    // When
    door.compute();
    door.getClosed().tick();

    // Then
    assertAll(
      () -> assertThat(door.getOpen().getValue()).isEqualTo(open),
      () -> assertThat(door.getClose().getValue()).isEqualTo(close),
      () -> assertThat(door.getClosed().getValue()).isEqualTo(closed)
    );
  }

  public static Stream<Arguments> transitions() {
    return Stream.of(
      Arguments.of(States.CloseDoor, null, false, false, false, States.CloseDoor, false, true, false),
      Arguments.of(States.CloseDoor, null, true, false, false, States.CloseDoor, false, true, false),
      Arguments.of(States.CloseDoor, null, false, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.CloseDoor, null, false, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, null, true, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.CloseDoor, null, true, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, null, false, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, null, true, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, false, false, false, States.CloseDoor, false, true, false),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, true, false, false, States.CloseDoor, false, true, false),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, false, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, false, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, true, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, true, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, false, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.CLOSE, true, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, false, false, false, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, true, false, false, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, false, true, false, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, false, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, true, true, false, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, true, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, false, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.CloseDoor, DoorCMD.OPEN, true, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, null, false, false, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, null, true, false, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, null, false, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, null, false, false, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, null, true, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, null, true, false, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, null, false, true, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, null, true, true, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, false, false, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, true, false, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, false, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, false, false, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, true, true, false, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, true, false, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, false, true, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.CLOSE, true, true, true, States.DoorIsClosed, false, false, true),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, false, false, false, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, true, false, false, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, false, true, false, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, false, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, true, true, false, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, true, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, false, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.DoorIsClosed, DoorCMD.OPEN, true, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, null, false, false, false, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, null, true, false, false, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, null, false, true, false, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, null, false, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, null, true, true, false, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, null, true, false, true, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, null, false, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, null, true, true, true, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, false, false, false, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, true, false, false, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, false, true, false, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, false, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, true, true, false, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, true, false, true, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, false, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.CLOSE, true, true, true, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, false, false, false, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, true, false, false, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, false, true, false, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, false, false, true, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, true, true, false, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, true, false, true, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, false, true, true, States.OpenDoor, true, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, true, true, true, States.DoorIsOpen, false, false, false),
      Arguments.of(States.OpenDoor, DoorCMD.OPEN, true, true, true, States.DoorIsOpen, false, false, false)
    );
  }

  @ParameterizedTest
  @Order(4)
  @MethodSource("transitionsFromWait")
  public void testWaitTimer(@NotNull int ticks,
                            @NotNull States[] expStates,
                            @NotNull boolean[] expOpen,
                            @NotNull boolean[] expClose,
                            @NotNull boolean[] expClosed) {
    Preconditions.checkNotNull(expStates);
    Preconditions.checkNotNull(expOpen);
    Preconditions.checkNotNull(expClose);
    Preconditions.checkNotNull(expClosed);
    Preconditions.checkArgument(ticks > 0);
    Preconditions.checkArgument(expStates.length == ticks + 1);
    Preconditions.checkArgument(expOpen.length == ticks + 1);
    Preconditions.checkArgument(expClose.length == ticks + 1);
    Preconditions.checkArgument(expClosed.length == ticks + 1);

    // Given
    Door door = new Door();
    door.setUp();

    door.currentState = States.Wait;
    door.timer = ticks;

    States[] actStates = new States[ticks + 1];
    boolean[] actOpen = new boolean[ticks + 1];
    boolean[] actClose = new boolean[ticks + 1];
    boolean[] actClosed = new boolean[ticks + 1];

    // When
    for (int i = 0; i < ticks + 1; i++) {
      door.compute();

      actStates[i] = door.getCurrentState();
      actOpen[i] = door.getOpen().getValue();
      actClose[i] = door.getClose().getValue();

      door.tick();

      actClosed[i] = door.getClosed().getValue();
    }

    // Then
    assertAll(
      () -> assertThat(actStates).containsExactly(expStates),
      () -> assertThat(actOpen).containsExactly(expOpen),
      () -> assertThat(actClose).containsExactly(expClose),
      () -> assertThat(actClosed).containsExactly(expClosed)
    );
  }

  public static Stream<Arguments> transitionsFromWait() {
    return Stream.of(
      // 1
      Arguments.of(
        1, new States[]{States.Wait, States.CloseDoor},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 2
      Arguments.of(
        2, new States[]{States.Wait, States.Wait, States.CloseDoor},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false}
      ),
      // 3
      Arguments.of(
        3, new States[]{States.Wait, States.Wait, States.Wait, States.CloseDoor},
        new boolean[]{false, false, false, false},
        new boolean[]{false, false, false, false},
        new boolean[]{false, false, false, false}
      )
    );
  }

  @ParameterizedTest
  @Order(5)
  @MethodSource("transitionsFromDoorIsOpen")
  public void testDoorIsOpen(@NotNull int ticks,
                            @NotNull States[] expStates,
                            @NotNull boolean[] expOpen,
                            @NotNull boolean[] expClose,
                            @NotNull boolean[] expClosed) {
    Preconditions.checkNotNull(expStates);
    Preconditions.checkNotNull(expOpen);
    Preconditions.checkNotNull(expClose);
    Preconditions.checkNotNull(expClosed);
    Preconditions.checkArgument(ticks > 0);
    Preconditions.checkArgument(expStates.length == ticks + 1);
    Preconditions.checkArgument(expOpen.length == ticks + 1);
    Preconditions.checkArgument(expClose.length == ticks + 1);
    Preconditions.checkArgument(expClosed.length == ticks + 1);

    // Given
    Door door = new Door();
    door.setUp();

    door.currentState = States.DoorIsOpen;
    door.timer = ticks;

    States[] actStates = new States[ticks + 1];
    boolean[] actOpen = new boolean[ticks + 1];
    boolean[] actClose = new boolean[ticks + 1];
    boolean[] actClosed = new boolean[ticks + 1];

    // When
    for (int i = 0; i < ticks + 1; i++) {
      door.compute();

      actStates[i] = door.getCurrentState();
      actOpen[i] = door.getOpen().getValue();
      actClose[i] = door.getClose().getValue();

      door.tick();

      actClosed[i] = door.getClosed().getValue();
    }

    // Then
    assertAll(
      () -> assertThat(actStates).containsExactly(expStates),
      () -> assertThat(actOpen).containsExactly(expOpen),
      () -> assertThat(actClose).containsExactly(expClose),
      () -> assertThat(actClosed).containsExactly(expClosed)
    );
  }

  public static Stream<Arguments> transitionsFromDoorIsOpen() {
    return Stream.of(
      // 1
      Arguments.of(
        1, new States[]{States.DoorIsOpen, States.CloseDoor},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 2
      Arguments.of(
        2, new States[]{States.DoorIsOpen, States.DoorIsOpen, States.CloseDoor},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false}
      ),
      // 3
      Arguments.of(
        3, new States[]{States.DoorIsOpen, States.DoorIsOpen, States.DoorIsOpen, States.CloseDoor},
        new boolean[]{false, false, false, false},
        new boolean[]{false, false, false, false},
        new boolean[]{false, false, false, false}
      )
    );
  }

  @Test
  @Order(6)
  public void testTick() {
    // Given
    Door door = new Door();
    door.setUp();

    DoorCMD cmd = DoorCMD.CLOSE;
    door.getCmd().update(cmd);
    door.getIsOpen().update(true);
    door.getIsClosed().update(true);
    door.getIsObstacle().update(true);
    door.getOpen().setValue(true);
    door.getClose().setValue(true);
    door.getClosed().setValue(true);

    // When
    door.tick();

    // Then
    assertAll(
      () -> assertThat(door.getCmd().getValue()).isNotNull().isEqualTo(cmd),
      () -> assertThat(door.getIsOpen().getValue()).isNotNull().isEqualTo(true),
      () -> assertThat(door.getIsClosed().getValue()).isNotNull().isEqualTo(true),
      () -> assertThat(door.getIsObstacle().getValue()).isNotNull().isEqualTo(true),
      () -> assertThat(door.getOpen().getValue()).isNull(),
      () -> assertThat(door.getClose().getValue()).isNull(),
      () -> assertThat(door.getClosed().getValue()).isNotNull().isEqualTo(true)
    );
  }

  @Order(7)
  @ParameterizedTest
  @MethodSource("runs")
  public void testCompute(@NotNull DoorCMD[] cmd,
                          @NotNull boolean[] isOpen,
                          @NotNull boolean[] isClosed,
                          @NotNull boolean[] isObstacle,
                          @NotNull States[] expStates,
                          @NotNull boolean[] expOpen,
                          @NotNull boolean[] expClose,
                          @NotNull boolean[] expClosed) {
    Preconditions.checkNotNull(cmd);
    Preconditions.checkNotNull(isOpen);
    Preconditions.checkNotNull(isClosed);
    Preconditions.checkNotNull(isObstacle);
    Preconditions.checkNotNull(expStates);
    Preconditions.checkNotNull(expOpen);
    Preconditions.checkNotNull(expClose);
    Preconditions.checkNotNull(expClosed);
    Preconditions.checkArgument(cmd.length > 0);
    Preconditions.checkArgument(isOpen.length > 0);
    Preconditions.checkArgument(isClosed.length > 0);
    Preconditions.checkArgument(isObstacle.length > 0);
    Preconditions.checkArgument(isOpen.length == cmd.length);
    Preconditions.checkArgument(isClosed.length == cmd.length);
    Preconditions.checkArgument(isObstacle.length == cmd.length);
    Preconditions.checkArgument(expStates.length == cmd.length + 1);
    Preconditions.checkArgument(expOpen.length == cmd.length);
    Preconditions.checkArgument(expClose.length == cmd.length);
    Preconditions.checkArgument(expClosed.length == cmd.length);

    // Given
    Door door = new Door();
    door.setUp();

    States[] actStates = new States[expStates.length];
    boolean[] actOpen = new boolean[expOpen.length];
    boolean[] actClose = new boolean[expClose.length];
    boolean[] actClosed = new boolean[expClosed.length];


    // When
    door.init();
    actStates[0] = door.getCurrentState();

    for (int i = 0; i < cmd.length; i++) {
      door.getCmd().update(cmd[i]);
      door.getIsOpen().update(isOpen[i]);
      door.getIsClosed().update(isClosed[i]);
      door.getIsObstacle().update(isObstacle[i]);

      door.compute();

      actOpen[i] = door.getOpen().getValue();
      actClose[i] = door.getClose().getValue();
      actStates[i + 1] = door.getCurrentState();

      door.tick();

      actClosed[i] = door.getClosed().getValue();
    }

    // Then
    assertAll(
      () -> assertThat(actStates).containsExactly(expStates),
      () -> assertThat(actOpen).containsExactly(expOpen),
      () -> assertThat(actClose).containsExactly(expClose),
      () -> assertThat(actClosed).containsExactly(expClosed)
    );
  }

  public static Stream<Arguments> runs() {
    return Stream.of(
      // 1 - Timer
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE},
        new boolean[]{false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, false},
        new States[]{States.Wait, States.Wait, States.Wait, States.Wait,
          States.Wait, States.Wait, States.CloseDoor, States.CloseDoor},
        new boolean[]{false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, true},
        new boolean[]{false, false, false, false, false, false, false}
      ),
      // 2 - Close the door
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE
          , DoorCMD.CLOSE, DoorCMD.CLOSE},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, false, false, true},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new States[]{States.Wait, States.Wait, States.Wait, States.Wait,
          States.Wait, States.Wait, States.CloseDoor, States.CloseDoor,
          States.CloseDoor, States.DoorIsClosed},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, true, true, false},
        new boolean[]{false, false, false, false, false, false, false, false, true}
      ),
      // 3 - Open the door
      Arguments.of(
        new DoorCMD[]{DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN,
          DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN},
        new boolean[]{false, false, false, false, false, false, false, false, true},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new States[]{States.Wait, States.Wait, States.Wait, States.Wait,
          States.Wait, States.Wait, States.CloseDoor, States.OpenDoor,
          States.OpenDoor, States.DoorIsOpen},
        new boolean[]{false, false, false, false, false, false, true, true, false},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, false, false, false}
      ),
      // 4 - Obstacle
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE, DoorCMD.CLOSE},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false, false, false, true},
        new States[]{States.Wait, States.Wait, States.Wait, States.Wait,
          States.Wait, States.Wait, States.CloseDoor, States.CloseDoor,
          States.CloseDoor, States.OpenDoor},
        new boolean[]{false, false, false, false, false, false, false, false, true},
        new boolean[]{false, false, false, false, false, false, true, true, false},
        new boolean[]{false, false, false, false, false, false, false, false, false}
      ),
      // 5 - Close the door, then open it
      Arguments.of(
        new DoorCMD[]{DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.OPEN, DoorCMD.OPEN,
          DoorCMD.OPEN, DoorCMD.OPEN},
        new boolean[]{false, false, false, false, false, false,
          false, false, false, false, false, false, true},
        new boolean[]{false, false, false, false, false, false,
          false, false, true, false, false, false, false},
        new boolean[]{false, false, false, false, false, false,
          false, false, false, false, false, false, false},
        new States[]{States.Wait, States.Wait, States.Wait, States.Wait,
          States.Wait, States.Wait, States.CloseDoor, States.CloseDoor,
          States.CloseDoor, States.DoorIsClosed, States.OpenDoor,
          States.OpenDoor, States.OpenDoor, States.DoorIsOpen},
        new boolean[]{false, false, false, false, false, false,
          false, false, false, true, true, true, false},
        new boolean[]{false, false, false, false, false, false,
          true, true, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false,
          false, false, true, false, false, false, false}
      ),
      // 6 - Open the door, then close it
      Arguments.of(
        new DoorCMD[]{DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN,
          DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.OPEN,
          DoorCMD.OPEN, DoorCMD.OPEN, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE, DoorCMD.CLOSE,
          DoorCMD.CLOSE},
        new boolean[]{false, false, false, false, false, false,
          false, false, true, true, true, true, true, false, false, false},
        new boolean[]{false, false, false, false, false, false,
          false, false, false, false, false, false, false, false, false, true},
        new boolean[]{false, false, false, false, false, false,
          false, false, false, false, false, false, false, false, false, false},
        new States[]{States.Wait, States.Wait, States.Wait, States.Wait,
          States.Wait, States.Wait, States.CloseDoor, States.OpenDoor,
          States.OpenDoor, States.DoorIsOpen, States.DoorIsOpen,
          States.DoorIsOpen, States.DoorIsOpen, States.CloseDoor,
          States.CloseDoor, States.CloseDoor, States.DoorIsClosed},
        new boolean[]{false, false, false, false, false, false,
          true, true, false, false, false, false, false, false, false, false},
        new boolean[]{false, false, false, false, false, false,
          false, false, false, false, false, false, false, true, true, false},
        new boolean[]{false, false, false, false, false, false,
          false, false, false, false, false, false, false, false, false, true}
      )
    );
  }

}
