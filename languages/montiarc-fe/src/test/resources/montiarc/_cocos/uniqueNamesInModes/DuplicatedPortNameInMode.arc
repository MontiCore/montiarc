/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Invalid, because 'p' exists twice in Mode1. One instance exists always and the other one is added by the mode.
 */
component DuplicatedPortNameInMode {
  port out int p;

  component Inner i{}

  mode Mode1 {
    port out int p;
  }

  mode automaton {
    initial Mode1;
  }
}