package components.body.automaton.states;

/**
 * Invalid model. The state I is referenced as an initial state, but
 * wasn't defined.
 */
component UndefinedInitialState {
    port 
      in Integer a,
      out Integer b;

    automaton InitialStateNotDefined {    
        state A,B,C,D,E,F,G,H;
        state J,K,L,M;
        state N,O,P;
        
        initial C,E,F,G,H,I; // State I does not exist
        initial A,B,D;
        initial N,O,P;
        
        A->B;
        A->C;
        A->D;
        A->E;
        A->F;
        A->G;
        A->H;
        A->J;
        A->K;
        A->L;
        A->M;
        A->N;
        A->O;
        A->P;
        
    }
}