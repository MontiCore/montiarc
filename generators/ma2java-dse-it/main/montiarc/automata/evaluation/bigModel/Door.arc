/* (c) https://github.com/MontiCore/monticore */
package evaluation.bigModel;

import evaluation.Commands.DoorCMD;

component Door {

  port <<sync>> in DoorCMD cmd;
  port <<sync>> in Boolean isOpen;
  port <<sync>> in Boolean isClosed;
  port <<sync>> in Boolean isObstacle;
  port <<sync>> out Boolean open;
  port <<sync>> out Boolean close;
  port <<sync, delayed>> out Boolean closed;

  Double timer = 5.0;

  automaton {
    initial { closed = false; } state Wait;

    Wait -> Wait [timer >= 0.625] / {
      open = false;
      close = false;
      closed = false;
      timer = timer * 0.5;
    };
    Wait -> CloseDoor [timer <= 0.625] / {
      open = false;
      close = false;
      closed = false;
    };

    state CloseDoor;

    CloseDoor -> CloseDoor [(cmd == DoorCMD.NULL || cmd == DoorCMD.CLOSE)
                            && !isObstacle && !isClosed] / {
      open = false;
      close = true;
      closed = false;
    };
    CloseDoor -> DoorIsClosed [(cmd == DoorCMD.NULL || cmd == DoorCMD.CLOSE)
                               && !isObstacle && isClosed] / {
      open = false;
      close = false;
      closed = true;
    };
    CloseDoor -> OpenDoor [isObstacle == true] / {
      open = true;
      close = false;
      closed = false;
      timer = 3.0;
    };
    CloseDoor -> OpenDoor [cmd != DoorCMD.NULL && cmd == DoorCMD.OPEN
                           && !isObstacle] / {
      open = true;
      close = false;
      closed = false;
      timer = 3.0;
    };

    state DoorIsClosed;

    DoorIsClosed -> DoorIsClosed [cmd == DoorCMD.NULL || cmd == DoorCMD.CLOSE] / {
      open = false;
      close = false;
      closed = true;
    };
    DoorIsClosed -> OpenDoor [cmd != DoorCMD.NULL && cmd == DoorCMD.OPEN] / {
      open = true;
      close = false;
      closed = false;
      timer = 10.0;
    };

    state OpenDoor;

    OpenDoor -> OpenDoor [!isOpen] / {
      open = true;
      close = false;
      closed = false;
    };
    OpenDoor -> DoorIsOpen [isOpen == true] / {
      open = false;
      close = false;
      closed = false;
    };

    state DoorIsOpen;

    DoorIsOpen -> DoorIsOpen [timer >= 0.625] / {
      open = false;
      close = false;
      closed = false;
      timer = timer * 0.5;
    };
    DoorIsOpen -> CloseDoor [timer <= 0.625] / {
      open = false;
      close = false;
      closed = false;
    };
  }
}
