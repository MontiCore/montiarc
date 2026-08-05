package elevator;

import elevator.Commands.MotorCMD;

component Motor {
  port sync in MotorCMD command;
  port sync out double position;

  // Component fields
  double currentPosition = 0;
  double speed = 0.5;

  automaton {
    initial state S;
    S -> S / {
      if (command == MotorCMD.UP) {
        currentPosition = currentPosition + speed;
      } else if (command == MotorCMD.DOWN) {
        currentPosition = currentPosition - speed;
      }

      position = currentPosition;
      System.out.println("Elevator position: " + currentPosition);
    }
  }
}
