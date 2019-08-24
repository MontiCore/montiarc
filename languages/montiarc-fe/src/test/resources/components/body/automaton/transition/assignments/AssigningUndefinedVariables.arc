/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/**
 * Invalid model. Reads values from inexisting variables x,y.
 *
 * @implements [Wor16] AR1: Names used in guards, valuations, and assignments exist in the automaton. (p. 102, Lst. 5.19)
 */
component AssigningUndefinedVariables {

   port 
     in int a,
     out int c;

   int b;
 
    automaton UseOfUndeclaredField {
        state A,B;
        initial A;
    
        A -> B /{x = "Hallo Welt", y = 5}; // x,y undefined
        B -> A [a == 2 && b == 3] / {b = System.currentTimeMillis()};
        A -> A [a == b];
    }
}
