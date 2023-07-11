/* (c) https://github.com/MontiCore/monticore */
package evaluation.bigModel;

import evaluation.Commands.LiftCMD;

component Lift {

  port <<sync>> in LiftCMD cmd;
  port <<sync>> out Boolean up;
  port <<sync>> out Boolean down;

  automaton {
    initial state Wait;

    Wait -> Wait [cmd == LiftCMD.NULL || cmd == LiftCMD.STOP] / {
      up = false;
      down = false;
    };

    Wait -> Up [cmd != LiftCMD.NULL && cmd == LiftCMD.UP] / {
      up = true;
      down = false;
    };
    Wait -> Down [cmd != LiftCMD.NULL && cmd == LiftCMD.DOWN] / {
      up = false;
      down = true;
    };

    state Up;

    Up -> Wait [cmd != LiftCMD.NULL && cmd == LiftCMD.STOP] / {
       up = false;
       down = false;
     };
    Up -> Up [cmd == LiftCMD.NULL || cmd == LiftCMD.UP] / {
      up = true;
      down = false;
    };
    Up -> Down [cmd != LiftCMD.NULL && cmd == LiftCMD.DOWN] / {
      up = false;
      down = true;
    };

    state Down;

    Down -> Wait [cmd != LiftCMD.NULL && cmd == LiftCMD.STOP] / {
       up = false;
       down = false;
     };
    Down -> Up [cmd != LiftCMD.NULL && cmd == LiftCMD.UP] / {
      up = true;
      down = false;
    };
    Down -> Down [cmd == LiftCMD.NULL || cmd == LiftCMD.DOWN] / {
      up = false;
      down = true;
    };
  }
}
