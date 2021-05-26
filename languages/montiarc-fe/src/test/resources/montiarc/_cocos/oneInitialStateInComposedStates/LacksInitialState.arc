/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInComposedStates;

component LacksInitialState {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid model, because there is no initial state in D
  automaton {
    state A;
    state B;
    state C;
    state D {
      state DA;
      state DB;
    };

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}