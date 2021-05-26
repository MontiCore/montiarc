/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInComposedStates;

component ValidHierarchicalStates {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // valid model
  automaton {
    initial state A {
      initial state AA;
      state AB;
      AA -> AB;
    };
    state B;
    state C;
    state D {
      initial state DA;
      state DB;
      state DC;
      state DD;
      state DE;

      DC -> DB;
      DA -> DC;
    };

    A -> B;
    B -> C;
    C -> D;
    D -> A;
  }
}