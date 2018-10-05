package components.body.automaton.transition.assignments;

/**
 * Invalid model.
 *
 * @implements No literature reference
 */
component MethodCallWithoutCallKeyword {

   port 
     in int a,
     out int c;

 
    automaton UseOfUndeclaredField {
        state A;
        initial A;
        
        A -> A /{call c=5}; //wrong: there must be a method call after call keyword
    
    }
}