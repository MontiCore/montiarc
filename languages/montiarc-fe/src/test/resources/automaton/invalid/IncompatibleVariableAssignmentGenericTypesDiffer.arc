package automaton.invalid;

import automaton.valid.bumperbot.Datatypes.MotorCommand;
import automaton.valid.bumperbot.Datatypes.TimerSignal;
import automaton.valid.bumperbot.Datatypes.TimerCmd;
import java.util.HashMap;

component IncompatibleVariableAssignmentGenericTypesDiffer {
  port
    in Double distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCommand right,
    out MotorCommand left;
    
  var HashMap<String,Integer> stateChanges;// = new HashMap<String,Integer>();

  automaton BumpControl {
    state Idle, Driving, Backing, Turning;

    initial Idle / {right = MotorCommand.STOP, left = MotorCommand.STOP};

    Idle -> Driving  / {right = MotorCommand.FORWARD, left = MotorCommand.FORWARD, stateChanges = "1"}; 
    Driving -> Backing [distance < 5] / {right = MotorCommand.BACKWARD, left = MotorCommand.BACKWARD, timer = TimerCmd.SINGLE, stateChanges++}; 
    Backing -> Turning {signal == TimerSignal.ALERT} / {right = MotorCommand.BACKWARD, left = MotorCommand.FORWARD, timer = TimerCmd.DOUBLE};
    Turning -> Driving {signal == TimerSignal.ALERT} / {left = MotorCommand.FORWARD, right = MotorCommand.FORWARD};

  }
}