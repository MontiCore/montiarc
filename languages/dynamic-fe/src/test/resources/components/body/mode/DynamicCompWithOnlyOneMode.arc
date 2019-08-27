/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 * Dynamic components should have at least two modes.
 * 1 Error
 */
component DynamicCompWithOnlyOneMode {

  component Adder add;

  modeautomaton {
    mode Mode1 {}

    initial Mode1;

    Mode1 -> Mode1;
  }
}