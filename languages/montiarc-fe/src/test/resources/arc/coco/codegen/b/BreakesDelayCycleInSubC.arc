package b;

component BreakesDelayCycleInSubC {

    port
        in String sIn,
        out String sOut;

    component A;
    component C;
    
    component UsingAandB;
    
    connect sIn -> c.sIn1;
    connect c.sOut -> a.sIn;
    connect a.sOut -> sOut, usingAandB.sIn;
    connect usingAandB.sOut -> c.sIn2;
    
}
