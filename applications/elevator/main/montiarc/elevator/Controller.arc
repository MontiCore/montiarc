/* (c) https://github.com/MontiCore/monticore */
package elevator;

import elevator.Commands.DoorCMD;
import elevator.Commands.LiftCMD;
import elevator.Commands.Direction;

component Controller {

  port <<sync>> in boolean req1, req2, req3, req4,
       <<sync>> in boolean at1, at2, at3, at4,
       <<sync>> in boolean isClosed,
       <<sync>> out DoorCMD door,
       <<sync>> out LiftCMD lift,
       <<sync, delayed>> out int clear;

  Direction direction = Direction.NA;
  int current = 0;
  int target = 0;
  int timer = 5;
  boolean stopNext = false;

  automaton {
    initial { clear = 0; } state Init {

      initial state WaitTimer;

      WaitTimer -> WaitTimer [timer > 0] / {
        timer = timer - 1;
        clear = 0;
      };
      WaitTimer -> CloseDoor [timer == 0] / {
        clear = 0;
      };

      state CloseDoor;

      CloseDoor -> CloseDoor [!isClosed] / {
        clear = 0;
      };

      CloseDoor -> OK [at1 && isClosed] / {
        current = 1;
        direction = Direction.UP;
        clear = 0;
      };
      CloseDoor -> DriveDown [!at1 && isClosed] / {
        lift = LiftCMD.DOWN;
        clear = 0;
      };

      state DriveDown;

      DriveDown -> DriveDown [!at1] / {
        clear = 0;
      };

      DriveDown -> OK [at1] / {
        current = 1;
        direction = Direction.UP;
        clear = 0;
      };

      state OK;

      OK -> WaitReq / {
        stopNext = true;
        clear = 0;
      };
    };

    state WaitReq {
      initial state SearchFloor1;

      SearchFloor1 -> SearchFloor2 [!req1 && direction == Direction.UP] / {
        clear = 0;
      };
      SearchFloor1 -> SearchFloor4 [!req1 && direction == Direction.DOWN] / {
        clear = 0;
      };
      SearchFloor1 -> Found [req1] / {
        target = 1;
        clear = 0;
      };

      state SearchFloor2;

      SearchFloor2 -> SearchFloor3 [!req2 && direction == Direction.UP] / {
        clear = 0;
      };
      SearchFloor2 -> SearchFloor1 [!req2 && direction == Direction.DOWN] / {
        clear = 0;
      };
      SearchFloor2 -> Found [req2] / {
        target = 2;
        clear = 0;
      };

      state SearchFloor3;

      SearchFloor3 -> SearchFloor4 [!req3 && direction == Direction.UP] / {
        clear = 0;
      };
      SearchFloor3 -> SearchFloor2 [!req3 && direction == Direction.DOWN] / {
        clear = 0;
      };
      SearchFloor3 -> Found [req3] / {
        target = 3;
        clear = 0;
      };

      state SearchFloor4;

      SearchFloor4 -> SearchFloor1 [!req4 && direction == Direction.UP] / {
        clear = 0;
      };
      SearchFloor4 -> SearchFloor3 [!req4 && direction == Direction.DOWN] / {
        clear = 0;
      };
      SearchFloor4 -> Found [req4] / {
        target = 4;
        clear = 0;
      };

      state Found;

      Found -> Continue [current < target] / {
        direction = Direction.UP;
        stopNext = false;
        clear = 0;
      };
      Found -> Continue [current > target] / {
        direction = Direction.DOWN;
        stopNext = false;
        clear = 0;
      };
      Found -> Continue [current == target] / {
        stopNext = true;
        clear = 0;
      };

      state Continue;

      Continue -> Floor1 [target == 1] / {
        clear = 0;
      };
      Continue -> Floor2 [target == 2] / {
        clear = 0;
      };
      Continue -> Floor3 [target == 3] / {
        clear = 0;
      };
      Continue -> Floor4 [target == 4] / {
        clear = 0;
      };
    };

    state Floor1;

    // stop at 1 and open door
    Floor1 -> Door1 [at1 && req1 && stopNext] / {
      lift = LiftCMD.STOP;
      door = DoorCMD.OPEN;
      clear = 1;
    };
    // up but do not stop at 2
    Floor1 -> Floor2 [direction == Direction.UP && isClosed && at1
                      && !req2 && !stopNext] / {
      current = 2;
      stopNext = false;
      lift = LiftCMD.UP;
      clear = 0;
    };
    // up and stop at 2
    Floor1 -> Floor2 [direction == Direction.UP && isClosed && at1
                      && req2 && !stopNext] / {
       current = 2;
       stopNext = true;
       lift = LiftCMD.UP;
    };

    state Floor2;

    // stop at 2 and open door
    Floor2 -> Door2 [at2 && req2 && stopNext] / {
      lift = LiftCMD.STOP;
      door = DoorCMD.OPEN;
      clear = 2;
    };
    // up but do not stop at 3
    Floor2 -> Floor3 [direction == Direction.UP && isClosed && at2
                      && !req3 && !stopNext] / {
      current = 3;
      stopNext = false;
      lift = LiftCMD.UP;
      clear = 0;
    };
    // up and stop at 3
    Floor2 -> Floor3 [direction == Direction.UP && isClosed && at2
                      && req3 && !stopNext] / {
       current = 3;
       stopNext = true;
       lift = LiftCMD.UP;
    };
    // down at stop at 1
    Floor2 -> Floor1 [direction == Direction.DOWN && isClosed && at2
                      && !stopNext] / {
       current = 1;
       stopNext = true;
       lift = LiftCMD.DOWN;
    };

    state Floor3;

    // stop at 3 and open door
    Floor3 -> Door3 [at3 && req3 && stopNext] / {
      lift = LiftCMD.STOP;
      door = DoorCMD.OPEN;
      clear = 3;
    };
    // up and stop at 4
    Floor3 -> Floor4 [direction == Direction.UP && isClosed && at3
                      && !stopNext] / {
       current = 4;
       stopNext = true;
       lift = LiftCMD.UP;
    };
    // down but do not stop at 2
    Floor3 -> Floor2 [direction == Direction.DOWN && isClosed && at3
                      && !req2 && !stopNext] / {
       current = 2;
       stopNext = false;
       lift = LiftCMD.DOWN;
    };
    // down and stop at 2
    Floor3 -> Floor2 [direction == Direction.DOWN && isClosed && at3
                      && req2 && !stopNext] / {
       current = 2;
       stopNext = true;
       lift = LiftCMD.DOWN;
    };

    state Floor4;

    // stop at 4 and open door
    Floor4 -> Door4 [at4 && req4 && stopNext] / {
      lift = LiftCMD.STOP;
      door = DoorCMD.OPEN;
      clear = 4;
    };
    // down but do not stop at 3
    Floor4 -> Floor3 [direction == Direction.DOWN && isClosed && at4
                      && !req3 && !stopNext] / {
       current = 3;
       stopNext = false;
       lift = LiftCMD.DOWN;
    };
    // down and stop at 3
    Floor4 -> Floor3 [direction == Direction.DOWN && isClosed && at4
                      && req3 && !stopNext] / {
       current = 3;
       stopNext = true;
       lift = LiftCMD.DOWN;
    };

    state Door1;

    Door1 -> Door1 [req1 && at1] / {
      door = DoorCMD.OPEN;
      clear = 1;
    };
    Door1 -> SearchFloor1 [!req1 && isClosed];

    state Door2;

    Door2 -> Door2 [req2 && at2] / {
      door = DoorCMD.OPEN;
      clear = 2;
    };
    Door2 -> SearchFloor2 [!req2 && isClosed];

    state Door3;

    Door3 -> Door3 [req3 && at3] / {
      door = DoorCMD.OPEN;
      clear = 3;
    };
    Door3 -> SearchFloor3 [!req3 && isClosed];

    state Door4;

    Door4 -> Door4 [req4 && at4] / {
      door = DoorCMD.OPEN;
      clear = 4;
    };
    Door4 -> SearchFloor4 [!req4 && isClosed];
  }
}
