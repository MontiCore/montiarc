package source;

component TimedTriggerUsage1 {

    port
        out Boolean bOut1,
        out Boolean bOut2;
        
    component TimedTrigger(5) tt1;
    component TimedTrigger(3) tt2;
    
    connect tt1.trigger -> bOut1;
    connect tt2.trigger -> bOut2;
}