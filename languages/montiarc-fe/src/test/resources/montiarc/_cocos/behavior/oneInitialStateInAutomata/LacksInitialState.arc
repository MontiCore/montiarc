/* (c) https://github.com/MontiCore/monticore */
package behavior.oneInitialStateInAutomata;

component LacksInitialState {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid model, because there is no initial state
  automaton {
    state A;
    state B;
    state C;
    state D;

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}