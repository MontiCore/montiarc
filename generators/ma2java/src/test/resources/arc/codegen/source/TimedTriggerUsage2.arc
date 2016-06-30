package source;

component TimedTriggerUsage2 {

    port 
        in String sIn,
        out Boolean bOut;
        
    component TimedTrigger(5) tt;
    
    component MyInnerC myInner {
        port 
            in String sIn;
    }
    
    connect tt.trigger -> bOut;
    
    connect sIn -> myInner.sIn;    
}