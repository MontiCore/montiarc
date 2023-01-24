/* (c) https://github.com/MontiCore/monticore */
package elevator;

component Door {

  port <<sync>> in DoorCMD cmd;
  port <<sync>> in boolean isOpen;
  port <<sync>> in boolean isClosed;
  port <<sync>> in boolean isObstacle;
  port <<sync>> out boolean open;
  port <<sync>> out boolean close;
  port <<sync, delayed>> out boolean closed;

  int timer = 5;

  automaton {
    initial { closed = false; } state Wait;

    Wait -> Wait [timer > 0] / {
      open = false;
      close = false;
      closed = false;
      timer = timer - 1;
    };
    Wait -> CloseDoor [timer == 0] / {
      open = false;
      close = false;
      closed = false;
    };

    state CloseDoor;

    CloseDoor -> CloseDoor [(cmd == null || cmd == DoorCMD.CLOSE)
                            && !isObstacle && !isClosed] / {
      open = false;
      close = true;
      closed = false;
    };
    CloseDoor -> DoorIsClosed [(cmd == null || cmd == DoorCMD.CLOSE)
                               && !isObstacle && isClosed] / {
      open = false;
      close = false;
      closed = true;
    };
    CloseDoor -> OpenDoor [isObstacle] / {
      open = true;
      close = false;
      closed = false;
      timer = 3;
    };
    CloseDoor -> OpenDoor [cmd != null && cmd == DoorCMD.OPEN
                           && !isObstacle] / {
      open = true;
      close = false;
      closed = false;
      timer = 3;
    };

    state DoorIsClosed;

    DoorIsClosed -> DoorIsClosed [cmd == null || cmd == DoorCMD.CLOSE] / {
      open = false;
      close = false;
      closed = true;
    };
    DoorIsClosed -> OpenDoor [cmd != null && cmd == DoorCMD.OPEN] / {
      open = true;
      close = false;
      closed = false;
      timer = 10;
    };

    state OpenDoor;

    OpenDoor -> OpenDoor [!isOpen] / {
      open = true;
      close = false;
      closed = false;
    };
    OpenDoor -> DoorIsOpen [isOpen] / {
      open = false;
      close = false;
      closed = false;
    };

    state DoorIsOpen;

    DoorIsOpen -> DoorIsOpen [timer > 0] / {
      open = false;
      close = false;
      closed = false;
      timer = timer - 1;
    };
    DoorIsOpen -> CloseDoor [timer == 0] / {
      open = false;
      close = false;
      closed = false;
    };
  }
}
