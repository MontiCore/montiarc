/* (c) https://github.com/MontiCore/monticore */
package components.body;

import types.Datatypes.MotorCommand;
import types.Datatypes.TimerSignal;
import types.Datatypes.TimerCmd;

/**
 * Invalid model.
 * @implements [Wor16] MU2:  Each atomic component contains at most one
 *  behavior model (p. 55. Lst. 4.6)
 */
component ComponentWithAJavaAndAutomaton {

  port
    in Integer distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCommand right,
    out MotorCommand left;

  automaton {
    state Idle, Driving, Backing, Turning;

    initial Idle / {right = MotorCommand.STOP, left = MotorCommand.STOP};

    Idle -> Driving  / {right = MotorCommand.FORWARD, left = MotorCommand.FORWARD};
    Driving -> Backing [distance < 5] / {right = MotorCommand.BACKWARD, left = MotorCommand.BACKWARD, timer = TimerCmd.SINGLE};
    Backing -> Turning [signal == TimerSignal.ALERT] / {right = MotorCommand.BACKWARD, left = MotorCommand.FORWARD, timer = TimerCmd.DOUBLE};
    Turning -> Driving [signal == TimerSignal.ALERT] / {left = MotorCommand.FORWARD, right = MotorCommand.FORWARD};
  }

  compute IncreaseDistance {
    Integer test = 1;
  }
}
