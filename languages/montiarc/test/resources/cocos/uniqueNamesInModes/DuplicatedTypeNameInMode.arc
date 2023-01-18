/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Invalid, because Inner exists twice in Mode1. One exists statically and the other one is added by the mode.
 */
component DuplicatedTypeNameInMode {

  component Inner {}

  mode Mode1 {
    component Inner {}
  }

  mode automaton {
    initial Mode1;
  }
}
