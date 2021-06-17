/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInAutomata;

component StateDeclaredInitialTwice {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // valid model, because although "initial" appears twice,
  // it references the same state
  automaton {
    state A;
    state B;
    initial state C;
    state D;

    initial C / {ringing = false;};

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}