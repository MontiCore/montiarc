/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos;

component TransitionSourceMissing {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Locked;
    state Opened {
      initial state SwingOpen;
      state IsOpen;

      SwingOpen -> IsOpen;
    };

    DoorMissing -> Opened; // source state not defined
    Closed -> Opened [open] / {ringing = true;};
    Closed -> Locked        / {System.out.println("Door locked now.");};
    Locked -> Closed [unlock == true];
  }
}
