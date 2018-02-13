package b;

component UsingCompWithStringInputAndOutputAndDelayedComponentWithTwoPorts {
    port
        in String sIn,
        out String sOut;
        
    component CompWithStringInputAndOutput a;
    
    component DelayedComponentWithTwoPorts b;
    
    connect sIn -> b.sIn;
    connect b.sOut -> a.sIn;
    connect a.sOut -> sOut;

}
