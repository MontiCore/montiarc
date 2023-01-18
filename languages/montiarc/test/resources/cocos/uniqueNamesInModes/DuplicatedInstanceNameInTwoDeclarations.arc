/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Invalid, because 'i' exists twice in Mode1
 */
component DuplicatedInstanceNameInTwoDeclarations {

  component Inner {}

  mode Mode1 {
    Inner i;
  }

  mode Mode1, Mode2 {
    Inner i;
  }

  mode automaton {
    initial Mode1;
  }
}
