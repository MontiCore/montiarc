/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

import L.lMethodStatic;

// Valid, given that L is resolvable with static field int lFieldStatic, static method int lMethodStatic, normal field
// int lField, normal method int lMethod
component WithImportedMethodAccess(L parameter) {
  port out int lMethodStatic;

  automaton {
    state Begin;
    state End;
    initial Begin / {
      // The right part of the assignment is not a read of our port, but an method invocation on L.
      // Thus this is valid.
      lMethodStatic = lMethodStatic();
    };

    Begin -> End / {
      // The right part of the assignment is not a read of our port, but an method invocation on L.
      // Thus this is valid.
      lMethodStatic = lMethodStatic();
    };
  }
}