package component.body.automaton.transition.assignments;

/**
 * Invalid model. Reads values from inexisting variables x,y.
 */
component AssigningUndefinedVariables {

   port 
     in int a,
     out int c;

   int b;
 
    automaton UseOfUndeclaredField {
        state A,B;
        initial A;
    
        A -> B {x == "Hallo Welt", y == 5}; // x,y undefined
        B -> A {a == 2, b == 3};
        A -> A {a == b};
    }
}