package components.body.automaton.transition.assignments;

import types.Datatypes.MotorCommand;
import types.Datatypes.TimerSignal;
import types.Datatypes.TimerCmd;
import java.util.HashMap;

/*
 * Invalid model.
 *
 * @implements TODO
 */
component IncompatibleVariableAssignmentGenericTypesDiffer {
  port
    in Double distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCommand right,
    out MotorCommand left;
    
  HashMap<String, Integer> stateChanges;

  automaton BumpControl {
    state Idle, Driving, Backing, Turning;

    initial Idle / {stateChanges = new HashMap<String, Integer>(), right = MotorCommand.STOP, left = MotorCommand.STOP};

    Idle -> Driving  / {right = MotorCommand.FORWARD, left = MotorCommand.FORWARD, stateChanges.put("Idle",1)}; 
    Driving -> Backing [distance < 5] / {right = MotorCommand.BACKWARD, left = MotorCommand.BACKWARD, timer = TimerCmd.SINGLE,  stateChanges.put("Driving",1)}; 
    Backing -> Turning [signal == TimerSignal.ALERT] / {right = MotorCommand.BACKWARD, left = MotorCommand.FORWARD, timer = TimerCmd.DOUBLE};
    Turning -> Driving [signal == TimerSignal.ALERT] / {left = MotorCommand.FORWARD, right = MotorCommand.FORWARD};

  }
}