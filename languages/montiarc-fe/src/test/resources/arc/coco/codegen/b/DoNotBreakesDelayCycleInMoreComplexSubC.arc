package b;

component DoNotBreakesDelayCycleInMoreComplexSubC {

    port
        in String sIn,
        out String sOut;

    component A a1, a2;
    component C;
    
    component UsingAandCandB;
    
    connect sIn -> c.sIn1;
    connect c.sOut -> a1.sIn;
    connect a1.sOut -> a2.sIn, usingAandCandB.sIn1;
    connect a2.sOut -> usingAandCandB.sIn2, sOut;
    
    connect usingAandCandB.sOut -> c.sIn2;
    
}
