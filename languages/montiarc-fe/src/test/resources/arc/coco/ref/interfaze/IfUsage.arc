package interfaze;

component IfUsage {
    
    component IfProvider p1, p2;
    
    component IfRequired r1, r2;
    
    // all same type
    connect p1.implOut -> r1.implIn;
    connect p1.ifOut -> r1.ifIn;
    
    // correct
    connect p2.implOut -> r2.ifIn;
    // invalid
    connect p2.ifOut -> r2.implIn;

}
