/* (c) https://github.com/MontiCore/monticore */
package evaluation.bigModel;

component Elevator {

  port <<sync>> in Boolean req1, req2, req3, req4,
       <<sync>> out Integer clear;
  port <<sync>> in Boolean at1, at2, at3, at4,
       <<sync>> out Boolean open, close, up, down,
       <<sync>> in Boolean isOpen, isClosed, isObstacle;

  Controller ctrl;

  req1 -> ctrl.req1;
  req2 -> ctrl.req2;
  req3 -> ctrl.req3;
  req4 -> ctrl.req4;

  ctrl.clear -> clear;

  at1 -> ctrl.at1;
  at2 -> ctrl.at2;
  at3 -> ctrl.at3;
  at4 -> ctrl.at4;

  isOpen -> door.isOpen;
  isClosed -> door.isClosed;
  isObstacle -> door.isObstacle;

  Door door;

  door.open -> open;
  door.close -> close;
  door.closed -> ctrl.isClosed;

  Lift lift;

  lift.up -> up;
  lift.down -> down;

  ctrl.door -> door.cmd;
  ctrl.lift -> lift.cmd;

}
