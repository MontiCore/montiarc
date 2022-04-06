/* (c) https://github.com/MontiCore/monticore */
package behavior.oneInitialStateInAutomata;

component MultipleStatesDeclaredInitial {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid, because both B and C are marked as initial
  automaton {
    state A;
    initial state B;
    initial state C;
    state D;

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}