/* (c) https://github.com/MontiCore/monticore */
package behavior.oneInitialStateInAutomata;

component MultipleInitialOutputDeclarations {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid, because both B and C are marked as initial
  automaton {
    state A;
    state B;
    state C;
    state D;

    initial B / {ringing = false;};
    initial C / {ringing = false;};

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}