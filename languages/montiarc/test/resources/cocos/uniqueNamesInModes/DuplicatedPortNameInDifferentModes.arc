/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Valid, because the 'p' in Mode1 does not interfer with the 'p' in Mode2
 */
component DuplicatedPortNameInDifferentModes {

  component Inner i{}

  mode Mode1 {
    port out int p;
  }

  mode Mode2 {
    port out int p;
  }

  mode automaton {
    initial Mode1;
  }
}
