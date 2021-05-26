/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInAutomata;

component AllStatesInitial {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid model, because there is no unique initial state
  automaton {
    initial state A;
    initial state B;
    initial state C;
    initial state D;

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}