package component.body.automaton.transition.assignments;

import types.Datatypes.MotorCommand;
import types.Datatypes.TimerSignal;
import types.Datatypes.TimerCmd;
import java.util.HashMap;

component IncompatibleVariableAssignmentGenericTypesDifferSimple {
  
  var HashMap<String, Integer> stateChanges;

  automaton BumpControl {
    state Idle;
    initial Idle / {stateChanges.put("foo", 5)};
    Idle -> Idle / {stateChanges.put("asd", 4)};
  }
}