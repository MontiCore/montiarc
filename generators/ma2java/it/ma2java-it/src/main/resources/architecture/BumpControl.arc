package architecture;

import robotmodels.time.types.TimerCmd;
import robotmodels.time.types.TimerSignal;
import robotmodels.actuators.types.*;

component BumpControl {

  port
    in Double distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCommand right,
    out MotorCommand left;

}