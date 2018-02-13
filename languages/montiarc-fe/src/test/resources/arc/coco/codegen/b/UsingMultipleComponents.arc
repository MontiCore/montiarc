package b;

component UsingMultipleComponents {
    
    port
        in String sIn1,
        in String sIn2,
        out String sOut;
        
    component CompWithTwoStringInputsAndOutput c;
    
    component DelayedComponentWithTwoPorts b;
    
    component CompWithStringInputAndOutput a;
    
    
    connect sIn1 -> b.sIn;
    connect sIn2 -> a.sIn;
    connect b.sOut -> c.sIn1;
    connect a.sOut -> c.sIn2;
    connect c.sOut -> sOut;
    
    
}
