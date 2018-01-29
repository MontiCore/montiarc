package component.body.automaton.transition;


/**
 * Invalid model. Uses undefined states C, D.
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