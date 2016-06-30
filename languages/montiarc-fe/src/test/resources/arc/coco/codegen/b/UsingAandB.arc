package b;

component UsingAandB {
    port
        in String sIn,
        out String sOut;
        
    component A;
    
    component B;
    
    connect sIn -> b.sIn;
    connect b.sOut -> a.sIn;
    connect a.sOut -> sOut;

}
