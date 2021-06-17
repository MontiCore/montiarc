/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInAutomata;

component StateDeclaredInitialSeparately {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // valid model, because although no state has an "initial"-modifier,
  // there is an initial state: C is marked as such separately
  automaton {
    state A;
    state B;
    state C;
    state D;

    initial C / {ringing = false;};

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}