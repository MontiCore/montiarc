package a;

component UsingCompWithInnerComp {
    
    port 
        in String sIn,
        out String sOut;
        
    component CompWithInnerComp c;
    
    connect sIn -> c.sIn;
    connect c.sOut -> sOut;
}