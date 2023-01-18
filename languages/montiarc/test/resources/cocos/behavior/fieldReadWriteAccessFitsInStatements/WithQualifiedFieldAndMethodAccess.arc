/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// Valid, given that L is resolvable with static field lFieldStatic, static method lMethodStatic, normal field lField,
// normal method lMethod
component WithQualifiedFieldAndMethodAccess(L l) {
  port out int lFieldStatic, lMethodStatic, lField, lMethod;

  int variable = 42;

  automaton {
    initial {
      // The reads in right part of th assignment are not reads of our out ports, but reads on fields of L/l or method
      // invocations. Thus this is valid.
      lFieldStatic = L.fieldStatic;
      lMethodStatic = L.methodStatic();
      lField = l.field;
      lMethod = l.method();
    } state Begin;
    state End;

    Begin -> End / {
      // The reads in right part of th assignment are not reads of our out ports, but reads on fields of L/l or method
      // invocations. Thus this is valid.
      lFieldStatic = L.fieldStatic;
      lMethodStatic = L.methodStatic();
      lField = l.field;
      lMethod = l.method();
    };
  }
}
