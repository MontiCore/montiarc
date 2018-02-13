package b;

/*
 * Valid model. (in MontiArc 3)
 */
component DelayedComponentWithTwoPorts {
    
    timing delayed;
    
    port
        in String sIn,
        out String sOut;
}
