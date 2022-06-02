/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition;

/**
 * Invalid model.
 * Uses undefined states C, D.
 *
 * @implements [Wor16] AR3: Used states exist (p. 104. Lst. 5.21)
 */
component UsingUndefinedStates {
    port 
    	in Integer a,
    	out Integer b;

    automaton UseOfUndefStates {
        state A,B;
        initial A;
        
        A->B;
        A->C;
        D->B;
        C->D;
        D;
    }
}
