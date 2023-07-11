/* (c) https://github.com/MontiCore/monticore */
package evaluation.bigModel;

/**
 *
 *
 */
component ElevatorSystem(Integer parameter) {

  port <<sync>> in Boolean btn1, btn2, btn3, btn4,
       <<sync>> out Boolean light1, light2, light3, light4;
  port <<sync>> in Boolean at1, at2, at3, at4,
       <<sync>> out Boolean open, close, up, down,
       <<sync>> in Boolean isOpen, isClosed, isObstacle;

  ControlStation control(parameter);

  btn1 -> control.btn1;
  btn2 -> control.btn2;
  btn3 -> control.btn3;
  btn4 -> control.btn4;

  control.light1 -> light1;
  control.light2 -> light2;
  control.light3 -> light3;
  control.light4 -> light4;

  control.req1 -> elevator.req1;
  control.req2 -> elevator.req2;
  control.req3 -> elevator.req3;
  control.req4 -> elevator.req4;

  Elevator elevator;

  at1 -> elevator.at1;
  at2 -> elevator.at2;
  at3 -> elevator.at3;
  at4 -> elevator.at4;

  elevator.open -> open;
  elevator.close -> close;
  elevator.up -> up;
  elevator.down -> down;

  elevator.clear -> control.clear;

  isOpen -> elevator.isOpen;
  isClosed -> elevator.isClosed;
  isObstacle -> elevator.isObstacle;

}
