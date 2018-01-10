package invalid;

component UseOfUndefinedStates {

    port 
    	in Integer a,
    	out Integer b;
    


automaton UseOfUndefStates{

    state A,B;
    
    initial A;
    
    A->B;
    A->C;
    D->B;
    C->D;
    D;

}
}