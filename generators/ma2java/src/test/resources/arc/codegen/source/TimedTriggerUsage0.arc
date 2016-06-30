package source;

component TimedTriggerUsage0 {

    port
        out Boolean bOut;
        
    component TimedTrigger(5) tt;
    
    connect tt.trigger -> bOut;
}