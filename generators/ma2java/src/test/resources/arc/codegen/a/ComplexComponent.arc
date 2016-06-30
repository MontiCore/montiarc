package a;

/**
 * This is a component comment that has to occur in the generated class and interface.
 *
 */
component ComplexComponent<K, V extends Number>(String parStr, Integer parInt) {
    
    port
        /** This is a comment for port strIn1. */
        in String strIn1,
        in K kIn,
        out String strOut1,
        /** This is a comment for port vOut. */
        out V vOut;
}