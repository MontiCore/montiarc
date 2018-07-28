package components.body.automaton.transition.assignments;

/**
 * Valid model. 
 */
component MethodCallWithCallKeyword {

   port 
     in String a,
     out int c;

 
    automaton UseOfUndeclaredField {
        state A;
        initial A;
        
        A -> A /{call c.toString()}; 
    
    }
}