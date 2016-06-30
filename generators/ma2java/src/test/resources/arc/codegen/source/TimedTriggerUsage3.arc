package source;

component TimedTriggerUsage3 {

    port 
        in String sIn1,
        in String sIn2,
        out Boolean bOut;
        
    component TimedTrigger(5) tt;
    
    component MyInnerC2 myInner1 {
        port 
            in String sIn;
    }
    
    component MyInnerC2 myInner2;
    
    connect tt.trigger -> bOut;
    
    connect sIn1 -> myInner1.sIn;
    connect sIn2 -> myInner2.sIn;    
}