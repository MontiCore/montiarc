package components.body.automaton.transition.guards;

/*
 * Invalid model.
 * Uses undeclared fields in guards of transitions.
 *
 * TODO Literature
 */
component GuardUsesUndeclaredField{

  automaton Test{

    state Initial, Second, Third;
    initial Initial;

    Initial -> Second [intVariable == 0];
      // Error: intVariable is not declared
    Second -> Third [undeclaredList.get(0).equals(anotherUndeclVar)];
      // Error: undeclaredList ist not declared
      // Error: anotherUndeclVar is not declared
  }
}