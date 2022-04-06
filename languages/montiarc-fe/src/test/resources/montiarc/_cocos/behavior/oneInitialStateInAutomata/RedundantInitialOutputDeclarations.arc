/* (c) https://github.com/MontiCore/monticore */
package behavior.oneInitialStateInAutomata;

component RedundantInitialOutputDeclarations {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid, because B has multiple initial outputs
  automaton {
    state A;
    initial {ringing = true;} state B;
    initial {ringing = false;} state B;
    state C;
    state D;

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}