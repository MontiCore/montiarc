/* (c) https://github.com/MontiCore/monticore */
package behavior.oneInitialStateInAutomata;

component StateDeclaredInitialTwice {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    state A;
    state B;
    initial {ringing = false;} state C;
    state D;

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}