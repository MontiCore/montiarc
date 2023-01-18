/* (c) https://github.com/MontiCore/monticore */
package components;

import components.body.subcomponents._subcomponents.package1.ValidComponentInPackage1;

/*
 * Invalid model.
 * @implements [Wor16] AU3: The names of all inputs, outputs, and variables
 * are unique. (p. 98. Lst. 5.10)
 * @implements [Hab16] B1: All names of model elements within a component
 * namespace have to be unique. (p. 59. Lst. 3.31)
 * @implementst [Wor16] MU1: The name of each component variable is unique
 *  among ports, variables, and configuration parameters. (p.54, Lst. 4.5)
 */
component UniqueNamesDifferentSymboltypes<myName>(int myName) {
  // Error: Ambiguous name

    port
        in String myName, // Error: Ambiguous name
        out String sOut;

    component ValidComponentInPackage1 myName; // Error: Ambiguous name

    connect myName -> myName.stringIn;
      // Autocompleting is not possible due to the ambiguous names
    connect myName.stringOut -> sOut;

    java inv myName: { // Error: Ambiguous name
        assert x==1;
    };

}
