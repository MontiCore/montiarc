/* (c) https://github.com/MontiCore/monticore */
package elevator;

/**
 * Adapted from the specification of an elevator control system presented in
 * Strobl, Frank, Alexander Wisspeintner, and A. Marz. "Specification of an
 * elevator control system." Technische Universitat Munchen (1999).
 */
component ElevatorSystem {

  port <<sync>> in boolean btn1, btn2, btn3, btn4,
       <<sync>> out boolean light1, light2, light3, light4;
  port <<sync>> in boolean at1, at2, at3, at4,
       <<sync>> out boolean open, close, up, down,
       <<sync>> in boolean isOpen, isClosed;

  ControlStation control;

  btn1 -> control.btn1;
  btn2 -> control.btn2;
  btn3 -> control.btn3;
  btn4 -> control.btn4;

  control.light1 -> light1;
  control.light2 -> light2;
  control.light3 -> light3;
  control.light4 -> light4;

  Elevator elevator;

  at1 -> elevator.at1;
  at2 -> elevator.at2;
  at3 -> elevator.at3;
  at4 -> elevator.at4;

  elevator.open -> open;
  elevator.close -> close;
  elevator.up -> up;
  elevator.down -> down;

  isOpen -> elevator.isOpen;
  isClosed -> elevator.isClosed;

}
