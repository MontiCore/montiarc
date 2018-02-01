package components.body.automaton.transition.assignments;

/**
 * Invalid model. Cannot assign value '5' to boolean port;
 */
component AssignmentTypeConflict {

    port 
        out Boolean b;

    automaton {
      state S;
      initial S;
      S -> S / {b = 5}; // Error
    }
}