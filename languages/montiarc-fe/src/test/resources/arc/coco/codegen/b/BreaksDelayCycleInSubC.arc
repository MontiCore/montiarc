package b;

/*
 * Valid model.
 */
component BreaksDelayCycleInSubC {

    port
        in String sIn,
        out String sOut;

    component CompWithStringInputAndOutput a;
    component CompWithTwoStringInputsAndOutput c;
    
    component UsingCompsWithTwoPortsAndWithAndWithoutDelay using;
    
    connect sIn -> c.sIn1;
    connect c.sOut -> a.sIn;
    connect a.sOut -> sOut, using.sIn;
    connect using.sOut -> c.sIn2;
    
}
