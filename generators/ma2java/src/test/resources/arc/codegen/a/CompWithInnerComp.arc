package a;

component CompWithInnerComp {
    
    port 
        in String sIn,
        out String sOut;
        
    component Inner i {
        port 
        in String sIn,
        out String sOut;
    }
    
    connect sIn -> i.sIn;
    connect i.sOut -> sOut;

}