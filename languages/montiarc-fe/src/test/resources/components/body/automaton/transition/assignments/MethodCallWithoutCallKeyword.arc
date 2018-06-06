package components.body.automaton.transition.assignments;

/**
 * Invalid model. Reads values from inexisting variables x,y.
 *
 * @implements [Wor16] AR1: Names used in guards, valuations, and assignments exist in the automaton. (p. 102, Lst. 5.19)
 */
component MethodCallWithoutCallKeyword {

   port 
     in int a,
     out int c;

 
    automaton UseOfUndeclaredField {
        state A;
        initial A;
        
        A -> A /{call c=5};
    
    }
}