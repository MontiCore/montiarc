package b;

component BreakesDelayCycleInMoreComplexSubC {

    port
        in String sIn,
        out String sOut;

    component A a1, a2;
    component C;
    
    component UsingCandB;
    
    connect sIn -> c.sIn1;
    connect c.sOut -> a1.sIn;
    connect a1.sOut -> a2.sIn, usingCandB.sIn1;
    connect a2.sOut -> usingCandB.sIn2, sOut;
    
    connect usingCandB.sOut -> c.sIn2;
    
}
