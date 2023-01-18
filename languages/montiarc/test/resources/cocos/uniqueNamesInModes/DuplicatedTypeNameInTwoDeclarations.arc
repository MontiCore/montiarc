/* (c) https://github.com/MontiCore/monticore */
package uniqueNamesInModes;

/**
 * Invalid, because Inner exists twice in Mode1
 */
component DuplicatedTypeNameInTwoDeclarations {

  component Other {}

  mode Mode1 {
    component Inner {}
  }

  mode Mode1, Mode2 {
    component Inner {}
  }

  mode automaton {
    initial Mode1;
  }
}
