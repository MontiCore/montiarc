/* (c) https://github.com/MontiCore/monticore */
package speedbumper;

import montiarc.lejos.lib.motor.MotorCmd;
import montiarc.lejos.lib.timer.TimerSignal;
import montiarc.lejos.lib.timer.TimerCmd;
import speedbumper.SpeedCmd;

import java.lang.Integer;
import java.lang.String;

component BumpControl {
  port
    in Integer distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCmd right,
    out MotorCmd left,
    out SpeedCmd speedCmd,
    out String log;

  automaton {
    initial state Idle {
      entry / {
        right = MotorCmd.STOP;
        left = MotorCmd.STOP;
        log = "Idle";
      }
    };
    state Driving;
    state Backing;
    state Turning;

    Idle -> Driving / {
      right = MotorCmd.FORWARD;
      left = MotorCmd.FORWARD;
      speedCmd = SpeedCmd.Dynamic;
      log = "Driving";
    };
    Driving -> Driving [distance >= 5] / {
      right = MotorCmd.FORWARD;
      left = MotorCmd.FORWARD;
      speedCmd = SpeedCmd.Dynamic;
    };
    Driving -> Backing [distance < 5] / {
      right = MotorCmd.BACKWARD;
      left = MotorCmd.BACKWARD;
      timer = TimerCmd.SINGLE;
      log = "Backing";
      speedCmd = SpeedCmd.Static;
    };
    Backing -> Turning [signal == TimerSignal.ALERT] / {
      right = MotorCmd.BACKWARD;
      left = MotorCmd.FORWARD;
      timer = TimerCmd.DOUBLE;
      log = "Turning";
      speedCmd = SpeedCmd.Static;
    };
    Turning -> Driving [signal == TimerSignal.ALERT] / {
      left = MotorCmd.FORWARD;
      right = MotorCmd.FORWARD;
      log = "Driving";
      speedCmd = SpeedCmd.Dynamic;
    };
  }
}