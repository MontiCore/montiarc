package components.body.invariants;

/*
 * Invalid model.
 * Identifiers for invariants are ambiguous.
 *
 * Formerly named "E4" in MontiArc3.
 *
 * @implements [Hab16] B1: All names of model elements within a component
                            namespace have to be unique. (p. 59. Lst. 3.31)
 * TODO Add test
 */
component AmbiguousInvariantNames2 {

  java inv duplicateInvariant: {
      //ERROR: Invariant name 'duplicateInvariant' is ambiguous!
    1 == 1;
  };
  
  java inv duplicateInvariant: {
      //ERROR: Invariant name 'duplicateInvariant' is ambiguous!
    2 == 2;
  };
  
  java inv uniqueInvariant: {
    3 == 3;
  };
}