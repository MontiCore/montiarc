/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

import L.lMethodStatic;

// Valid, given that L is resolvable with static field int lFieldStatic, static method int lMethodStatic, normal field
// int lField, normal method int lMethod
component WithImportedMethodAccess(L parameter) {
  port out int lMethodStatic;

  automaton {
    state Begin;
    state End;
    initial Begin / { lMethodStatic = 0; };

    // The read in the guard is not a read of our port, but an method invocation on L.
    // Thus this is valid.
    Begin -> End [lMethodStatic() > 2] / {
      lMethodStatic = 1;
    };
  }
}