/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton;

import types.Datatypes.MotorCommand;
import types.Datatypes.TimerSignal;
import types.Datatypes.TimerCmd;

/**
 * Valid model.
 */
component BumpControl {
  port
    in Double distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCommand right,
    out MotorCommand left;

  automaton BumpControl {
    state Idle, Driving, Backing, Turning;

    initial Idle / {right = MotorCommand.STOP, left = MotorCommand.STOP};

    Idle -> Driving  / {right = MotorCommand.FORWARD, left = MotorCommand.FORWARD};
    Driving -> Backing [distance < 5] / {right = MotorCommand.BACKWARD, left = MotorCommand.BACKWARD, timer = TimerCmd.SINGLE};
    Backing -> Turning [signal == TimerSignal.ALERT] / {right = MotorCommand.BACKWARD, left = MotorCommand.FORWARD, timer = TimerCmd.DOUBLE};
    Turning -> Driving [signal == TimerSignal.ALERT] / {left = MotorCommand.FORWARD, right = MotorCommand.FORWARD};

  }
}
