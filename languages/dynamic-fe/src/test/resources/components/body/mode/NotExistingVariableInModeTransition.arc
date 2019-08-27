/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 * Uses undeclared variables.
 */
component NotExistingVariableInModeTransition {

  component Adder add;
  component Multiplier mul;

  modeautomaton {

    M1 -> M2 [someVar == 2] / {otherVar = 5};

    initial M1;

    mode M1 {}
    mode M2 {}
  }
}