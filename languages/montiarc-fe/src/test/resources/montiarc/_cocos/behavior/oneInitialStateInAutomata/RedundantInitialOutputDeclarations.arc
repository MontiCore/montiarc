/* (c) https://github.com/MontiCore/monticore */
package behavior.oneInitialStateInAutomata;

component RedundantInitialOutputDeclarations {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid, because B has multiple initial outputs
  automaton {
    state A;
    state B;
    state C;
    state D;

    initial B / {ringing = true;};
    initial B / {ringing = false;};

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}