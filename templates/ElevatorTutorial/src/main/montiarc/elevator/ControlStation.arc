package elevator;

import elevator.Commands.MotorCMD;
import java.util.TreeSet;
import java.util.Optional;

component ControlStation {
  port in int requestOnFloor;
  port out boolean openDoor;
  port sync in double motorPosition;
  port sync out MotorCMD motorCommand;

  TreeSet<int> pendingRequests = TreeSet.TreeSet();
  Optional<int> targetFloor = Optional.empty();

  <<delayed>> automaton {
    initial state Idle {
      do / {
        motorCommand = MotorCMD.STOP;
      }
    }

    state OpenDoor {
      entry / {
        openDoor = true;
      }
      do / {
        motorCommand = MotorCMD.STOP;
      }
      exit / {
        openDoor = false;
      }

      initial state IdleOpenDoor;

      IdleOpenDoor -> IdleOpenDoor requestOnFloor / {
        pendingRequests.add(requestOnFloor);
      }
      IdleOpenDoor -> IdleOpenDoor [!pendingRequests.isEmpty() && targetFloor.isEmpty()] / {
        int next = pendingRequests.first();
        pendingRequests.remove(next);
        targetFloor = Optional.of(next);
      }
    }

    state MovingUp {
      do / {
        motorCommand = MotorCMD.UP;
      }
    }
    state MovingDown {
      do / {
        motorCommand = MotorCMD.DOWN;
      }
    }

    Idle -> MovingUp   [targetFloor.isPresent() && targetFloor.get() > motorPosition];
    Idle -> MovingDown [targetFloor.isPresent() && targetFloor.get() < motorPosition];

    MovingUp   -> Idle     [targetFloor.isEmpty()];
    MovingUp   -> OpenDoor [targetFloor.isPresent() && targetFloor.get() == motorPosition] / {
      targetFloor = Optional.empty();
    }
    MovingDown -> Idle     [targetFloor.isEmpty()];
    MovingDown -> OpenDoor [targetFloor.isPresent() && targetFloor.get() == motorPosition] / {
      targetFloor = Optional.empty();
    }

    Idle -> OpenDoor [targetFloor.isPresent() && targetFloor.get() == motorPosition] / {
      targetFloor = Optional.empty();
    }

    Idle -> Idle requestOnFloor / {
      pendingRequests.add(requestOnFloor);
    }
    MovingUp -> MovingUp requestOnFloor / {
      pendingRequests.add(requestOnFloor);
    }
    MovingDown -> MovingDown requestOnFloor / {
      pendingRequests.add(requestOnFloor);
    }

    Idle -> Idle [!pendingRequests.isEmpty() && targetFloor.isEmpty()] / {
      int next = pendingRequests.first();
      pendingRequests.remove(next);
      targetFloor = Optional.of(next);
    }

    OpenDoor -> Idle [targetFloor.isPresent()];

  }
}
