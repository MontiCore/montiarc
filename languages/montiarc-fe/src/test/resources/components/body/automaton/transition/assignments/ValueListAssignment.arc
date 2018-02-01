package components.body.automaton.transition.assignments;

component ValueListAssignment {

    port 
        out Boolean b;
    
    automaton ValueListAssignment {
      state S;
      initial S;
    
      S -> S / {b = [true, false, true]};
    }
}