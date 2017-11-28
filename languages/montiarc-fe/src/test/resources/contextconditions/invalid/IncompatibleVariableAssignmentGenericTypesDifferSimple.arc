package contextconditions.invalid;

import contextconditions.valid.Datatypes.MotorCommand;
import contextconditions.valid.Datatypes.TimerSignal;
import contextconditions.valid.Datatypes.TimerCmd;
import java.util.HashMap;

component IncompatibleVariableAssignmentGenericTypesDifferSimple {
  
  var HashMap<String, Integer> stateChanges;

  automaton BumpControl {
    state Idle;

    initial Idle / {stateChanges.put("foo", 5)};

    Idle -> Idle / {stateChanges.put("asd", 4)};

  }
}