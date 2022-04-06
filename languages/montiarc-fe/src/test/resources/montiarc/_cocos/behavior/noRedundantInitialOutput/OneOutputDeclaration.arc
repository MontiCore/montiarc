/* (c) https://github.com/MontiCore/monticore */
package behavior.noRedundantInitialOutput;

// Valid, one initial output declaration is the norm.
component OneOutputDeclaration {
  port out int number;

  automaton {
    initial { number = 0; } state Alpha;
    state Beta;

    Alpha -> Beta;
  }
}