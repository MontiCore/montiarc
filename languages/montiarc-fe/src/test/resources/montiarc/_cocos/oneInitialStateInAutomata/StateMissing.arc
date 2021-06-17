/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInAutomata;

component StateMissing {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid, because state F does not exist
  automaton {
    initial state A;
    state B;
    state C;
    state D;

    initial F / {ringing = false;};

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}