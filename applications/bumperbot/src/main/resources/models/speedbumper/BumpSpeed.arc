/* (c) https://github.com/MontiCore/monticore */
package speedbumper;

import speedbumper.SpeedCmd;

import java.lang.Integer;

component BumpSpeed(Integer defaultSpeed) {
  port
    in SpeedCmd cmd,
    in Integer distance,
    out Integer speed;

  Integer count255=0;

  automaton {

    state Static;
    initial state Dynamic;

    Dynamic -> Static [cmd == SpeedCmd.Static] / {speed = defaultSpeed; count255=0;};

    Static -> Dynamic [count255 < 10 && cmd == SpeedCmd.Dynamic] / {speed = defaultSpeed; count255++;};
    Static -> Dynamic [count255 >= 10 && cmd == SpeedCmd.Dynamic] / {speed = defaultSpeed>=(distance)?defaultSpeed:(distance);};

    Dynamic -> Dynamic [count255 < 10 && cmd == SpeedCmd.Dynamic] / {speed = defaultSpeed; count255++;};
    Dynamic -> Dynamic [count255 >= 10 && cmd ==  SpeedCmd.Dynamic] / {speed = defaultSpeed>=(distance)?defaultSpeed:(distance);};
  }
}