package bumperbot;

import bumperbot.Datatypes.*;

component BumpSpeed(Integer defaultSpeed) {
  port
    in SpeedCmd cmd,
    in Integer distance,
    out Integer speed;

  var Integer count255;


  automaton BumpSpeed {
    
    state Static, Dynamic;
    initial Dynamic / {count255 = 10};

    Dynamic -> Static {cmd == SpeedCmd.Static} / {speed = defaultSpeed, count255=0};
  }
}
  