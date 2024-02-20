/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.bigModel;

import evaluation.Commands.LiftCMD;

component Lift {

  port in LiftCMD cmd;
  port out Boolean up;
  port out Boolean down;

  <<sync>> automaton {
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
