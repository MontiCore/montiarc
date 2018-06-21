package components.body.automaton.transition.assignments;

/**
 * Valid model. 
 */
component MethodCallWithoutCallKeyword {

   port 
     in int a,
     out int c;

 
    automaton UseOfUndeclaredField {
        state A;
        initial A;
        
        A -> A /{call c.toString()}; 
    
    }
}