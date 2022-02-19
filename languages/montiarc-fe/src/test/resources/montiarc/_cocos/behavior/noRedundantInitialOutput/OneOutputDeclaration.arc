/* (c) https://github.com/MontiCore/monticore */
package behavior.noRedundantInitialOutput;

// Valid, one initial output declaration is the norm.
component OneOutputDeclaration {
  port out int number;

  automaton {
    state Alpha;
    state Beta;
    initial Alpha / { number = 0; };

    Alpha -> Beta;
  }
}