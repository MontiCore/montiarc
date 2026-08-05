package elevator;

component ElevatorSystem {
  port in int pressedOnFloor;

  Elevator elevator;
  ControlStation control;
  Motor motor;

  pressedOnFloor -> control.requestOnFloor;
  control.openDoor -> elevator.openDoor;
  motor.position -> control.motorPosition;
  control.motorCommand -> motor.command;
}