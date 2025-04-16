/* (c) https://github.com/MontiCore/monticore */
package elevator;

component ElevatorSystem {
  Elevator elevator;
  ControlStation control;
  Buttons buttons;
  Motor motor;

  buttons.pressedOnFloor -> control.requestOnFloor;
  control.openDoor -> elevator.openDoor;
  motor.position -> control.motorPosition;
  control.motorCommand -> motor.command;
}