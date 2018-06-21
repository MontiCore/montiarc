package components.body.automaton.transition.assignments;

/**
 * Valid model.
 */
component MethodCallAfterCallKeyword {

   port 
     in int a,
     out Integer b,
     out Integer c;

 
    automaton UseOfUndeclaredField {
        state A;
        initial A;
        
        A -> A /{b = a.toString()};
    
    }
}