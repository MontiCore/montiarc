/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Valid, because the 'i' in Mode1 does not interfer with the 'i' in Mode2
 */
component DuplicatedInstanceNameInDifferentModes {

  component Inner {}

  mode Mode1 {
    Inner i;
  }

  mode Mode2 {
    Inner i;
  }

  mode automaton {
    initial Mode1;
  }
}