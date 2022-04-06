/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

import montiarc.lejos.lib.motor.MotorCmd;
import montiarc.lejos.lib.timer.TimerSignal;
import montiarc.lejos.lib.timer.TimerCmd;

component BumpControl {
  port
    in Integer distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCmd right,
    out MotorCmd left,
    out Integer speed,
    out String log;

  automaton {
    initial {
      right = MotorCmd.STOP;
      left = MotorCmd.STOP;
      log = "Idle";
    } state Idle;

    state Driving;
    state Backing;
    state Turning;



    Idle -> Driving / {
      right = MotorCmd.FORWARD;
      left = MotorCmd.FORWARD;
      speed = 30;
      log = "Driving";
    };
    Driving -> Backing [distance < 5] / {
      right = MotorCmd.BACKWARD;
      left = MotorCmd.BACKWARD;
      timer = TimerCmd.SINGLE;
      log = "Backing";
    };
    Backing -> Turning [signal == TimerSignal.ALERT] / {
      right = MotorCmd.BACKWARD;
      left = MotorCmd.FORWARD;
      timer = TimerCmd.DOUBLE;
      log = "Turning";
    };
    Turning -> Driving [signal == TimerSignal.ALERT] / {
      left = MotorCmd.FORWARD;
      right = MotorCmd.FORWARD;
      log = "Driving";
    };
  }
}