package component.body.automaton.transition.assignments;

component AssignmentTypeConflict {

    port 
        out Boolean b;

    automaton AssignmentTypeConflict {
      state S;
      initial S;
        
      S -> S / {b = 5};
    }
}