/* (c) https://github.com/MontiCore/monticore */
package behavior.oneInitialStateInAutomata;

component HasInitialState {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // valid model
  automaton {
    state A;
    state B;
    initial state C;
    state D;

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}