package elevator;

component Elevator {
  port in boolean openDoor;

  Door door;

  openDoor -> door.shouldOpen;
}