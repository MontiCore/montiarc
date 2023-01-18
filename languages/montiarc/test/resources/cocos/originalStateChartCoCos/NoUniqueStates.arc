/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos;

component NoUniqueStates {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed; // <- name "Closed" used twice
    state Locked;
    state Closed { // <- name "Closed" used twice
      initial state SwingOpen;
      state IsOpen;

      SwingOpen -> IsOpen;
    };

    Closed -> Locked        / {System.out.println("Door locked now.");};
    Locked -> Closed [unlock == true];

  }
}
