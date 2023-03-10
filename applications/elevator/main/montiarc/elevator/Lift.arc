/* (c) https://github.com/MontiCore/monticore */
package elevator;

import elevator.Commands.LiftCMD;

component Lift {

  port <<sync>> in LiftCMD cmd;
  port <<sync>> out boolean up;
  port <<sync>> out boolean down;

  automaton {
    initial state Wait;

    Wait -> Wait [cmd == null || cmd == LiftCMD.STOP] / {
      up = false;
      down = false;
    };
    Wait -> Up [cmd != null && cmd == LiftCMD.UP] / {
      up = true;
      down = false;
    };
    Wait -> Down [cmd != null && cmd == LiftCMD.DOWN] / {
      up = false;
      down = true;
    };

    state Up;

    Up -> Wait [cmd != null && cmd == LiftCMD.STOP] / {
       up = false;
       down = false;
     };
    Up -> Up [cmd == null || cmd == LiftCMD.UP] / {
      up = true;
      down = false;
    };
    Up -> Down [cmd != null && cmd == LiftCMD.DOWN] / {
      up = false;
      down = true;
    };

    state Down;

    Down -> Wait [cmd != null && cmd == LiftCMD.STOP] / {
       up = false;
       down = false;
     };
    Down -> Up [cmd != null && cmd == LiftCMD.UP] / {
      up = true;
      down = false;
    };
    Down -> Down [cmd == null || cmd == LiftCMD.DOWN] / {
      up = false;
      down = true;
    };
  }
}
