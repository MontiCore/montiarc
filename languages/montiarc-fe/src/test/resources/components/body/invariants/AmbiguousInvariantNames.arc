package components.body.invariants;

/**
 * Invalid model. Multiple invariants of the same name.
 */
component AmbiguousInvariantNames {
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