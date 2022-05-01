/* (c) https://github.com/MontiCore/monticore */
package ilc;

import ilc.signals.*;

component LightCtrl {
  timing sync;

  port in ModeCmd;
  port out LightCmd cmd;

  mode Default {
    initial;

    port in DoorStatus;

    DoorEval doorEval;
    Arbiter arbiter;

    doorStatus -> doorEval.status;
    doorEval.request -> arbiter.request;
    arbiter.lightCmd -> cmd;
  }

  mode Comfort {
    MotorEval motorEval;
    Arbiter arbiter;

    port in MotorStatus;

    motorStatus -> motorEval.status;
    motorEval.request -> arbiter.request;
    arbiter.lightCmd -> cmd;
  }

  mode automaton {
    Default -> Comfort [ modeCmd == COMFORT ];
    Comfort -> Default [ modeCmd == DEFAULT ];
  }
}
