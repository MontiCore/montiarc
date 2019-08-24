/* (c) https://github.com/MontiCore/monticore */
package components.body.invariants;

/**
 * Invalid model.
 * Multiple invariants of the same name.
 *
 * @implements [Hab16] B1: All names of model elements within a component
 *  namespace have to be unique. (p. 59. Lst. 3.31)
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
