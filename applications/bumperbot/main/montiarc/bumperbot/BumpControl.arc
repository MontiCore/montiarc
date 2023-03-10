/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

import bumperbot.Datatypes.*;

component BumpControl {
  port
    <<sync>> in Integer distance,
    <<sync>> in TimerSignal signal,
    <<sync>> out TimerCmd timer,
    <<sync>> out MotorCmd right,
    <<sync>> out MotorCmd left,
    <<sync>> out Integer speed,
    <<sync>> out String log;

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
