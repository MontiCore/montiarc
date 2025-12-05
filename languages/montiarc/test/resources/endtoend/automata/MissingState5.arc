/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The source and target states 'M1', 'M2', and 'M3' of various
 * transitions are missing (the state symbols cannot be resolved).
 */
component MissingState5 {

  automaton {
    initial state S1 {
      M1 -> M2;
    }

    state S2 {
      M3 -> M4;
      state S3 {
        M5 -> M6;
      }
    }
  }
}
