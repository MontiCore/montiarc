/* (c) https://github.com/MontiCore/monticore */
package behavior.noRedundantInitialOutput;

// Invalid, as two initial output declarations refer to the same state.
component RedundantOutputDeclaration {
  port in int number;

  automaton {
    state Alpha;
    state Beta;
    initial Alpha / { number = 0; };
    initial Alpha / { number = 1; };

    Alpha -> Beta;
  }
}