package b;

component UsingAandCandB {
    
    port
        in String sIn1,
        in String sIn2,
        out String sOut;
        
    component C;
    
    component B;
    
    component A;
    
    
    connect sIn1 -> b.sIn;
    connect sIn2 -> a.sIn;
    connect b.sOut -> c.sIn1;
    connect a.sOut -> c.sIn2;
    connect c.sOut -> sOut;
    
    
}
