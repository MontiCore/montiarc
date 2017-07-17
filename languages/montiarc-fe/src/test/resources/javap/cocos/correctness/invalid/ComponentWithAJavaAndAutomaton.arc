package javap.cocos.correctness.invalid;

import javap.valid.bumperbot.Datatypes.MotorCommand;
import javap.valid.bumperbot.Datatypes.TimerSignal;
import javap.valid.bumperbot.Datatypes.TimerCmd;

component ComponentWithAJavaAndAutomaton {

  port
    in Double distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCommand right,
    out MotorCommand left;

  automaton {
    state Idle, Driving, Backing, Turning;

    initial Idle / {right = MotorCommand.STOP, left = MotorCommand.STOP};

    Idle -> Driving  / {right = MotorCommand.FORWARD, left = MotorCommand.FORWARD};
    Driving -> Backing [distance < 5] / {right = MotorCommand.BACKWARD, left = MotorCommand.BACKWARD, timer = TimerCmd.SINGLE};
    Backing -> Turning {signal == TimerSignal.ALERT} / {right = MotorCommand.BACKWARD, left = MotorCommand.FORWARD, timer = TimerCmd.DOUBLE};
    Turning -> Driving {signal == TimerSignal.ALERT} / {left = MotorCommand.FORWARD, right = MotorCommand.FORWARD};
  }
  
  compute IncreaseDistance {
    distance--;
  }
}