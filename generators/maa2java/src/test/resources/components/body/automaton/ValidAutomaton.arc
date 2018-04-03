package components.body.automaton;

import types.Datatypes.SpeedCmd;

/**
 * Valid model.
 */
component ValidAutomaton(Integer defaultSpeed) {
  port
    in SpeedCmd cmd,
    in Integer distance,
    out Integer speed,
    out String log;

  automaton BumpSpeed {
    state Static, Dynamic;

    initial Dynamic;

	Dynamic -> Static {cmd == SpeedCmd.Static} / {speed = defaultSpeed, log = "Static Speed"};
	Static -> Dynamic {cmd == SpeedCmd.Dynamic} / {speed = defaultSpeed>=(int)((float)distance/255.0f)?defaultSpeed:(int)((float)distance/255.0f), log = "Dynamic Speed"};
	Dynamic -> Dynamic / {speed = (Integer)(defaultSpeed>=(int)((float)distance/255.0f)?defaultSpeed:(int)((float)distance/255.0f))};	
  }
}