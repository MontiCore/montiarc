/* (c) https://github.com/MontiCore/monticore */
package elevator;

component Elevator {

  port <<sync>> in boolean req1, req2, req3, req4,
       <<sync>> out int clear;
  port <<sync>> in boolean at1, at2, at3, at4,
       <<sync>> out boolean open, close, up, down,
       <<sync>> in boolean isOpen, isClosed, isObstacle;

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