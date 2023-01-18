/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Invalid, because 'i' exists twice in Mode1. One instance exists always and the other one is added by the mode.
 */
component DuplicatedInstanceNameInMode {

  component Inner {}

  Inner i;

  mode Mode1 {
    Inner i;
  }

  mode automaton {
    initial Mode1;
  }
}
