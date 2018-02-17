package b;

/*
 * Valid model.
 * Formerly named 'B'
 */
component DelayedComponentWithTwoPorts {
    
    timing delayed;
    
    port
        in String sIn,
        out String sOut;
}
