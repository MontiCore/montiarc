/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

// Valid, given that L is resolvable with static field lFieldStatic, static method lMethodStatic, normal field lField,
// normal method lMethod
component WithQualifiedFieldAndMethodAccess(L l) {
  port out int lFieldStatic, lMethodStatic, lField, lMethod;

  int variable = 42;

  automaton {
    initial
      { lFieldStatic = 0; lMethodStatic = 0; lField = 0; lMethod = 0; }
      state Begin;
    state End;

    // The reads in the guard are not reads of our out ports, but reads on fields of L/l or method invocations.
    // Thus this is valid.
    Begin -> End [L.lFieldStatic > 100 && L.lMethodStatic() > 100 && l.lField > 100 && l.lMethod() > 100] / {
      lFieldStatic = 0;
      lMethodStatic = 0;
      lField = 0;
      lMethod = 0;
    };
  }
}