/* (c) https://github.com/MontiCore/monticore */
package evaluation.bigModel;

import evaluation.Commands.DoorCMD;
import evaluation.Commands.LiftCMD;
import evaluation.Commands.Direction;

component Controller {

  port in Boolean req1, req2, req3, req4,
       in Boolean at1, at2, at3, at4,
       in Boolean isClosed,
       out DoorCMD door,
       out LiftCMD lift,
       <<delayed>> out Integer clear;

  Direction directions = Direction.NA;
  int current = 0;
  int target = 0;
  Float timer = 5.0f;
  boolean stopNext = false;

  <<sync>> automaton {
    initial { clear = 0; } state Init {

      initial state WaitTimer;

      WaitTimer -> WaitTimer [timer > 0.625f] / {
        timer = timer * 0.5f;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      WaitTimer -> CloseDoor [timer == 0.625f] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      state CloseDoor;

      CloseDoor -> CloseDoor [!isClosed] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      CloseDoor -> OK [at1 && isClosed] / {
        current = 1;
        directions = Direction.UP;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      CloseDoor -> DriveDown [!at1 && isClosed] / {
        lift = LiftCMD.DOWN;
        clear = 0;
        door = DoorCMD.NULL;
      };

      state DriveDown;

      DriveDown -> DriveDown [!at1] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      DriveDown -> OK [at1 == true] / {
        current = 1;
        directions = Direction.UP;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      state OK;

      OK -> WaitReq / {
        stopNext = true;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
    };

    state WaitReq {
      initial state SearchFloor1;

      SearchFloor1 -> SearchFloor2 [!req1 && directions == Direction.UP] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor1 -> SearchFloor4 [!req1 && directions == Direction.DOWN] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor1 -> Found [req1 == true] / {
        target = 1;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
    };

      state SearchFloor2;

      SearchFloor2 -> SearchFloor3 [!req2 && directions == Direction.UP] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor2 -> SearchFloor1 [!req2 && directions == Direction.DOWN] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor2 -> Found [req2 == true] / {
        target = 2;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      state SearchFloor3;

      SearchFloor3 -> SearchFloor4 [!req3 && directions == Direction.UP] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor3 -> SearchFloor2 [!req3 && directions == Direction.DOWN] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor3 -> Found [req3 == true] / {
        target = 3;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      state SearchFloor4;

      SearchFloor4 -> SearchFloor1 [!req4 && directions == Direction.UP] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor4 -> SearchFloor3 [!req4 && directions == Direction.DOWN] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      SearchFloor4 -> Found [req4 == true] / {
        target = 4;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      state Found;

      Found -> Continue [current < target] / {
        directions = Direction.UP;
        stopNext = false;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      Found -> Continue [current > target] / {
        directions = Direction.DOWN;
        stopNext = false;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      Found -> Continue [current == target] / {
        stopNext = true;
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };

      state Continue;

      Continue -> Floor1 [target == 1] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      Continue -> Floor2 [target == 2] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      Continue -> Floor3 [target == 3] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
      };
      Continue -> Floor4 [target == 4] / {
        clear = 0;
        door = DoorCMD.NULL;
        lift = LiftCMD.NULL;
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
    Floor1 -> Floor2 [directions == Direction.UP && isClosed && at1
                      && !req2 && !stopNext] / {
      current = 2;
      stopNext = false;
      lift = LiftCMD.UP;
      clear = 0;
      door = DoorCMD.NULL;
    };
    // up and stop at 2
    Floor1 -> Floor2 [directions == Direction.UP && isClosed && at1
                      && req2 && !stopNext] / {
       current = 2;
       stopNext = true;
       lift = LiftCMD.UP;
       door = DoorCMD.NULL;
       clear = 0;
    };

    state Floor2;

    // stop at 2 and open door
    Floor2 -> Door2 [at2 && req2 && stopNext] / {
      lift = LiftCMD.STOP;
      door = DoorCMD.OPEN;
      clear = 2;
    };
    // up but do not stop at 3
    Floor2 -> Floor3 [directions == Direction.UP && isClosed && at2
                      && !req3 && !stopNext] / {
      current = 3;
      stopNext = false;
      lift = LiftCMD.UP;
      clear = 0;
      door = DoorCMD.NULL;
    };
    // up and stop at 3
    Floor2 -> Floor3 [directions == Direction.UP && isClosed && at2
                      && req3 && !stopNext] / {
       current = 3;
       stopNext = true;
       lift = LiftCMD.UP;
       door = DoorCMD.NULL;
       clear = 0;
    };
    // down at stop at 1
    Floor2 -> Floor1 [directions == Direction.DOWN && isClosed && at2
                      && !stopNext] / {
       current = 1;
       stopNext = true;
       lift = LiftCMD.DOWN;
       door = DoorCMD.NULL;
       clear = 0;

    };

    state Floor3;

    // stop at 3 and open door
    Floor3 -> Door3 [at3 && req3 && stopNext] / {
      lift = LiftCMD.STOP;
      door = DoorCMD.OPEN;
      clear = 3;
    };
    // up and stop at 4
    Floor3 -> Floor4 [directions == Direction.UP && isClosed && at3
                      && !stopNext] / {
       current = 4;
       stopNext = true;
       lift = LiftCMD.UP;
       door = DoorCMD.NULL;
       clear = 0;

    };
    // down but do not stop at 2
    Floor3 -> Floor2 [directions == Direction.DOWN && isClosed && at3
                      && !req2 && !stopNext] / {
       current = 2;
       stopNext = false;
       lift = LiftCMD.DOWN;
       door = DoorCMD.NULL;
       clear = 0;
    };
    // down and stop at 2
    Floor3 -> Floor2 [directions == Direction.DOWN && isClosed && at3
                      && req2 && !stopNext] / {
       current = 2;
       stopNext = true;
       lift = LiftCMD.DOWN;
       door = DoorCMD.NULL;
       clear = 0;
    };

    state Floor4;

    // stop at 4 and open door
    Floor4 -> Door4 [at4 && req4 && stopNext] / {
      lift = LiftCMD.STOP;
      door = DoorCMD.OPEN;
      clear = 4;
    };
    // down but do not stop at 3
    Floor4 -> Floor3 [directions == Direction.DOWN && isClosed && at4
                      && !req3 && !stopNext] / {
       current = 3;
       stopNext = false;
       lift = LiftCMD.DOWN;
       door = DoorCMD.NULL;
       clear = 0;
    };
    // down and stop at 3
    Floor4 -> Floor3 [directions == Direction.DOWN && isClosed && at4
                      && req3 && !stopNext] / {
       current = 3;
       stopNext = true;
       lift = LiftCMD.DOWN;
       door = DoorCMD.NULL;
       clear = 0;
    };

    state Door1;

    Door1 -> Door1 [req1 && at1] / {
      door = DoorCMD.OPEN;
      clear = 1;
      lift = LiftCMD.NULL;
    };
    Door1 -> SearchFloor1 [!req1 && isClosed];

    state Door2;

    Door2 -> Door2 [req2 && at2] / {
      door = DoorCMD.OPEN;
      clear = 2;
      lift = LiftCMD.NULL;
    };
    Door2 -> SearchFloor2 [!req2 && isClosed];

    state Door3;

    Door3 -> Door3 [req3 && at3] / {
      door = DoorCMD.OPEN;
      clear = 3;
      lift = LiftCMD.NULL;
    };
    Door3 -> SearchFloor3 [!req3 && isClosed];

    state Door4;

    Door4 -> Door4 [req4 && at4] / {
      door = DoorCMD.OPEN;
      clear = 4;
      lift = LiftCMD.NULL;
    };

    Door4 -> SearchFloor4 [!req4 && isClosed];
  }
}
