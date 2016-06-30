package a;

component UniqueConstraint {
    port
        in String sIn;
        
    
    component Inner {
        port
            in String sInInner;
            
        inv invName : {}; // not ambiguous since in other scope
        
        java inv innerInvName : {};
        ocl inv innerInvName : {}; // ambiguous
    
    }
    
    java inv invName : {};
    ocl inv invName : {}; // ambiguous
    
    
}