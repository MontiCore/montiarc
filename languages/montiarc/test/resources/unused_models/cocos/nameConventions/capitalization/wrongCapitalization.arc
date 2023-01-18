/* (c) https://github.com/MontiCore/monticore */
package components.body;

/*
 * Invalid model.
 * Multiple capitalization mistakes resulting in unresolvable subcomponents
 *   and ports.
 *
 * Formerly named "k1" in MontiArc3.
 *
 * @implements [Hab16] CV1: Instance names start with a lower-case letter.
 *                            (p.71, Lst. 3.51)
 * @implements [RRW14a] C2: The names of variables and ports start with
                              lowercase letters. (p. 31, Lst. 6.5)
 */
component wrongCapitalization { // ERROR: Component names have to start with a capital letter

  port
    in String BigName, // ERROR: Port names have to start with a lowercase letter
    out String smallName,
    out String out2;


  component components.body.subcomponents._subcomponents.HasStringInputAndOutput BigRef,
      // ERROR: Subcomponent names have to start with a lowercase letter
                             smallRef;

  connect bigName -> bigRef.pIn, smallRef.pIn;
      // ERROR: Can not resolve port 'bigName' of component 'wrongCapitalization'
  connect bigRef.pOut -> smallName;
      // ERROR: Can not resolve component 'bigRef' of component 'wrongCapitalization'
  connect smallRef.pOut -> out2;

  java inv BigLetterInvariant: {
      // ERROR: Invariant names have to start with a lowercase letter
    1 == 1;
  };

  java inv smallLetterInvariant: {
    1 == 1;
  };
}
