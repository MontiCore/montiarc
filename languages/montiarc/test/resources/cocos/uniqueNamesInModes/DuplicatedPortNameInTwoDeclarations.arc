/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Invalid, because 'p' exists twice in Mode1
 */
component DuplicatedPortNameInTwoDeclarations {

  component Inner i{}

  mode Mode1 {
    port out int p;
  }

  mode Mode1, Mode2 {
    port out int p;
  }

  mode automaton {
    initial Mode1;
  }
}