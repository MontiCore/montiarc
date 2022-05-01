/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Valid, because the type 'Inner' in Mode1 does not interfer with the type in Mode2
 */
component DuplicatedTypeNameInDifferentModes {

  component Other {}

  mode Mode1 {
    component Inner {}
  }

  mode Mode2 {
    component Inner {}
  }

  mode automaton {
    initial Mode1;
  }
}