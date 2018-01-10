package contextconditions.valid;

import contextconditions.valid.Types.*;

component TransitiveAdaptedTypeMatching {

  port out MotorCmd;
  
  automaton AmbiguousMatching {
    state Idle; 

    initial Idle / {MotorCmd.FORWARD};
  
  }


}