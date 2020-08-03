/* (c) https://github.com/MontiCore/monticore */
package speedbumper;

import de.montiarcautomaton.lejos.lib.motor.Datatypes.MotorCmd;
import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerSignal;
import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerCmd;
import speedbumper.Datatypes.SpeedCmd;

component BumpControl {
  port
    in Integer distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCmd right,
    out MotorCmd left,
    out SpeedCmd speedCmd,
    out String log;

  /*automaton BumpControl {
    state Idle, Driving, Backing, Turning;

    initial Idle / {right = MotorCmd.STOP, left = MotorCmd.STOP, log = "Idle"};

    Idle -> Driving / {right = MotorCmd.FORWARD, left = MotorCmd.FORWARD, speedCmd = SpeedCmd.Dynamic, log = "Driving"};
    Driving -> Driving [distance >= 5] / {right = MotorCmd.FORWARD, left = MotorCmd.FORWARD, speedCmd = SpeedCmd.Dynamic};
    Driving -> Backing [distance < 5] / {right = MotorCmd.BACKWARD, left = MotorCmd.BACKWARD, timer = TimerCmd.SINGLE, log = "Backing", speedCmd = SpeedCmd.Static};
    Backing -> Turning [signal == TimerSignal.ALERT] / {right = MotorCmd.BACKWARD, left = MotorCmd.FORWARD, timer = TimerCmd.DOUBLE, log = "Turning", speedCmd = SpeedCmd.Static};
    Turning -> Driving [signal == TimerSignal.ALERT] / {left = MotorCmd.FORWARD, right = MotorCmd.FORWARD, log = "Driving", speedCmd = SpeedCmd.Dynamic};
  }*/
}