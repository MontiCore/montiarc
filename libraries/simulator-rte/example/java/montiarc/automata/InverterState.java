/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.automaton.State;

enum InverterState implements State { // State as factory-esque stuff?
  S {
    @Override
    public InverterState entryAction(Inverter context) {
      System.out.println("S Entry Action");
      // modify context here if there is anything to be done
      return this; // return a sub-state if this is hierarchic
    }

    @Override
    public void exitAction(Inverter context) {
      System.out.println("S Exit Action");
      // modify context here if there is anything to be done
    }
  };

  abstract void exitAction(Inverter context);

  abstract InverterState entryAction(Inverter context);

}
