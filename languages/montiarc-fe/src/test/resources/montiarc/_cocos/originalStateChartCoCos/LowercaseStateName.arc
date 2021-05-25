/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos;

component LowercaseStateName {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  statechart Door {
    initial state Closed;
    state Locked;
    state opened { // <- lowercase name here
      initial state SwingOpen;
      state IsOpen;

      SwingOpen -> IsOpen;
    };

    opened -> Closed;
    Closed -> opened [open] / {ringing = true;};
    Closed -> Locked        / {System.out.println("Door locked now.");};
    Locked -> Closed [unlock == true];

  }
}