/* (c) https://github.com/MontiCore/monticore */
package elevator;

component Elevator {
  port in boolean openDoor;

  Door door;

  openDoor -> door.shouldOpen;
}
