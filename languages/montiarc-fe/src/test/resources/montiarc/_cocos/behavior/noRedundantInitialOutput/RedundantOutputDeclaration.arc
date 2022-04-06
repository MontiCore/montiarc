/* (c) https://github.com/MontiCore/monticore */
package behavior.noRedundantInitialOutput;

// Invalid, as two initial output declarations refer to the same state.
component RedundantOutputDeclaration {
  port in int number;

  automaton {
    initial { number = 0; } state Alpha;
    initial { number = 1; } state Alpha;
    state Beta;

    Alpha -> Beta;
  }
}